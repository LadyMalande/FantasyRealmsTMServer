package maluses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class DeletesAllTypeExceptCard extends Malus {
    public String text;
    public int thiscardid;
    public ArrayList<Type> types;
    public ArrayList<Integer> cards;

    public DeletesAllTypeExceptCard( int thiscardid, ArrayList<Type> types, ArrayList<Integer> cards) {
        this.thiscardid = thiscardid;
        this.text = "Blanks all "+ giveListOfTypesWithSeparator(types, ", ")+" except " + giveListOfCardsWithSeparator(cards, ", ","en",1,false);
        this.types = types;
        this.cards = cards;
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }

    @Override
    public String getText(){
        return this.text;
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
        sb.append(maluses.getString("all"));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types, ", ",locale,4,true));
        sb.append(" ");
        sb.append(maluses.getString("except"));
        sb.append(" ");
        sb.append(giveListOfCardsWithSeparator(cards, ", ",locale,2, false));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToRemove) {
        if(hand.stream().anyMatch(card -> card.id == this.thiscardid)) {
            ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(hand);
            for (Card c : copyDeckToMakeChanges) {
                if (types.contains(c.type) && !cards.contains(c.id)) {
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
        /*
        if(hand.stream().anyMatch(card -> card.id == this.thiscardid)) {
            ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(hand);
            for (Card c : copyDeckToMakeChanges) {
                if (types.contains(c.type) && !cards.contains(c.id)) {
                    hand.remove(c);
                }
            }
        }

         */
        return 0;
    }

    @Override
    public Malus clone() throws CloneNotSupportedException{
        ArrayList<Type> newtypes = new ArrayList<Type>();
        for(Type t: types){
            newtypes.add(t);
        }
        DeletesAllTypeExceptCard newm = new DeletesAllTypeExceptCard(this.thiscardid,newtypes,this.cards);
        //System.out.println("In cloning DeletesAllTypeExceptCard: The new types and old types are equal = " + (newm.types == this.types));
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
        return this.types.stream().anyMatch(type -> types.contains(type)) ||
                cards.stream().anyMatch( id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id)))));
    }
    @Override
    public int getReaction(Type t, ArrayList<Card> hand) {
        return 0;
    }

    @Override
    public ArrayList<Integer> returnWillBeDeleted(){
        ArrayList<Integer> willBeDeleted = new ArrayList<>();
        for(Type t: types){
            willBeDeleted.addAll(BigSwitches.switchTypeForIds(t));
        }
        willBeDeleted.removeAll(cards);
        return willBeDeleted;
    }
}
