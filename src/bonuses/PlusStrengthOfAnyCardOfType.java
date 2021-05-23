package bonuses;

import artificialintelligence.State;
import util.BigSwitches;
import server.Card;
import server.Type;
import util.TextCreators;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Bonus that represents bonus which gives points equal to the highest strength on card of suitable type.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusStrengthOfAnyCardOfType extends Bonus  {
    /**
     * Cards of those types can be considered in the choosing of highest strength.
     */
    public List<Type> types;

    /**
     * Constructor for the bonus.
     * @param types Cards of those types can be considered in the choosing of highest strength.
     */
    public PlusStrengthOfAnyCardOfType(List<Type> types) {
        this.types = types;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();

        Locale loc = new Locale(locale);
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses", loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes", loc);
        sb.append(bonuses.getString("plusStrengthOfAnyCardOfType"));
        sb.append(" ");
        sb.append(rb.getString("any2" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(TextCreators.giveListOfTypesWithSeparator((ArrayList<Type>) types, ", ", locale, 2));
        sb.append(" ");
        sb.append(bonuses.getString("plusStrengthOfAnyCardOfType2"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int max_on_hand = 0;
        for(Card c: hand){
            if(types.contains(c.getType()) && c.getStrength()>max_on_hand){
                max_on_hand = c.getStrength();
            }
        }
        return max_on_hand;

    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){

        double now = this.count(hand);
        double potentialTableDeck;
       List<Card> potential = table.stream().filter(c -> types.contains(c.getType())).collect(Collectors.toList());
       int max = 0;
       for(Card c : potential){
           if(c.getStrength() > max){
               max = c.getStrength();
           }
       }
        double tableStrength = Math.max((1 - state.getNumberOfEnemies() /table.size()) * max,0) ;
        List<Card> potentialDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).collect(Collectors.toList());
        double deckStrength = 0.0;
        for(Card c : potentialDeck){
            deckStrength += c.getStrength() * deckSize/(float)unknownCards * (1/(float)deckSize);
        }
        potentialTableDeck = Math.max(tableStrength, deckStrength);
        return Math.max(now, potentialTableDeck);
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
