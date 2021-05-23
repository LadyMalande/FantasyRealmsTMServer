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
 * Implements the malus that deletes all cards of given type.
 * @author Tereza Miklóšová
 */
public class DeletesAllType extends Malus{
    /**
     * Cards of these types will be deleted from hand.
     */
    public ArrayList<Type> types;

    /**
     * Id of the card that contains this penalty.
     */
    public int thiscardid;

    /**
     * Constructor for this penalty.
     * @param id Id of the card that contains this penalty.
     * @param types Cards of these types will be deleted from hand.
     */
    public DeletesAllType( int id, ArrayList<Type> types) {
        this.thiscardid = id;
        this.types = types;
    }

    @Override
    public  ArrayList<Type> getTypes(){ return this.types; }
    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle maluses = ResourceBundle.getBundle("maluses.CardMaluses",loc);
        sb.append(maluses.getString("deletesAll"));
        sb.append(" ");
        sb.append(TextCreators.giveListOfTypesWithSeparator(types, "and",locale,4,true));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToRemove) {
        if(hand.stream().anyMatch(card -> card.getId() == this.thiscardid)) {
            ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(hand);
            for (Card c : copyDeckToMakeChanges) {
                if (types.contains(c.getType())) {
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
        return new DeletesAllType(this.thiscardid,newtypes);
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
        return willBeDeleted;
    }
}
