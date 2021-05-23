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
 * Bonus that represents bonus which gives one time bonus when the hand contains cardNeeded
 * and at least one of idsOfCardsNeeded.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusIfYouHaveCardAndAtLeastOneFrom extends Bonus{
    /**
     * Points given for each completion of the conditions to get it.
     */
    private int how_much;
    /**
     * One of these these cards with this ids are needed to activate the bonus.
     */
    private ArrayList<Integer> idsOfCardsNeeded;
    /**
     * Card of this id is needed to complete this bonus.
     */
    private int cardNeeded;

    /**
     * Constructor of this bonus.
     * @param hm How much is this bonus giving when completed.
     * @param cards Which card needs to be in the hand to complete the bonus.
     * @param idcardneeded One of those cards is needed to complete the bonus.
     */
    public PlusIfYouHaveCardAndAtLeastOneFrom(int hm, ArrayList<Integer> cards, int idcardneeded){
        this.how_much = hm;
        this.idsOfCardsNeeded = cards;
        this.cardNeeded = idcardneeded;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames",loc);
        ResourceBundle rbbonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+").append(how_much);
        sb.append(rbbonuses.getString("ifYouHave"));
        sb.append(" ");
        sb.append(rb.getString(BigSwitches.switchIdForSimplifiedName(cardNeeded) + "4"));
        sb.append(" ");
        sb.append(rbbonuses.getString("and"));
        sb.append(" ");
        sb.append(rbbonuses.getString("atLeastOneOfTheseCards"));
        sb.append(" ");
        sb.append(TextCreators.giveListOfCardsWithSeparator(idsOfCardsNeeded, "and",locale,4));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        ArrayList<String> names = new ArrayList<>();
        for(Integer i: idsOfCardsNeeded){
            names.add(BigSwitches.switchIdForName(i, "en"));
        }
        boolean hascard = false;
        boolean hasoneofthese = false;
        for(Card c: hand){
            if(c.getName().equals(BigSwitches.switchIdForName(cardNeeded, "en"))){
                hascard = true;
            }
            else if(names.contains(c.getName())){
                hasoneofthese = true;
            }
            if(hascard && hasoneofthese){
                return how_much;
            }
        }
        return 0;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return idsOfCardsNeeded.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id))))) ||
                types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((cardNeeded))));

    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(hand.stream().anyMatch(card -> card.getId() == cardNeeded)){
            // We have the card needed in our hand,check the other cards
            if(idsOfCardsNeeded.stream().anyMatch((id -> BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName(id)) == t))){
                return how_much;
            }
        } else{
            // We DONT have the card needed in our hand,check the other cards
            if(hand.stream().anyMatch(card -> idsOfCardsNeeded.contains(card.getId())) && BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName(cardNeeded)) == t){
                return how_much;
            }
        }
        return 0;
    }
}
