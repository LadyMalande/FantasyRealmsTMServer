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
 * Bonus that represents bonus which gives points for the types if the hand contains given card.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusForEachTypeIfYouHaveCard extends Bonus  {
    /**
     * Points given for each of the types if conditions are met.
     */
    public int howMuch;
    /**
     * Types of cards that will award points if the hand contains also card of cardId.
     */
    public ArrayList<Type> types;
    /**
     * This card is needed to enable the bonus for the types.
     */
    public int cardid;

    /**
     * Constructor of the bonus.
     * @param howMuch Points given for each of the types if conditions are met.
     * @param types Types of cards that will award points if the hand contains also card of cardId.
     * @param cardid This card is needed to enable the bonus for the types.
     */
    public PlusForEachTypeIfYouHaveCard(int howMuch, ArrayList<Type> types, int cardid) {
        this.howMuch = howMuch;
        this.types = types;
        this.cardid = cardid;
    }

    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        if(hand.stream().anyMatch(c -> c.getName().equals(BigSwitches.switchIdForName(cardid)))){
            return types;
        }
        return null;
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
        ResourceBundle rbcards = ResourceBundle.getBundle("server.CardNames",loc);
        sb.append("+").append(howMuch);
        sb.append(" ");
        sb.append(rb.getString("for"));
        sb.append(" ");
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(TextCreators.giveListOfTypesWithSeparator(types,", ",locale, 4));
            sb.append(rb.getString("ifYouHave"));
        sb.append(" ");
            sb.append(rbcards.getString(BigSwitches.switchIdForSimplifiedName(cardid) + "4"));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        boolean countit = false;
        for(Card c: hand){
            if(c.getName().equals(BigSwitches.switchIdForName(cardid))){
                countit = true;
            }
                if (types.contains(c.getType())) {
                    total += howMuch;
                }

        }
        if(countit){
            return total;
        } else{
            return 0;
        }
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        long numberOfSuitableCards = hand.stream().filter(c -> types.contains(c.getType())).count();
        if(hand.stream().filter(c -> c.getName().equals(BigSwitches.switchIdForName(cardid))).count() >= 1){
            potential += (numberOfSuitableCards)*howMuch;
        }
        if(hand.stream().filter(c -> c.getName().equals(BigSwitches.switchIdForName(cardid))).count() >= 1) {
            if (numberOfSuitableCards < 5) {
                long oddsOnTable = table.stream().filter(c -> types.contains(c.getType())).count();
                potential += (oddsOnTable - state.getNumberOfEnemies() * oddsOnTable / (float)table.size()) * howMuch;
                long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).count();
                potential += (deckSize / (float)unknownCards) * oddsOnDeck /(float) deckSize * howMuch;
            }
        }
        if(hand.stream().filter(c -> c.getName().equals(BigSwitches.switchIdForName(cardid))).count() < 1) {
            long tableCard = table.stream().filter(c -> c.getName().equals(BigSwitches.switchIdForName(cardid))).count();
            potential += (tableCard - state.getNumberOfEnemies() * tableCard / (float)table.size()) * howMuch * numberOfSuitableCards;
            long deck = state.getProbablyInDeck().stream().filter(c -> c.getName().equals(BigSwitches.switchIdForName(cardid))).count();
            potential += (deck - state.getNumberOfEnemies() * deck / (float)table.size()) * howMuch * numberOfSuitableCards;
        }
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((cardid)))) ||
                types.stream().anyMatch(types::contains);

    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(types.contains(t) && hand.stream().anyMatch(card -> card.getName().equals(BigSwitches.switchIdForName(cardid)))){
            return howMuch;
        }
        if(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((cardid))) == t){
            if(hand.stream().noneMatch(card -> card.getName().equals(BigSwitches.switchIdForName(cardid)))){
                int numberOfmatchingTypes = 0;
                for(Card c : hand){
                    if(types.contains(c.getType())){
                        numberOfmatchingTypes++;
                    }
                }
                return numberOfmatchingTypes * howMuch;
            }
        }
        return 0;
    }
}
