package interactive;

import artificialintelligence.ScoreCounterForAI;
import maluses.Malus;
import server.Card;
import server.Server;
import server.Type;
import util.BigSwitches;

import java.util.ArrayList;
import java.util.List;

/**
 * Class counts parts of job computed by CopyNameAndType.
 * @author Tereza Miklóšová
 */
public class CopyNameAndTypeThread extends Thread {
    /**
     * Cards in hand.
     */
    List<Card> hand;
    /**
     * Cards on table.
     */
    ArrayList<Card> table;
    /**
     * Type assigned to try all its members for change.
     */
    Type typeToTry;
    /**
     * The name that awards the greatest score.
     */
    String bestName;
    /**
     * The best score which has been achieved with this type changing.
     */
    int bestScore;
    /**
     * Id of the card containing this bonus.
     */
    int thiscardid;

    Server server;

    /**
     * Constructor of this thread.
     * @param h Cards in hand.
     * @param t Cards on table.
     * @param type The name that awards the greatest score.
     * @param thisCardId Id of the card containing this bonus.
     */
    public CopyNameAndTypeThread(List<Card> h, ArrayList<Card> t, Type type, int thisCardId, Server server) {
        this.hand = h;
        this.table = t;
        this.typeToTry = type;
        this.thiscardid = thisCardId;
        bestScore = -999;
        this.server = server;
    }

    /**
     * Get {@link CopyNameAndTypeThread#bestName}.
     * @return {@link CopyNameAndTypeThread#bestName}
     */
    public String getBestName(){
        return bestName;
    }

    /**
     * Get {@link CopyNameAndTypeThread#bestScore}.
     * @return {@link CopyNameAndTypeThread#bestScore}
     */
    public int getBestScore(){
        return bestScore;
    }

    /**
     * Get {@link CopyNameAndTypeThread#typeToTry}.
     * @return {@link CopyNameAndTypeThread#typeToTry}
     */
    public Type getTypeToTry(){
        return typeToTry;
    }

    /**
     * The thread tries all members for given type as a new name and type for the card containing this bonus.
     * It saves the best options along with the best score achieved.
     */
    public void run(){

        ArrayList<String> namesToTry = BigSwitches.switchTypeForNames(typeToTry);
        ScoreCounterForAI sc = new ScoreCounterForAI(server);
        assert namesToTry != null;
        for (String nameToTry : namesToTry) {
            List<Card> newHand = new ArrayList<>();
            for (Card copy : hand) {
                ArrayList<Malus> maluses = new ArrayList<>();
                ArrayList<Interactive> interactives = new ArrayList<>();
                // bonuses are never deleted, they can be the same objects
                // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
                if (copy.getMaluses() != null) {
                    for (Malus m : copy.getMaluses()) {
                        try {
                            maluses.add(m.clone());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // The same rule apply to interactives = they can be deleted to signal it has been used
                if (copy.getInteractives() != null) {
                    for (Interactive inter : copy.getInteractives()) {
                        try {
                            interactives.add(inter.clone());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(thiscardid == copy.getId()){
                    newHand.add(new Card(copy.getId(), nameToTry, copy.getStrength(), typeToTry, copy.getBonuses(), maluses, interactives));
                } else{
                    newHand.add(new Card(copy.getId(), copy.getName(), copy.getStrength(), copy.getType(), copy.getBonuses(), maluses, interactives));
                }

            }

            int currentScore = sc.countScore(newHand, table, true);
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestName = nameToTry;
            }
        }
    }
}
