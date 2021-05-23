package maluses;

import artificialintelligence.State;
import util.BigSwitches;
import server.Card;
import server.Type;
import util.TextCreators;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Implements the penalty that deducts points from final score for each card of given type or other cards of selftype.
 * @author Tereza Miklóšová
 */
public class MinusForEachOtherSelftypeOrType extends Malus {
    /**
     * This penalty will be resolved last.
     */
    public int priority = 8;

    /**
     * How many points from final score will be deducted for each card of given types in hand.
     */
    public int howMuch;
    /**
     * Types of cards that will deduct points from hand.
     */
    public ArrayList<Type> types;
    /**
     * Other cards of this type will deduct points from hand.
     */
    public Type selftype;
    /**
     * Id of the card that contains this penalty.
     */
    public int thiscardid;

    /**
     * Constructor for this penalty.
     * @param howMuch How many points from final score will be deducted for each card of given types in hand.
     * @param types Types of cards that will deduct points from hand.
     * @param selftype Other cards of this type will deduct points from hand.
     * @param thiscardid Id of the card that contains this penalty.
     */
    public MinusForEachOtherSelftypeOrType(int howMuch, ArrayList<Type> types, Type selftype, int thiscardid) {
        this.howMuch = howMuch;
        this.types = types;
        this.selftype = selftype;
        this.thiscardid = thiscardid;
    }

    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        ArrayList<Type> list = new ArrayList<>();
        if(types != null){
            list.addAll(types);
        }
        if(selftype != null){
            list.add(selftype);
        }

        return list;
    }
    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        return howMuch;
    }

    @Override
    public  ArrayList<Type> getTypes(){ return this.types; }
    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(howMuch);
        sb.append(" ");
        sb.append(rb.getString("for"));
        sb.append(" ");
        if(types != null){
            sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
            sb.append(" ");
            sb.append(TextCreators.giveListOfTypesWithSeparator(types, "or",locale,4,false));
            sb.append(" ");
            sb.append(rb.getString("or"));
            sb.append(" ");
        }
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(selftype)));
        sb.append(" ");
        sb.append(rb.getString("other4" + BigSwitches.switchTypeForGender(selftype)));
        sb.append(" ");
        sb.append(rb.getString(BigSwitches.switchTypeForName(selftype).toLowerCase() + "4"));
        sb.append(".");
        return sb.toString();
    }
    @Override
    public int getPriority(){ return this.priority; }
    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        for(Card c: hand){
            if(c.getType().equals(selftype) && c.getId() != thiscardid){
                total += howMuch;
            }
            if(types != null && !types.isEmpty()){
                if (types.contains(c.getType()) && (c.getId() != thiscardid)) {
                    total += howMuch;
                }
            }
        }
        return total;
    }

    @Override
    public Malus clone() {

        ArrayList<Type> newtypes;
        if(this.types != null)
            newtypes = new ArrayList<>(this.types);
        else{
            newtypes = null;
        }
        return new MinusForEachOtherSelftypeOrType(this.howMuch, newtypes, this.selftype, this.thiscardid);
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }

    public boolean reactsWithTypes(ArrayList<Type> types) {
        return this.types.stream().anyMatch(types::contains)
                || types.contains(selftype);
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand) {
        if(types.contains(t) || selftype == t){
            return howMuch;
        }
        return 0;
    }
}
