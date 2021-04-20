package interactive;

import artificialintelligence.ScoreCounterForAI;
import maluses.Malus;
import server.*;

import java.util.*;

import static server.Type.*;


public class ChangeColor extends Interactive {
    public int priority = 3;
    public String text;
    public int thiscardid;

    public ChangeColor(int id) {
        this.text = "Change type of one card in your hand";
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
        sb.append(rb.getString("changeColor"));
        return sb.toString();
    }

    // All dialogs made with the help of example on https://code.makery.ch/blog/javafx-dialogs-official/

    @Override
    public boolean askPlayer(ClientHandler client) {
        return client.sendInteractive( "ChangeColor#"+thiscardid);

    }

    @Override
    public void changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        long startTime = System.nanoTime();
        //System.out.println("Counting ChangeColor");

        Type bestTypeToChangeInto = null;
        Card bestCardToChange = null;


        // Remove this interactive from card to not get stuck in loop
        for (Card original : originalHand) {
            if (thiscardid == original.id) {
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

        ArrayList<Type> typesToTry = new ArrayList<Type>(Arrays.asList(FLOOD, FLAME, LAND, WEATHER, ARMY, WEAPON, ARTIFACT, WIZARD, LEADER, BEAST, WILD));

        // Try out different changes and find out the actual best possible score
        for(Card card : originalHand) {
            for (Type type : typesToTry) {
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
                        // Change the color of card if it is its turn to change it, otherwise just copy the card for further score counting
                        if (copy.equals(card)) {
                            newHand.add(new Card(copy.id, copy.name, copy.strength, type, copy.bonuses, maluses, interactives));
                        } else {
                            newHand.add(new Card(copy.id, copy.name, copy.strength, copy.type, copy.bonuses, maluses, interactives));
                        }

                    }

                    int currentScore = sc.countScore(newHand, cardsOnTable);
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestTypeToChangeInto = type;
                        bestCardToChange = card;
                    }
                    //System.out.println("After changing color of " + card.name + " to [" + card.type + "] the score IS " + currentScore);
            }
        }

        if(bestTypeToChangeInto != null){
            bestCardToChange.type = bestTypeToChangeInto;
            //System.out.println("Changed the Color of " + bestCardToChange.name + " to be [" + bestCardToChange.type + "]");
        }
        long elapsedTime = System.nanoTime() - startTime;
       // System.out.println("Total execution time spent in ChangeColor in millis: " + elapsedTime/1000000);

    }

    @Override
    public Interactive clone() throws CloneNotSupportedException{
        ChangeColor newi = (ChangeColor)super.clone();
        newi.priority = this.priority;
        newi.text = this.text;
        newi.thiscardid = this.thiscardid;
        return newi;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards){
        double potential = 0.0;
        // TODO
        return potential;
    }
}
