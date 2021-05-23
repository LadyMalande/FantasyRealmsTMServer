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
 * Implements the penalty that deletes all cards of given types or all other cards except this one of this card's type.
 * @author Tereza Miklóšová
 */
public class DeletesAllTypeOrOtherSelftype  extends Malus{
    /**
     * Cards of these types will be deleted from hand.
     */
    public ArrayList<Type> types;
    /**
     * Other cards of this type except this one will be deleted from hand.
     */
    public Type selftype;
    /**
     * Id of the card that contains this penalty.
     */
    private int thiscardid;

    /**
     * Constructor for this penalty.
     * @param types Cards of these types will be deleted from hand.
     * @param type Other cards of this type except this one will be deleted from hand.
     * @param thiscardid Id of the card that contains this penalty.
     */
    public DeletesAllTypeOrOtherSelftype(ArrayList<Type> types, Type type, int thiscardid) {
        this.types = types;
        this.selftype = type;
        this.thiscardid = thiscardid;
    }

    @Override
    public  ArrayList<Type> getTypes(){ return this.types; }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle maluses = ResourceBundle.getBundle("maluses.CardMaluses",loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(maluses.getString("blanks"));
        sb.append(" ");
        if(types != null){
            sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
            sb.append(" ");
            sb.append(TextCreators.giveListOfTypesWithSeparator(types, "or",locale,4,false));
            sb.append(" ");
            sb.append(rb.getString("or"));
            sb.append(" ");
        }
        sb.append(rb.getString("other4" + BigSwitches.switchTypeForGender(selftype)));
        sb.append(" ");
        sb.append(rb.getString(BigSwitches.switchTypeForName(selftype).toLowerCase() + "4"));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToDelete) {
        ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(hand);
        for(Card c: copyDeckToMakeChanges){
            if(types.contains(c.getType()) || (selftype.equals(c.getType()) && thiscardid != c.getId())){

                if(!whatToDelete.contains(c)) {
                    whatToDelete.add(c);
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
        return new DeletesAllTypeOrOtherSelftype(newtypes, this.selftype, this.thiscardid);
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return this.types.stream().anyMatch(types::contains)
                || types.contains(selftype);
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
        willBeDeleted.addAll(Objects.requireNonNull(BigSwitches.switchTypeForIds(selftype)));
        return willBeDeleted;
    }

}
