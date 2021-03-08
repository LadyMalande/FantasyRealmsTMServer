package maluses;

import server.Card;
import server.Type;

import java.util.ArrayList;

public class DeletesAllType extends Malus{
    public String text;
    public ArrayList<Type> types;
    public int thiscardid;

    public DeletesAllType( int id, ArrayList<Type> types) {
        this.thiscardid = id;
        this.text = "Blanks all "+ giveListOfTypesWithSeparator(types, ", ");
        this.types = types;
        //System.out.println("Card INIT: Text: " + getText());
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
                if (types.contains(c.type)) {
                    if(!whatToRemove.contains(c)){
                        whatToRemove.add(c);
                    }
                }
            }
            return 0;
        } else{
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
                if (types.contains(c.type)) {
                    hand.remove(c);
                }
            }
            return 0;
        } else{
            return 0;
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
        DeletesAllType newm = new DeletesAllType(this.thiscardid,newtypes);
        //System.out.println("In cloning DeletesAllType: The new types and old types are equal = " + (newm.types == this.types));
        return newm;
    }
}
