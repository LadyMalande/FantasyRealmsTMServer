package interactive;

import artificialintelligence.ScoreCounterForAI;
import maluses.Malus;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.List;

public class ScoreCounterCopyNameAndTypeThread extends Thread {
    List<Card> hand;
    ArrayList<Card> table;
    Type typeToTry;
    String bestName;
    int bestScore;
    int thiscardid;

    public ScoreCounterCopyNameAndTypeThread(List<Card> h, ArrayList<Card> t, Type type, int thisCardId) {
        this.hand = h;
        this.table = t;
        this.typeToTry = type;
        this.thiscardid = thisCardId;
        bestScore = -999;
    }

    public String getBestName(){
        return bestName;
    }

    public int getBestScore(){
        return bestScore;
    }

    public Type getTypeToTry(){
        return typeToTry;
    }

    public void run(){

        ArrayList<String> namesToTry = BigSwitches.switchTypeForNames(typeToTry);
        ScoreCounterForAI sc = new ScoreCounterForAI();
        for (String nameToTry : namesToTry) {
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
                if(thiscardid == copy.id){
                    newHand.add(new Card(copy.id, nameToTry, copy.strength, typeToTry, copy.bonuses, maluses, interactives));
                } else{
                    newHand.add(new Card(copy.id, copy.name, copy.strength, copy.type, copy.bonuses, maluses, interactives));
                }

            }

            int currentScore = sc.countScore(newHand, table, true);
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestName = nameToTry;
            }

            //System.out.println("After trying out changing into " + thisCard.name + " [" + thisCard.type + "] the score IS " + currentScore);
        }
        //System.out.println("Thread of color " + BigSwitches.switchTypeForName(typeToTry) + " has best score : " + bestScore + " with name " + bestName);
    }
}
