package maluses;

import artificialintelligence.State;
import util.BigSwitches;
import server.Card;
import server.Type;
import util.TextCreators;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Implements the penalty that deletes all given types with the exception of named cards.
 * @author Tereza Miklóšová
 */
public class DeletesAllTypeExceptCard extends Malus {
    /**
     * Id of the card that contains this penalty.
     */
    public int thiscardid;

    /**
     * All cards of these types will be deleted from hand.
     */
    public ArrayList<Type> types;

    /**
     * All those cards won't be deleted from hand even if they are of type from {@link DeletesAllTypeExceptCard#types}.
     */
    public ArrayList<Integer> cards;

    /**
     * Constructor for this penalty.
     * @param thiscardid Id of the card that contains this penalty.
     * @param types All cards of these types will be deleted from hand.
     * @param cards All those cards won't be deleted from hand even if they are of type from {@link DeletesAllTypeExceptCard#types}.
     */
    public DeletesAllTypeExceptCard( int thiscardid, ArrayList<Type> types, ArrayList<Integer> cards) {
        this.thiscardid = thiscardid;
        this.types = types;
        this.cards = cards;
    }
    @Override
    public ArrayList<Integer> getCards(){ return cards;}

    @Override
    public  ArrayList<Type> getTypes(){ return this.types; }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle maluses = ResourceBundle.getBundle("maluses.CardMaluses",loc);
        sb.append(maluses.getString("blanks"));
        sb.append(" ");
        sb.append(maluses.getString("all"));
        sb.append(" ");
        sb.append(TextCreators.giveListOfTypesWithSeparator(types, ", ",locale,4,true));
        sb.append(" ");
        sb.append(maluses.getString("except"));
        sb.append(" ");
        sb.append(TextCreators.giveListOfCardsWithSeparator(cards, locale,2));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToRemove) {
        if(hand.stream().anyMatch(card -> card.getId() == this.thiscardid)) {
            ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(hand);
            for (Card c : copyDeckToMakeChanges) {
                if (types.contains(c.getType()) && !cards.contains(c.getId())) {
                    if(!whatToRemove.contains(c)){
                        whatToRemove.add(c);
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        return 0;
    }

    @Override
    public Malus clone(){
        ArrayList<Type> newtypes = new ArrayList<>(types);
        return new DeletesAllTypeExceptCard(this.thiscardid,newtypes, this.cards);
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return this.types.stream().anyMatch(types::contains) ||
                cards.stream().anyMatch( id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id)))));
    }
    @Override
    public int getReaction(Type t, ArrayList<Card> hand) {
        return 0;
    }

    @Override
    public ArrayList<Integer> returnWillBeDeleted(){
        ArrayList<Integer> willBeDeleted = new ArrayList<>();
        for(Type t: types){
            willBeDeleted.addAll(Objects.requireNonNull(BigSwitches.switchTypeForIds(t)));
        }
        willBeDeleted.removeAll(cards);
        return willBeDeleted;
    }
}
