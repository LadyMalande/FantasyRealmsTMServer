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
 * Bonus that represents bonus which gives points for all of the mentioned types or cards.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusForEachTypeAndForEachCard extends Bonus  {
    /**
     * Points given for each completion of the conditions to get it.
     */
    private int howMuch;
    /**
     * Types that award bonus points.
     */
    public ArrayList<Type> types;
    /**
     * Cards that award bonus points.
     */
    public ArrayList<Integer> cards;

    /**
     * Constructor for the bonus.
     * @param hm Points given for each completion of the conditions to get it.
     * @param types Types that award bonus points.
     * @param cards Cards that award bonus points.
     */
    public PlusForEachTypeAndForEachCard(int hm, ArrayList<Type> types, ArrayList<Integer> cards){
        this.howMuch = hm;
        this.types = types;
        this.cards = cards;
    }
    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {

        return types;
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
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(TextCreators.giveListOfTypesWithSeparator(types,", ",locale, 4));
        if(cards != null){
            sb.append(", ");
            sb.append(TextCreators.giveListOfCardsWithSeparator(cards,", ",locale, 4));
        }
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        ArrayList<String> names = new ArrayList<>();
        for(Integer i: cards){
            names.add(BigSwitches.switchIdForName(i));
        }
        for(Card c: hand){
                if (types.contains(c.getType())) {
                    total += howMuch;
                }
                if (names.contains(c.getName())) {
                    total += howMuch;
                }
        }

        return total;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        long numberOfSuitableCards = hand.stream().filter(c -> types.contains(c.getType())).count() + hand.stream().filter(c -> cards.contains(c.getId())).count();
        potential += (numberOfSuitableCards)* howMuch;
        if(numberOfSuitableCards <6){
            long oddsOnTable = table.stream().filter(c -> types.contains(c.getType())).count() + table.stream().filter(c -> cards.contains(c.getId())).count();
            potential += (oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/(float)table.size()) * howMuch;
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).count() + state.getProbablyInDeck().stream().filter(c -> cards.contains(c.getId())).count();
            potential += (deckSize / (float)unknownCards) * oddsOnDeck/(float)deckSize * howMuch;
        }
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return this.types.stream().anyMatch(types::contains) ||
                cards.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id)))));

    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(types.contains(t)){
            return howMuch;
        }
        if(cards.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id)))))){
            return howMuch;
        }
        return 0;
    }
}
