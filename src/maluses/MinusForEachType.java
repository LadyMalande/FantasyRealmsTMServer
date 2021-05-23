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
 * Implements the penalty that deduces points from final score for each card of given type.
 * @author Tereza Miklóšová
 */
public class MinusForEachType extends Malus {
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
     * Constructor for this penalty.
     * @param howMuch How many points from final score will be deducted for each card of given types in hand.
     * @param types Types of cards that will deduct points from hand.
     */
    public MinusForEachType( int howMuch, ArrayList<Type> types) {
        this.howMuch = howMuch;
        this.types = types;
    }

    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {

        return types;
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
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(TextCreators.giveListOfTypesWithSeparator(types, "or",locale,4,false));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int getPriority(){ return this.priority; }

    @Override
    public int count(ArrayList<Card> hand) {

        int total = 0;
        for(Card c: hand){
            if(types.contains(c.getType())){
                total += howMuch;
            }
        }
        return total;
    }

    @Override
    public Malus clone(){
        ArrayList<Type> newtypes = new ArrayList<>(types);
        return new MinusForEachType(this.howMuch, newtypes);
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
        if(types.contains(t)){
            return howMuch;
        }
        return 0;
    }
}
