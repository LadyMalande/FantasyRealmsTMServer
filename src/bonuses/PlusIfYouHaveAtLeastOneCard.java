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
 * Bonus that represents bonus which gives points if the hand contains at least one of the cards.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusIfYouHaveAtLeastOneCard extends Bonus {
    /**
     * Points given for each completion of the conditions to get it.
     */
    private int how_much;
    /**
     * One of cards of these ids is needed to activate the bonus.
     */
    public ArrayList<Integer> idsOfCardsNeeded;

    /**
     * Constructor for the bonus.
     * @param hm How many points will the player get for completing the bonus.
     * @param cards One of this cards completes the bonus.
     */
    public PlusIfYouHaveAtLeastOneCard(int hm, ArrayList<Integer> cards){

        this.how_much = hm;
        this.idsOfCardsNeeded = cards;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rbbonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+").append(how_much);
        sb.append(rbbonuses.getString("ifYouHave"));
        sb.append(" ");
        sb.append(rbbonuses.getString("atLeastOneOfTheseCards"));
        sb.append(" ");
        sb.append(TextCreators.giveListOfCardsWithSeparator(idsOfCardsNeeded, "or",locale, 4));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        ArrayList<String> names = new ArrayList<>();
        for(Integer i : idsOfCardsNeeded){
            names.add(BigSwitches.switchIdForName(i));
        }
        for(Card c: hand){
            if(names.contains(c.getName())){
                return how_much;
            }
        }
        return 0;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potentialTable, potentialDeck;
        ArrayList<String> names = new ArrayList<>();
        for(Integer i : idsOfCardsNeeded){
            names.add(BigSwitches.switchIdForName(i));
        }
        if(hand.stream().filter(c -> names.contains(c.getName())).count() >= 1){
            return how_much;
        }
        long suitableOnTable = table.stream().filter(c -> names.contains(c.getName())).count();
        potentialTable = Math.min(suitableOnTable - state.getNumberOfEnemies() * suitableOnTable / table.size() * how_much, how_much);
        long suitableInDeck = state.getProbablyInDeck().stream().filter(c -> names.contains(c.getName())).count();
        potentialDeck = Math.min((deckSize / unknownCards) * suitableInDeck / deckSize * how_much, how_much);
        return Math.max(potentialTable, potentialDeck);
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return idsOfCardsNeeded.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id)))));
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        // Nothing to do better
        if(hand.stream().anyMatch(card -> idsOfCardsNeeded.contains(card.getId()))){
            return 0;
        }
        else{
            if(idsOfCardsNeeded.stream().anyMatch(id -> BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName(id)) == t)){
                return how_much;
            }
        }
        return 0;
    }
}
