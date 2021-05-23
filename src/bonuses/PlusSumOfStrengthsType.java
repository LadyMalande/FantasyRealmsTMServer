package bonuses;

import artificialintelligence.State;
import util.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Bonus that represents bonus which gives points equal to the sum of strengths of cards of that type in the hand.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusSumOfStrengthsType extends Bonus  {
    /**
     * Cards of which type will add the strengths to the final score.
     */
    public Type type;

    /**
     * Constructor for bonus.
     * @param type Type of cards which will
     */
    public PlusSumOfStrengthsType(Type type) {
        this.type = type;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses", loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes", loc);
        sb.append(bonuses.getString("plusSumOfStrengthsType")).append(" ");
        sb.append(rb.getString(BigSwitches.switchTypeForName(type).toLowerCase() + "2pl"));
        sb.append(bonuses.getString("plusSumOfStrengthsType2"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        for(Card c: hand){
            if(type.equals(c.getType())){
                total += c.getStrength();
            }
        }
        return total;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand) {
        return 0;
    }
}
