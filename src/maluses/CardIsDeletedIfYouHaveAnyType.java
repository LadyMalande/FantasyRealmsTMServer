package maluses;

import artificialintelligence.ScoreCounterForAI;
import artificialintelligence.State;
import util.BigSwitches;
import server.Card;
import server.Type;
import util.HandCloner;
import util.TextCreators;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements the penalty that deletes its host card if the hand contains any card of given type.
 * @author Tereza Miklóšová
 */
public class CardIsDeletedIfYouHaveAnyType extends Malus {
    /**
     * This card will be resolved after types have been deleted by different kinds of penalty.
     */
    public int priority = 7;
    /**
     * Id of the card that contains this penalty.
     */
    private int thiscardid;

    /**
     * Any card of one of these types will force this card to erase itself.
     */
    public ArrayList<Type> types;

    /**
     * Constructor for this penalty.
     * @param thiscardid Id of the card that contains this penalty.
     * @param types Any card of one of these types will force this card to erase itself.
     */
    public CardIsDeletedIfYouHaveAnyType(int thiscardid,  ArrayList<Type> types) {
        this.thiscardid = thiscardid;
        this.types = types;
    }

    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        List<Card> match = hand.stream().filter(card -> types.contains(card.getType())).collect(Collectors.toList());

        if(match.size() > 1){
            return null;
        }
        if(match.size() == 1){
            ArrayList<Type> colors =
                    new ArrayList<>(Arrays.asList(Type.ARMY, Type.ARTIFACT, Type.BEAST, Type.FLAME, Type.FLOOD,
                            Type.LAND, Type.LEADER, Type.WEAPON, Type.WEATHER, Type.WIZARD));
            colors.removeAll(types);
            return colors;
        } else{
            return types;
        }
    }

    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        List<Card> match = hand.stream().filter(card -> types.contains(card.getType())).collect(Collectors.toList());
        if(match.size() > 1){
            return 0;
        }
        ScoreCounterForAI sc = new ScoreCounterForAI();
        int fullHand = sc.countScore(hand, null, true);
        List<Card> without = hand.stream().filter(card -> card.getId() != thiscardid).collect(Collectors.toList());
        int withoutThis = sc.countScore(without, null, true);
        int difference = fullHand - withoutThis;
        if(match.size() == 1){
            return difference;
        } else{
            return -difference;
        }

    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle maluses = ResourceBundle.getBundle("maluses.CardMaluses",loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(maluses.getString("blankedWith"));
        sb.append(" ");
        sb.append(rb.getString("any4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(TextCreators.giveListOfTypesWithSeparator(types, "or",locale,4,false));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int getPriority(){ return this.priority; }

    @Override
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToRemove) {
        boolean delete = false;
        for(Card c: hand){
            if (!whatToRemove.contains(c)) {
                if(types.contains(c.getType())){
                    delete = true;
                    break;
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
        return new CardIsDeletedIfYouHaveAnyType(this.thiscardid, newtypes);
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return this.types.contains(Type.WILD) || this.types.stream().anyMatch(types::contains);
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand) {
        if(hand.stream().anyMatch(card -> types.contains(card.getType()))){
            // no need to enhance for this card, it has been already broken
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
                    return handOmited - newHandScore;
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }


            }
        }
        return 0;
    }
}
