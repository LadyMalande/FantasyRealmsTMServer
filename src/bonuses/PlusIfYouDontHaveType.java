package bonuses;

import artificialintelligence.State;
import util.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Bonus that represents bonus which gives one time points if given type is not in player's hand.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusIfYouDontHaveType extends Bonus  {
    /**
     * Points given for each completion of the conditions to get it.
     */
    public int howMuch;

    /**
     * If this type is not in the hand, player will receive bonus points.
     */
    public Type type;

    /**
     * Constructor for the bonus.
     * @param howMuch How much will the player get when the condition is met.
     * @param type This type can't be in player's hand in order to receive the bonus.
     */
    public PlusIfYouDontHaveType( int howMuch, Type type) {
        this.howMuch = howMuch;
        this.type = type;
    }
    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        // You get -howMuch if u have the foribidden type
        return new ArrayList<>() {{add(type);}};
    }
    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        return -howMuch;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        ResourceBundle rbbonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+").append(howMuch);
        sb.append(rbbonuses.getString("ifYouDontHave"));
        sb.append(" ");
            sb.append(rb.getString("no4" + BigSwitches.switchTypeForGender(type)));
            sb.append(" ");
        sb.append(rb.getString(BigSwitches.switchTypeForName(type).toLowerCase() + "4"));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(c.getType().equals(type)){
                return 0;
            }
        }
        return howMuch;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        if(!state.getHaveTheseTypes().contains(type)){
            return howMuch;
        }
        potential += howMuch /(float)(state.getNumOfType(type) + 1);

        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return types.contains(type);
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(type == t){
            return -howMuch;
        }
        return 0;
    }
}
