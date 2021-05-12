package maluses;

import artificialintelligence.ScoreCounterForAI;
import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;
import util.HandCloner;

import java.util.*;
import java.util.stream.Collectors;

public class CardIsDeletedIfYouHaveAnyType extends Malus {
    public int priority = 7;
    public String text;
    private int thiscardid;
    public ArrayList<Type> types;

    public CardIsDeletedIfYouHaveAnyType(int thiscardid,  ArrayList<Type> types) {
        this.text = "Blanked with any " + giveListOfTypesWithSeparator(types, " or " );
        this.thiscardid = thiscardid;
        this.types = types;
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }
    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        List<Card> match = hand.stream().filter(card -> types.contains(card.getType())).collect(Collectors.toList());

        if(match.size() > 1){
            return null;
        }
        if(match.size() == 1){
            ArrayList<Type> colors =
                    new ArrayList<Type>(Arrays.asList(Type.ARMY, Type.ARTIFACT, Type.BEAST, Type.FLAME, Type.FLOOD,
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
    public String getText(){
        return this.text;
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
        sb.append(giveListOfTypesWithSeparator(types, "or",locale,4,false));
        sb.append(".");
        return sb.toString();
    }
    @Override
    public int getPriority(){ return this.priority; }
    @Override
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToRemove) {
        boolean delete = false;
        //System.out.println("Resolving card "+ BigSwitches.switchIdForName(thiscardid));
        for(Card c: hand){
            if (!whatToRemove.contains(c)) {
                if(types.contains(c.type)){
                    delete = true;
                    //System.out.println("c.type==" + BigSwitches.switchTypeForName(c.type) + " je v seznamu types");
                    break;
                }
            }
        }
        if(delete){

            //whatToRemove.add(hand.stream().filter(cardOnHand -> thiscardid == (cardOnHand.id)).findFirst().get());
            hand.removeIf(x -> x.id == thiscardid);
            //System.out.println("Card with id " + thiscardid + " was put to whatToremove, now cards in hand: " + hand.size());
        }
        return 0;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        return 0;
    }

    @Override
    public Malus clone() throws CloneNotSupportedException{
        ArrayList<Type> newtypes = new ArrayList<Type>();
        for(Type t: types){
            newtypes.add(t);
        }
        CardIsDeletedIfYouHaveAnyType newm = new CardIsDeletedIfYouHaveAnyType(this.thiscardid, newtypes);
        //System.out.println("In cloning CardIsDeletedIfYouHaveAnyType: The new types and old types are equal = " + (newm.types == this.types));
        return newm;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        // TODO
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return this.types.contains(Type.WILD) || this.types.stream().anyMatch(type -> types.contains(type));
    }

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
                    int newHandScore = sc.countScore(hand, null, true);
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
