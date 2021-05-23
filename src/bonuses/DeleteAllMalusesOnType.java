package bonuses;

import artificialintelligence.State;
import util.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Bonus that represents bonus that deletes all maluses on cards of given type.
 * @author Tereza Miklóšová
 */
public class DeleteAllMalusesOnType extends Bonus  {
    /**
     * The priority of the bonus. Goes before the deleting penalties are counted.
     */
    public int priority = 5;

    /**
     * On which type of cards will be the penalties deleted.
     */
    private Type deleteMalusesOnThisType;

    /**
     * Constructor for the bonus.
     * @param t On which type of cards will the penalties deleted.
     */
    public DeleteAllMalusesOnType(Type t){
        this.deleteMalusesOnThisType = t;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(bonuses.getString("deleteAllMalusesOnType")).append(" ");
        sb.append(rb.getString(BigSwitches.switchTypeForName(deleteMalusesOnThisType).toLowerCase() + "6"));
        sb.append(".");
        return sb.toString();
    }
    @Override
    public int getPriority(){ return this.priority; }

    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(c.getType().equals(deleteMalusesOnThisType)){
                if(c.getMaluses() != null){
                    c.getMaluses().clear();
                }
            }
        }
        return 0;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double swamp = 0.0;
        double wildfire = 0.0;
        double greatflood = 0.0;
        for(Card c : hand){
            if( c.getId() == 35 ){
                swamp = c.getMaluses().get(0).count(hand);
            }
            if(c.getId() == 33 ){
                ArrayList<Card> whatToRemove = new ArrayList<>();
                c.getMaluses().get(0).count(hand, whatToRemove);
                greatflood = giveValue(hand, whatToRemove);
            }
            if(c.getId() == 41){
                ArrayList<Card> whatToRemove = new ArrayList<>();
                c.getMaluses().get(0).count(hand, whatToRemove);
                wildfire = giveValue(hand, whatToRemove);
            }
        }
        return Math.max(Math.max(swamp,wildfire), greatflood);
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }

}
