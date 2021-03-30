package maluses;

import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class DeletesAllExceptTypeOrCard extends Malus {
    public String text;
    public ArrayList<Type> types;
    public ArrayList<Type> except;
    public ArrayList<Integer> cards;
    private int thiscardid;

    public DeletesAllExceptTypeOrCard( ArrayList<Type> except, ArrayList<Integer> cards, int thiscardid) {
        this.except = except;
        this.types = getComplementOfTypes(except);
        this.text = "Blanks all except " + giveListOfTypesWithSeparator(except, ", ") + " and " + giveListOfCardsWithSeparator(cards, ", ","en",1,false);
        this.cards = cards;
        this.thiscardid = thiscardid;
       // System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }

    private ArrayList<Type> getComplementOfTypes(ArrayList<Type> except) {
        ArrayList<Type> complement_types = new ArrayList<>(){{add(Type.ARMY);add(Type.ARTIFACT); add(Type.WEAPON);add(Type.WEATHER);add(Type.BEAST);add(Type.FLOOD);add(Type.LEADER);add(Type.LAND);add(Type.WIZARD);add(Type.FLAME);add(Type.WILD);}};
       complement_types.add(Type.ARTIFACT);
       complement_types.add(Type.ARMY);
       for(Type t: except){
           complement_types.remove(t);
       }
        return complement_types;
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
        sb.append(maluses.getString("deletesAllCards"));
        sb.append(" ");
        sb.append(maluses.getString("except"));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(except, ", ",locale,2,true));
        if(cards != null){
            sb.append(", ");
            sb.append(giveListOfCardsWithSeparator(cards, ", ",locale,2, false));
        }
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToRemove) {
        if(!hand.stream().filter(card -> card.id == this.thiscardid).findAny().isEmpty()) {
            ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>();
            copyDeckToMakeChanges.addAll(hand);
            for (Card c : copyDeckToMakeChanges) {
                if (!cards.contains(c.id) && !except.contains(c.type) && c.id != thiscardid) {
                    if(!whatToRemove.contains(c)){
                        whatToRemove.add(c);
                    }
                }
            }
            return 0;
        }
        else{
            return 0;
        }
    }

    @Override
    public int count(ArrayList<Card> hand) {
        /*
        if(!hand.stream().filter(card -> card.id == this.thiscardid).findAny().isEmpty()) {
            ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>();
            copyDeckToMakeChanges.addAll(hand);
            for (Card c : copyDeckToMakeChanges) {
                if (!cards.contains(c.id) && !except.contains(c.type) && c.id != thiscardid) {
                    hand.remove(c);
                }
            }
            return 0;
        }
        else{
            return 0;
        }

         */
        return 0;
    }

    @Override
    public Malus clone() throws CloneNotSupportedException{
        DeletesAllExceptTypeOrCard newm = (DeletesAllExceptTypeOrCard)super.clone();
        newm.text = this.text;
        newm.priority = this.priority;
        newm.thiscardid = this.thiscardid;
        ArrayList<Type> newtypes = new ArrayList<Type>();
        for(Type t: types){
            newtypes.add(t);
        }
        ArrayList<Type> newexcept = new ArrayList<Type>();
        for(Type t: except){
            newexcept.add(t);
        }
        ArrayList<Integer> newCardIds = new ArrayList<>();
        for(Integer i: cards){
            newCardIds.add(Integer.valueOf(i));
        }
        newm.types = newtypes;
        newm.except = newexcept;
        newm.cards = newCardIds;
        //System.out.println("In cloning DeletesAllExceptTypeOrCard: The new types and old types are equal = " + (newm.types == this.types));
        return newm;
    }
}
