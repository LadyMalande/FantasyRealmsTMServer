package bonuses;

import artificialintelligence.State;
import util.BigSwitches;
import server.Card;
import server.Type;
import util.TextCreators;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Bonus that represents bonus which gives bonus points for every card of given types.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusForEachType extends Bonus  {
    /**
     * Types that award bonus points.
     */
    public ArrayList<Type> types;
    /**
     * Points given for each completion of the conditions to get it.
     */
    public int howMuch;

    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        return types;
    }
    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        return howMuch;
    }

    /**
     * Constructor for this bonus.
     * @param types Types that award bonus points.
     * @param how_much Points given for each completion of the conditions to get it.
     */
    public PlusForEachType(ArrayList<Type> types, int how_much) {
        this.types = types;
        this.howMuch = how_much;
        StringBuilder s = new StringBuilder();
        boolean first = true;
        for(Type t: types){
            if(!first){
                s.append(" or ");
            }
            s.append(BigSwitches.switchTypeForName(t));
            first = false;
        }
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
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(TextCreators.giveListOfTypesWithSeparator(types,"or",locale, 4));
        sb.append(" ");
        sb.append(rb.getString("inYourHand"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int sum = 0;
        for(Card c: hand){
                if (types.contains(c.getType())) {
                    sum += howMuch;
                }
        }
        return sum;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        long numberOfSuitableCards = hand.stream().filter(c -> types.contains(c.getType())).count();
        potential += (numberOfSuitableCards)* howMuch;
        if(numberOfSuitableCards <6){
            long oddsOnTable = table.stream().filter(c -> types.contains(c.getType())).count();
            potential += (oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/(float)table.size()) * howMuch;
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).count();
            potential += (deckSize / (float)unknownCards) * oddsOnDeck/(float)deckSize * howMuch;
        }
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return this.types.stream().anyMatch(types::contains);

    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(types.contains(t)){
            return howMuch;
        }
        return 0;
    }
}
