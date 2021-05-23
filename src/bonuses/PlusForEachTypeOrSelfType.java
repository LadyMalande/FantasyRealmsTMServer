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
 * Bonus that represents bonus which gives points for all cards of given types.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusForEachTypeOrSelfType extends Bonus  {
    /**
     * Points given for each completion of the conditions to get it.
     */
    public int howMuch;
    /**
     * These types award bonus points.
     */
    public ArrayList<Type> types;
    /**
     * Other cards with this type give extra points.
     */
    public Type selftype;
    /**
     * The id of the card that contains this bonus. This card will not give bonus points for being of selftype.
     */
    public int thiscardid;

    /**
     * Constructor for this bonus.
     * @param howMuch How many points are awarder for each card of given types.
     * @param thiscardid The id of the card that contains this bonus. This card will not give bonus points for being of selftype.
     * @param types These types award bonus points.
     * @param sefltype Other cards with this type give extra points.
     */
    public PlusForEachTypeOrSelfType(int howMuch, int thiscardid, ArrayList<Type> types, Type sefltype) {
        this.howMuch = howMuch;
        this.types = types;
        this.selftype = sefltype;
        this.thiscardid = thiscardid;
    }
    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        ArrayList<Type> list = new ArrayList<>(types);
        list.add(selftype);
        return list;
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
        if(types != null){
            sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
            sb.append(" ");
            sb.append(TextCreators.giveListOfTypesWithSeparator(types, "or",locale,4));
            sb.append(" ");
            sb.append(rb.getString("or"));
            sb.append(" ");
        }
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(selftype)));
        sb.append(" ");
        sb.append(rb.getString("other4" + BigSwitches.switchTypeForGender(selftype)));
        sb.append(" ");
        sb.append(rb.getString(BigSwitches.switchTypeForName(selftype).toLowerCase() + "4"));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;

        for(Card c: hand){
            if(types.contains(c.getType()) || (c.getType().equals(selftype) && c.getId() != thiscardid)){
                total += howMuch;
            }
        }

       return total;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        long numberOfSuitableCards = hand.stream().filter(c -> types.contains(c.getType())).count() + hand.stream().filter(c -> selftype == c.getType()).count() - 1;
        potential += (numberOfSuitableCards)*howMuch;
        if(numberOfSuitableCards <6){
            long oddsOnTable = table.stream().filter(c -> types.contains(c.getType())).count() + table.stream().filter(c -> selftype == c.getType()).count();
            potential += (oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/(float)table.size()) * howMuch;
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).count() + state.getProbablyInDeck().stream().filter(c -> selftype == c.getType()).count();
            potential += (deckSize / (float)unknownCards) * oddsOnDeck/(float)deckSize * howMuch;
        }
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return types.stream().anyMatch(types::contains) || types.contains(selftype);
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(types.contains(t)){
            return howMuch;
        }
        if(selftype == t){
            return howMuch;
        }
        return 0;
    }
}
