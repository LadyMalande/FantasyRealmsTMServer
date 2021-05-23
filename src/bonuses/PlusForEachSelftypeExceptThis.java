package bonuses;

import artificialintelligence.State;
import util.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Bonus that represents bonus which gives extra points for all cards of this type safe for this card.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusForEachSelftypeExceptThis extends Bonus  {
    /**
     * Points given for each completion of the conditions to get it.
     */
    public int howMuch;
    /**
     * Cards of this type will give extra points.
     */
    public Type type;
    /**
     * This card won't award extra points.
     */
    private int thiscardid;

    /**
     * Constructor for this bonus.
     * @param how_much Points given for each completion of the conditions to get it.
     * @param thiscardid This card won't award extra points.
     * @param type Cards of this type will give extra points.
     */
    public PlusForEachSelftypeExceptThis(int how_much, int thiscardid, Type type) {
        this.howMuch = how_much;
        this.type = type;
        this.thiscardid = thiscardid;
    }

    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        return new ArrayList<>() {{add(type);}};
    }
    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        return howMuch;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append("+").append(howMuch);
        sb.append(" ");
        sb.append(rb.getString("for"));
        sb.append(" ");
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(type)));
        sb.append(" ");
        sb.append(rb.getString("other4" + BigSwitches.switchTypeForGender(type)));
        sb.append(" ");
        sb.append(rb.getString(BigSwitches.switchTypeForName(type).toLowerCase() + "4"));
        sb.append(" ");
        sb.append(rb.getString("inYourHand"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int sum = 0;
        if(hand.stream().filter(card -> card.getId() == thiscardid).count() > 1 ){
            for(Card c: hand){
                if(c.getType().equals(type)){
                    sum += howMuch;
                }
            }
            sum -= howMuch;
        }
        else {
            for (Card c : hand) {
                if (c.getType().equals(type) && c.getId() != thiscardid) {
                    sum += howMuch;
                }
            }
        }
        return sum;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        potential += (state.getNumOfType(type) - 1)* howMuch;
        long oddsOnTable = table.stream().filter(c -> c.getType() == type).count();
        potential += (oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/(float)table.size()) * howMuch;
        long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> c.getType() == type).count();
        potential += (deckSize / (float)unknownCards) * oddsOnDeck/(float)deckSize * howMuch;
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return types.contains(type);
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(type == t){
            return howMuch;
        }
        return 0;
    }
}
