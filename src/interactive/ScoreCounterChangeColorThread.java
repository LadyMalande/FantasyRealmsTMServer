package interactive;

import artificialintelligence.ScoreCounterForAI;
import maluses.Malus;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static server.Type.*;

public class ScoreCounterChangeColorThread extends Thread{
    int bestScore;
    Card toChange;
    Type bestColor;
    List<Card> hand;
    ArrayList<Card> table;

    public ScoreCounterChangeColorThread(List<Card> h, ArrayList<Card> t,Card toChange) {
        this.toChange = toChange;
        this.hand = h;
        this.table = t;
        bestScore = -999;
    }

    public int getBestScore() {
        return bestScore;
    }

    public Card getToChange() {
        return toChange;
    }

    public Type getBestColor() {
        return bestColor;
    }

    public void run(){
        ArrayList<Type> typesToTry1 = new ArrayList<Type>(Arrays.asList(FLOOD, FLAME, LAND, WEATHER, ARMY, WILD,  ARTIFACT, WIZARD, LEADER, BEAST, WEAPON));
        ScoreCounterForAI sc = new ScoreCounterForAI();
        for (Type type : typesToTry1) {
            List<Card> newHand = new ArrayList<>();
            for (Card copy : hand) {
                ArrayList<Malus> maluses = new ArrayList<>();
                ArrayList<Interactive> interactives = new ArrayList<>();
                // bonuses are never deleted, they can be the same objects
                // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
                if (copy.maluses != null) {
                    //System.out.println("Klonuji kartu " + copy.name);
                    for (Malus m : copy.maluses) {
                        try {
                            maluses.add(m.clone());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // The same rule apply to interactives = they can be deleted to signal it has been used
                if (copy.interactives != null) {
                    for (Interactive inter : copy.interactives) {
                        try {
                            interactives.add(inter.clone());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // Change the color of card if it is its turn to change it, otherwise just copy the card for further score counting
                if (copy.equals(toChange)) {
                    newHand.add(new Card(copy.id, copy.name, copy.strength, type, copy.bonuses, maluses, interactives));
                } else {
                    newHand.add(new Card(copy.id, copy.name, copy.strength, copy.type, copy.bonuses, maluses, interactives));
                }


                int currentScore = sc.countScore(newHand, table, true);
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                    bestColor = type;
                }
                //System.out.println("After changing color of " + card.name + " to [" + card.type + "] the score IS " + currentScore);
            }
        }
    }
/*
    public void run(){
        ArrayList<Type> typesToTry1 = new ArrayList<Type>(Arrays.asList(FLOOD, FLAME, LAND, WEATHER, ARMY, WILD));
        ArrayList<Type> typesToTry2 = new ArrayList<Type>(Arrays.asList( ARTIFACT, WIZARD, LEADER, BEAST, WEAPON));

        ScoreCounterColorChangeSpecificColorsThread t1 = new ScoreCounterColorChangeSpecificColorsThread(hand, table, toChange, typesToTry1);
        ScoreCounterColorChangeSpecificColorsThread t2 = new ScoreCounterColorChangeSpecificColorsThread(hand, table, toChange, typesToTry2);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int b1 = t1.getBestScore();
        int b2 = t2.getBestScore();
        if(b1 > b2){
            bestScore = b1;
            bestColor = t1.getBestColor();
        } else{
            bestScore = b2;
            bestColor = t2.getBestColor();
        }
    }

 */
}
