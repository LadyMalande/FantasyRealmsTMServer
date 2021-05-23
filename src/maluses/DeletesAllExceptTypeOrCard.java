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
 * Implements the penalty that deletes all cards except given types and except certain cards.
 * @author Tereza Miklóšová
 */
public class DeletesAllExceptTypeOrCard extends Malus {
    /**
     * All of those types will be deleted from hand.
     */
    public ArrayList<Type> types;
    /**
     * All those types won't be deleted from hand.
     */
    public ArrayList<Type> except;
    /**
     * All those cards won't be deleted from hand even if they are of type from {@link DeletesAllExceptTypeOrCard#types}.
     */
    public ArrayList<Integer> cards;
    /**
     * Id of the card that contains this penalty.
     */
    private int thiscardid;

    /**
     * Constructor for this penalty.
     * @param except All those types won't be deleted from hand.
     * @param cards All those cards won't be deleted from hand even if they are of type from {@link DeletesAllExceptTypeOrCard#types}.
     * @param thiscardid Id of the card that contains this penalty.
     */
    public DeletesAllExceptTypeOrCard( ArrayList<Type> except, ArrayList<Integer> cards, int thiscardid) {
        this.except = except;
        this.types = getComplementOfTypes(except);
        this.cards = cards;
        this.thiscardid = thiscardid;
    }

    @Override
    public ArrayList<Integer> getCards(){ return cards;}

    private ArrayList<Type> getComplementOfTypes(ArrayList<Type> except) {
        ArrayList<Type> complement_types = new ArrayList<>(){{add(Type.ARMY);add(Type.ARTIFACT); add(Type.WEAPON);add(Type.WEATHER);add(Type.BEAST);add(Type.FLOOD);add(Type.LEADER);add(Type.LAND);add(Type.WIZARD);add(Type.FLAME);add(Type.WILD);}};
       for(Type t: except){
           complement_types.remove(t);
       }
        return complement_types;
    }

    @Override
    public  ArrayList<Type> getTypes(){ return this.types; }
    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle maluses = ResourceBundle.getBundle("maluses.CardMaluses",loc);
        sb.append(maluses.getString("deletesAllCards"));
        sb.append(" ");
        sb.append(maluses.getString("except"));
        sb.append(" ");
        sb.append(TextCreators.giveListOfTypesWithSeparator(except, ", ",locale,2,true));
        if(cards != null){
            sb.append(", ");
            sb.append(TextCreators.giveListOfCardsWithSeparator(cards, locale,2));
        }
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToRemove) {
        if(hand.stream().anyMatch(card -> card.getId() == this.thiscardid)) {
            ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(hand);
            for (Card c : copyDeckToMakeChanges) {
                if (!cards.contains(c.getId()) && !except.contains(c.getType()) && c.getId() != thiscardid) {
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
    public Malus clone() throws CloneNotSupportedException{
        DeletesAllExceptTypeOrCard newm = (DeletesAllExceptTypeOrCard)super.clone();
        newm.priority = this.priority;
        newm.thiscardid = this.thiscardid;
        ArrayList<Type> newtypes = new ArrayList<>(types);
        ArrayList<Type> newexcept = new ArrayList<>(except);
        ArrayList<Integer> newCardIds = new ArrayList<>(cards);
        newm.types = newtypes;
        newm.except = newexcept;
        newm.cards = newCardIds;
        return newm;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return this.types.stream().anyMatch(types::contains);
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
