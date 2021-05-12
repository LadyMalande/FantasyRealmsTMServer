package interactive;

import artificialintelligence.ScoreCounterForAI;
import artificialintelligence.State;
import bonuses.Bonus;
import bonuses.BonusOrBonus;
import bonuses.PlusForSameColorCards;
import bonuses.PlusIfTypesAreUnique;
import maluses.*;
import server.*;

import java.util.*;

public class CopyNameAndType extends Interactive {
    public int priority = 3;
    public String text;
    public ArrayList<Type> types;
    private int thiscardid;

    public CopyNameAndType( int id, ArrayList<Type> types) {
        this.text = "Copy name and type of any card of these types: " + giveListOfTypesWithSeparator(types, " or ");
        this.types = types;
        this.thiscardid = id;
        //System.out.println("Card INIT: Text: " + getText());
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("interactive.CardInteractives",loc);
        ResourceBundle typ = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(rb.getString("copyNameAndType"));
        sb.append(typ.getString("any2" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types, "or", locale, 2));

        sb.append(rb.getString("copyNameAndType2"));
        return sb.toString();
    }

    @Override
    public boolean askPlayer(ClientHandler client) {
        ArrayList<String> choices = new ArrayList<>();
        choices = ArrayListCreator.createListOfNamesFromTypes(types, client.locale);
        String toSend = giveStringOfStringsWithSeparator(choices, ",");
        return client.sendInteractive("CopyNameAndType#" + thiscardid + "#" + toSend);
    }


    @Override
    public int changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        int processors = Runtime.getRuntime().availableProcessors();
        //
        // System.out.println("CPU cores: " + processors);
        long startTime = System.nanoTime();
        //System.out.println("Counting CopyNameAndType");
        Type bestTypeToChangeInto = null;
        String bestNameToChangeInto = null;
        Card thisCard = null;


        // Remove this interactive from card to not get stuck in loop
        for (Card original : originalHand) {
            if (thiscardid == original.id) {
                thisCard = original;

                original.interactives.remove(this);
            }
            //System.out.print(original.name + ", ");
        }
        //System.out.println();

        //Copy hand for computing the original score on Hand
        List<Card> newHandOldScore = new ArrayList<>();
        for (Card copy : originalHand) {
            ArrayList<Malus> maluses = new ArrayList<>();
            ArrayList<Interactive> interactives = new ArrayList<>();
            // bonuses are never deleted, they can be the same objects
            // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
            if (copy.maluses != null) {
                //System.out.println("Klonuji kartu " + copy.name);
                for (Malus m : copy.maluses) {
                    maluses.add(m.clone());
                }
            }
            // The same rule apply to interactives = they can be deleted to signal it has been used
            if (copy.interactives != null) {
                for (Interactive inter : copy.interactives) {
                    interactives.add(inter.clone());
                }
            }
            newHandOldScore.add(new Card(copy.id, copy.name, copy.strength, copy.type, copy.bonuses, maluses, interactives));
        }

        //Count the score of the hand without this interactive to get the base best score
        ScoreCounterForAI sc = new ScoreCounterForAI();
        int bestScore = sc.countScore(newHandOldScore, cardsOnTable, true);

        //System.out.println("Best score form original hand: " + bestScore);
        // Try out different changes and find out the actual best possible score
        ArrayList<ScoreCounterCopyNameAndTypeThread> threads = new ArrayList<>();
        for(Type type : types){
            ScoreCounterCopyNameAndTypeThread t = new ScoreCounterCopyNameAndTypeThread(newHandOldScore,cardsOnTable,type,thiscardid);
            threads.add(t);
            t.start();
        }

        for(ScoreCounterCopyNameAndTypeThread thread : threads){
            try {
                thread.join();
                //System.out.println("Syncing thread of type " + thread.typeToTry);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(ScoreCounterCopyNameAndTypeThread thread : threads){
            int best = thread.getBestScore();
            if(best > bestScore){
                bestScore = best;
                bestNameToChangeInto = thread.getBestName();
                bestTypeToChangeInto = thread.getTypeToTry();
            }
        }


        if(bestTypeToChangeInto != null){
            thisCard.type = bestTypeToChangeInto;
            thisCard.name = bestNameToChangeInto;
            //System.out.println("Changed the card to be " + thisCard.name + " [" + thisCard.type + "]");
        }

        //long elapsedTime = System.nanoTime() - startTime;
        //System.out.println("Total execution time spent in CopyNameAndType in millis: " + elapsedTime/1000000);
        /*
        for(Card c : originalHand){
            System.out.print(c.name + ", ");
        }
        System.out.println();
*/


        return bestScore;
    }
    /*
    @Override
    public void changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {

        long startTime = System.nanoTime();
        //System.out.println("Counting CopyNameAndType");
        Type bestTypeToChangeInto = null;
        String bestNameToChangeInto = null;
        Card thisCard = null;

        // Remove this interactive from card to not get stuck in loop
        for (Card original : originalHand) {
            if (thiscardid == original.id) {
                thisCard = original;
                original.interactives.remove(this);
            }
        }

        //Copy hand for computing the original score on Hand
        List<Card> newHandOldScore = new ArrayList<>();
        for (Card copy : originalHand) {
            ArrayList<Malus> maluses = new ArrayList<>();
            ArrayList<Interactive> interactives = new ArrayList<>();
            // bonuses are never deleted, they can be the same objects
            // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
            if (copy.maluses != null) {
                //System.out.println("Klonuji kartu " + copy.name);
                for (Malus m : copy.maluses) {
                    maluses.add(m.clone());
                }
            }
            // The same rule apply to interactives = they can be deleted to signal it has been used
            if (copy.interactives != null) {
                for (Interactive inter : copy.interactives) {
                    interactives.add(inter.clone());
                }
            }
            newHandOldScore.add(new Card(copy.id, copy.name, copy.strength, copy.type, copy.bonuses, maluses, interactives));
        }

        //Count the score of the hand without this interactive to get the base best score
        ScoreCounterForAI sc = new ScoreCounterForAI();
        int bestScore = sc.countScore(newHandOldScore, cardsOnTable);

        // Try out different changes and find out the actual best possible score
        ArrayList<String> namesToTry = null;
        for (Type type : types) {
            //Get the names according to type the card can change into
            namesToTry = server.BigSwitches.switchTypeForNames(type);
            for (String nameToTry : namesToTry) {
                List<Card> newHand = new ArrayList<>();
                for (Card copy : originalHand) {
                    ArrayList<Malus> maluses = new ArrayList<>();
                    ArrayList<Interactive> interactives = new ArrayList<>();
                    // bonuses are never deleted, they can be the same objects
                    // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
                    if (copy.maluses != null) {
                        //System.out.println("Klonuji kartu " + copy.name);
                        for (Malus m : copy.maluses) {
                            maluses.add(m.clone());
                        }
                    }
                    // The same rule apply to interactives = they can be deleted to signal it has been used
                    if (copy.interactives != null) {
                        for (Interactive inter : copy.interactives) {
                            interactives.add(inter.clone());
                        }
                    }
                    if(thiscardid == copy.id){
                        newHand.add(new Card(copy.id, nameToTry, copy.strength, type, copy.bonuses, maluses, interactives));
                    } else{
                        newHand.add(new Card(copy.id, copy.name, copy.strength, copy.type, copy.bonuses, maluses, interactives));
                    }

                }
                
                int currentScore = sc.countScore(newHand, cardsOnTable);
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                    bestTypeToChangeInto = type;
                    bestNameToChangeInto = nameToTry;
                }
                //System.out.println("After trying out changing into " + thisCard.name + " [" + thisCard.type + "] the score IS " + currentScore);
            }

        }

        if(bestTypeToChangeInto != null){
            thisCard.type = bestTypeToChangeInto;
            thisCard.name = bestNameToChangeInto;
            //System.out.println("Changed the card to be " + thisCard.name + " [" + thisCard.type + "]");
        }

        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("Total execution time spent in CopyNameAndType in millis: " + elapsedTime/1000000);


    }


     */


    public void changeHandWithInteractiveNoRecursion(){

    }

    private Map<Type, Integer> getReactions(ArrayList<Card> hand){
        ArrayList<Card> inspectCards = cardsThatReactWithThisOne(hand);
        Map<Type, Integer> reactions = new HashMap<>();
        for(Type t : types){
            reactions.put(t, 0);
        }
        for(Card c : inspectCards){
            for(Type t : types){
                for(Bonus b : c.bonuses){
                    int newValue = reactions.get(t) +  b.getReaction(t, hand);
                    reactions.put(t, newValue);
                }
                for(Malus m : c.maluses){
                    int newValue = reactions.get(t) +  m.getReaction(t, hand);
                    reactions.put(t, newValue);
                }
            }

        }

        return reactions;
    }



    private ArrayList<Card> cardsThatReactWithThisOne(ArrayList<Card> hand){
        ArrayList<Card> cardsThatReact = new ArrayList<>();
        for(Card c : hand){
            for(Bonus b : c.bonuses){
                if(b instanceof BonusOrBonus && ((BonusOrBonus)b).reactsWithTypes(types)){
                    cardsThatReact.add(c);
                    break;
                } else if(b instanceof PlusForSameColorCards){
                    if(((PlusForSameColorCards)b).reactsWithTypes(types, hand)){
                        cardsThatReact.add(c);
                        break;
                    }
                } else if(b instanceof PlusIfTypesAreUnique){
                    if(((PlusIfTypesAreUnique)b).reactsWithTypes(types, hand)){
                        cardsThatReact.add(c);
                        break;
                    }
                }
                else if(b.reactsWithTypes(types)){
                    cardsThatReact.add(c);
                    break;
                }
            }
            for(Malus m : c.maluses){
                if(m.reactsWithTypes(types)){
                    cardsThatReact.add(c);
                    break;
                }
            }
        }

        return cardsThatReact;
    }

    private ArrayList<Integer> willBeWiped(ArrayList<Card> hand){
        ArrayList<Integer> wiped = new ArrayList<>();
        for(Card c: hand) {
            for (Malus m : c.getMaluses()) {
                if (m instanceof DeletesAllExceptTypeOrCard || m instanceof DeletesAllType ||
                        m instanceof DeletesAllTypeExceptCard || m instanceof DeletesAllTypeOrOtherSelftype) {
                    wiped.addAll(m.returnWillBeDeleted());
                }
            }
        }
        return wiped;
    }

    @Override
    public Interactive clone() throws CloneNotSupportedException{
        CopyNameAndType newi = (CopyNameAndType)super.clone();
        newi.priority = this.priority;
        newi.text = this.text;
        newi.types = (ArrayList<Type>) this.types.clone();
        newi.thiscardid = this.thiscardid;
        return newi;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        // TODO
        return potential;
    }
}
