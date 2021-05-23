package bonuses;

import artificialintelligence.State;
import server.Card;
import server.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Bonus that represents bonus that can delete all penalties from all cards in hand.
 * @author Tereza Miklóšová
 */
public class DeleteAllMaluses extends Bonus implements Serializable {
    /**
     * The priority of the bonus. Goes before the deleting penalties are counted.
     */
    public int priority = 5;

    /**
     * Constructor of the bonus.
     */
    public DeleteAllMaluses(){
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append(bonuses.getString("deleteAllMaluses"));
        return sb.toString();
    }

    @Override
    public int getPriority(){ return this.priority; }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        return state.getNumPointsLost();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(c.getMaluses() != null) {
                c.setMaluses(null);
            }
        }
        return 0;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }


}
