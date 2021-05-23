package maluses;

import artificialintelligence.State;
import util.BigSwitches;
import server.Card;
import server.Type;
import util.TextCreators;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Implements the penalty that deletes all given types with the exception of named cards.
 * @author Tereza Miklóšová
 */
public class MinusIfYouDontHaveAtLeastOneType extends Malus {
    /**
     * This penalty will be resolved last.
     */
    public int priority = 8;
    /**
     *  How many points will be deducted from the final score if the player doesn't have at least one
     *  card of given types in hand.
     */
    int howMuch;
    /**
     * Player has to have at least one card of one of these types to not lose points.
     */
    ArrayList<Type> types;

    /**
     * Constructor for this penalty.
     * @param howMuch Points deduced from the score for breaking the condition.
     * @param types Player has to have at least one card of one of these types to not lose points.
     */
    public MinusIfYouDontHaveAtLeastOneType( int howMuch, ArrayList<Type> types) {
        this.howMuch = howMuch;
        this.types = types;
    }

    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        if(hand.stream().anyMatch(card -> types.contains(card.getType()))){
            return null;
        }
        else {
            return types;
        }
    }

    @Override
    public Card satisfiesCondition(ArrayList<Card> hand) {
         List<Card> cards = hand.stream().filter(card -> types.contains(card.getType())).collect(Collectors.toList());
        //Says ids of cards that cant be recolored if the size of this array is only 1
        if(cards.size() == 1){
            return cards.get(0);
        } else{
            return null;
        }
    }

    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        return -howMuch;
    }

    @Override
    public  ArrayList<Type> getTypes(){ return this.types; }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle maluses = ResourceBundle.getBundle("maluses.CardMaluses",loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(howMuch);
        sb.append(maluses.getString("unlessWith"));
        sb.append(" ");
        sb.append(rb.getString("one4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(TextCreators.giveListOfTypesWithSeparator(types, "or",locale,4,false));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int getPriority(){ return this.priority; }

    @Override
    public int count(ArrayList<Card> hand) {

        for(Card c: hand){
            if(types.contains(c.getType())){
                return 0;
            }
        }
        return howMuch;
    }

    @Override
    public Malus clone(){
        ArrayList<Type> newtypes = new ArrayList<>(types);
        return new MinusIfYouDontHaveAtLeastOneType(this.howMuch, newtypes);
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
        if(hand.stream().noneMatch(card -> types.contains(card.getType()))){
            //We dont have the needed type AND we can change into it
            if(types.contains(t)){
                return howMuch;
            }
        }
        return 0;
    }
}
