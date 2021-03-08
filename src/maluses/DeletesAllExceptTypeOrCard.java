package maluses;

import server.Card;
import server.Type;

import java.util.ArrayList;

public class DeletesAllExceptTypeOrCard extends Malus {
    public String text;
    public ArrayList<Type> types;
    public ArrayList<Type> except;
    public ArrayList<Integer> cards;
    private int thiscardid;

    public DeletesAllExceptTypeOrCard( ArrayList<Type> except, ArrayList<Integer> cards, int thiscardid) {
        this.except = except;
        this.types = getComplementOfTypes(except);
        this.text = "Blanks all except " + giveListOfTypesWithSeparator(except, ", ") + " and " + giveListOfCardsWithSeparator(cards, ", ");
        this.cards = cards;
        this.thiscardid = thiscardid;
        //System.out.println("Card INIT: Text: " + getText());
    }

    private ArrayList<Type> getComplementOfTypes(ArrayList<Type> except) {
        ArrayList<Type> complement_types = new ArrayList<>(){{add(Type.ARMY);add(Type.ARTIFACT); add(Type.WEAPON);add(Type.WEATHER);add(Type.CREATURE);add(Type.FLOOD);add(Type.LEADER);add(Type.EARTH);add(Type.WIZARD);add(Type.FIRE);add(Type.WILD);}};
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
