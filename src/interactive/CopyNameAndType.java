package interactive;

import artificialintelligence.ScoreCounterForAI;
import artificialintelligence.State;
import maluses.Malus;
import server.Card;
import server.ClientHandler;
import server.Server;
import server.Type;
import util.ArrayListCreator;
import util.BigSwitches;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The interactive bonus that implements the possibility of renaming this card to any of offered names and changing its
 * color to suitable color of the card.
 * @author Tereza Miklóšová
 */
public class CopyNameAndType extends Interactive {
    /**
     * This card is counted before ChangeColor bonus.
     */
    public int priority = 3;
    /**
     * Types that offer their memebers to copy their names and types.
     */
    public ArrayList<Type> types;
    /**
     * Id of the card containing this bonus.
     */
    private int thiscardid;

    /**
     * Constructor of this interactive bonus.
     * @param id Id of the card containing this bonus.
     * @param types Types that offer their memebers to copy their names and types.
     */
    public CopyNameAndType( int id, ArrayList<Type> types) {
        this.types = types;
        this.thiscardid = id;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("interactive.CardInteractives",loc);
        ResourceBundle typ = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(rb.getString("copyNameAndType")).append(" ");
        sb.append(typ.getString("any2" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types, locale, 2));

        sb.append(rb.getString("copyNameAndType2"));
        return sb.toString();
    }

    @Override
    public void askPlayer(ClientHandler client) {
        ArrayList<String> choices;
        choices = ArrayListCreator.createListOfNamesFromTypes(types, client.locale);
        String toSend = giveStringOfStringsWithSeparator(choices, ",");
        client.sendMessage("CopyNameAndType#" + thiscardid + "#" + toSend);
    }


    // OPTIMIZED BY THREADING
    @Override
    public int changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable, Server server,
                                         Integer idOfCardToChange, Card toChangeInto) throws CloneNotSupportedException {

        Type bestTypeToChangeInto = null;
        String bestNameToChangeInto = null;
        Card thisCard = null;


        // Remove this interactive from card to not get stuck in loop
        for (Card original : originalHand) {
            if (thiscardid == original.getId()) {
                thisCard = original;
                original.getInteractives().remove(this);
            }
        }

        //Copy hand for computing the original score on Hand
        List<Card> newHandOldScore = new ArrayList<>();
        for (Card copy : originalHand) {
            ArrayList<Malus> maluses = new ArrayList<>();
            ArrayList<Interactive> interactives = new ArrayList<>();
            // bonuses are never deleted, they can be the same objects
            // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
            if (copy.getMaluses() != null) {
                for (Malus m : copy.getMaluses()) {
                    maluses.add(m.clone());
                }
            }
            // The same rule apply to interactives = they can be deleted to signal it has been used
            if (copy.getInteractives() != null) {
                for (Interactive inter : copy.getInteractives()) {
                    interactives.add(inter.clone());
                }
            }
            newHandOldScore.add(new Card(copy.getId(), copy.getName(), copy.getStrength(), copy.getType(), copy.getBonuses(),
                    maluses, interactives));
        }

        //Count the score of the hand without this interactive to get the base best score
        ScoreCounterForAI sc = new ScoreCounterForAI();
        int bestScore = sc.countScore(newHandOldScore, cardsOnTable, true);

        // Try out different changes and find out the actual best possible score
        ArrayList<CopyNameAndTypeThread> threads = new ArrayList<>();
        for(Type type : types){
            CopyNameAndTypeThread t = new CopyNameAndTypeThread(newHandOldScore,cardsOnTable,type,thiscardid, server);
            threads.add(t);
            t.start();
        }

        for(CopyNameAndTypeThread thread : threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(CopyNameAndTypeThread thread : threads){
            int best = thread.getBestScore();
            if(best > bestScore){
                bestScore = best;
                bestNameToChangeInto = thread.getBestName();
                bestTypeToChangeInto = thread.getTypeToTry();
            }
        }


        if(bestTypeToChangeInto != null){
            assert thisCard != null;
            thisCard.setType(bestTypeToChangeInto);
            thisCard.setName(bestNameToChangeInto);
            idOfCardToChange = thisCard.getId();
            toChangeInto = thisCard;
        }
        return bestScore;
    }

    /*
    // NOT OPTIMIZED
    @Override
    public int changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {

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
        int bestScore = sc.countScore(newHandOldScore, cardsOnTable, true);

        // Try out different changes and find out the actual best possible score
        ArrayList<String> namesToTry = null;
        for (Type type : types) {
            //Get the names according to type the card can change into
            namesToTry = BigSwitches.switchTypeForNames(type);
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
                
                int currentScore = sc.countScore(newHand, cardsOnTable, true);
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
        //System.out.println("Total execution time spent in CopyNameAndType in millis: " + elapsedTime/1000000);

        return bestScore;
    }



     */

    @Override
    public Interactive clone() throws CloneNotSupportedException{
        CopyNameAndType newi = (CopyNameAndType)super.clone();
        newi.priority = this.priority;
        newi.types = new ArrayList<>(this.types);
        newi.thiscardid = this.thiscardid;
        return newi;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }
}
