package maluses;

import artificialintelligence.ScoreCounterForAI;
import artificialintelligence.State;
import util.BigSwitches;
import server.Card;
import server.Type;
import util.HandCloner;
import util.TextCreators;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Implements the penalty that deletes its host card if the hand doesn't contain at least one card of given type.
 * @author Tereza Miklóšová
 */
public class CardIsDeletedIfYouDontHaveAtLeastOneType extends Malus {
    /**
     * This card will be resolved after types have been deleted by different kinds of penalty.
     */
    public int priority = 7;
    /**
     * Id of the card that contains this penalty.
     */
    int thiscardid;
    /**
     * Any card of one of these types is needed in the hand to stop this card from erasing itself.
     */
    public ArrayList<Type> types;

    /**
     * Constructor for this penalty.
     * @param thiscardid Id of the card that contains this penalty.
     * @param types Any card of one of these types is needed in the hand to stop this card from erasing itself.
     */
    public CardIsDeletedIfYouDontHaveAtLeastOneType( int thiscardid, ArrayList<Type> types) {
        this.thiscardid = thiscardid;
        this.types = types;
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
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        if(hand.stream().anyMatch(card -> types.contains(card.getType()))){
            return null;
        } else{
            return types;
        }

    }
    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        ScoreCounterForAI sc = new ScoreCounterForAI();
        int fullHand = sc.countScore(hand, null, true);
        List<Card> without = hand.stream().filter(card -> card.getId() != thiscardid).collect(Collectors.toList());
        int withoutThis = sc.countScore(without, null, true);
        return fullHand - withoutThis;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle maluses = ResourceBundle.getBundle("maluses.CardMaluses",loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(maluses.getString("cardIsDeletedIfYouDontHaveAtLeastOneType"));
        sb.append(" ");
        sb.append(rb.getString("one4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(TextCreators.giveListOfTypesWithSeparator(types, "or", locale,4,false));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int getPriority(){ return this.priority; }

    @Override
    public int count(ArrayList<Card> hand,  ArrayList<Card> whatToRemove) {
        boolean delete = true;
        for(Card c: hand){
            if(!whatToRemove.contains(c)){
                if(types.contains(c.getType())){
                    delete = false;
                }
            }
        }
        if(delete){
            hand.removeIf(x -> x.getId() == thiscardid);
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
        return new CardIsDeletedIfYouDontHaveAtLeastOneType(this.thiscardid, newtypes);
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
        if(hand.stream().anyMatch(card -> types.contains(card.getType()))){
            // no need to enhance for this card, it is satisfied
            return 0;
        } else{
            if(types.contains(t)){
                //the card can help
                HandCloner hc = new HandCloner();
                try{
                    ArrayList<Card> newHand = hc.cloneHand(null, hand);
                    List<Card> toOmit = hand.stream().filter(card -> card.getMaluses().contains(this)).collect(Collectors.toList());
                    ArrayList<Card> withoutThis = hc.cloneHand(toOmit.get(0), hand);
                    ScoreCounterForAI sc = new ScoreCounterForAI();
                    int newHandScore = sc.countScore(newHand, null, true);
                    int handOmited = sc.countScore(withoutThis, null, true);
                    return newHandScore - handOmited;
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }


            }
        }
        return 0;
    }
}
