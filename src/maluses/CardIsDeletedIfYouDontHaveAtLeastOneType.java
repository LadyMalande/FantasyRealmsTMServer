package maluses;

import artificialintelligence.ScoreCounterForAI;
import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;
import util.HandCloner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CardIsDeletedIfYouDontHaveAtLeastOneType extends Malus {
    public int priority = 7;
    public String text;
    int thiscardid;
    public ArrayList<Type> types;

    public CardIsDeletedIfYouDontHaveAtLeastOneType( int thiscardid, ArrayList<Type> types) {
        this.text = "Blanked unless with at least one " + giveListOfTypesWithSeparator(types, " or ");
        this.thiscardid = thiscardid;
        this.types = types;
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
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
        sb.append(maluses.getString("cardIsDeletedIfYouDontHaveAtLeastOneType"));
        sb.append(" ");
        sb.append(rb.getString("one4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types, "or", locale,4,false));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int getPriority(){ return this.priority; }
    @Override
    public int count(ArrayList<Card> hand,  ArrayList<Card> whatToRemove) {
        boolean delete = true;
        //System.out.println("Resolving card "+ BigSwitches.switchIdForName(thiscardid));
        for(Card c: hand){
            if(!whatToRemove.contains(c)){
                if(types.contains(c.type)){
                    delete = false;
                    //System.out.println("c.type==" + BigSwitches.switchTypeForName(c.type) + " je v seznamu types");
                } else{
                //System.out.println("c.type==" + BigSwitches.switchTypeForName(c.type) + " NENI v seznamu types");
                }
            }
        }
        if(delete){
            //whatToRemove.add(hand.stream().filter(cardOnHand -> thiscardid == (cardOnHand.id)).findFirst().get());
            hand.removeIf(x -> x.id == thiscardid);
            //System.out.println("Card with id " + thiscardid + " was put to whatToDelete, now cards in hand: " + hand.size());
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
        CardIsDeletedIfYouDontHaveAtLeastOneType newm = new CardIsDeletedIfYouDontHaveAtLeastOneType(this.thiscardid, newtypes);
        //System.out.println("In cloning CardIsDeletedIfYouDontHaveAtLeastOneType: The new types and old types are equal = " + (newm.types == this.types));
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
        return this.types.stream().anyMatch(type -> types.contains(type));
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
                    int newHandScore = sc.countScore(hand, null);
                    int handOmited = sc.countScore(withoutThis, null);
                    return newHandScore - handOmited;
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }


            }
        }
        return 0;
    }
}
