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
 * Bonus that represents bonus which gives points for each of listed cards.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusForEachCard extends Bonus {
    /**
     * Points given for each completion of the conditions to get it.
     */
    private int how_much;
    /**
     * These cards award extra points.
     */
    private ArrayList<Integer> idsOfCardsNeeded;

    /**
     * Constructor for this bonus.
     * @param hm How many points are awarded for every of the listed cards.
     * @param ids These cards award extra points.
     */
    public PlusForEachCard(int hm, ArrayList<Integer> ids){
        this.how_much = hm;
        this.idsOfCardsNeeded = ids;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+").append(how_much);
        sb.append(" ");
        sb.append(rb.getString("forEachOfThese")).append(" ");
        sb.append(TextCreators.giveListOfCardsWithSeparator(idsOfCardsNeeded,", ",locale,1));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int sum = 0;
        for(Card c: hand){
            for(int id: idsOfCardsNeeded) {
                if (BigSwitches.switchIdForName(id, "en").equals(c.getName())) {
                    sum += how_much;
                }
            }
        }
        return sum;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return idsOfCardsNeeded.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id)))));
    }
    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(idsOfCardsNeeded.stream().anyMatch(id -> t == (BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id)))))){
            return how_much;
        }
        return 0;
    }
}
