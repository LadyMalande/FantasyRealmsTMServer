package maluses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class DeletesAllTypeOrOtherSelftype  extends Malus{
    public String text;
    public ArrayList<Type> types;
    public Type selftype;
    private int thiscardid;

    public DeletesAllTypeOrOtherSelftype(ArrayList<Type> types, Type type, int thiscardid) {
        this.text = "Blanks all " + giveListOfTypesWithSeparator(types, ", ") + " or other " + BigSwitches.switchTypeForName(type);
        this.types = types;
        this.selftype = type;
        this.thiscardid = thiscardid;
        //System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToDelete) {
        ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>();
        copyDeckToMakeChanges.addAll(hand);
        for(Card c: copyDeckToMakeChanges){
            if(types.contains(c.type) || (selftype.equals(c.type) && thiscardid != c.id)){

                if(!whatToDelete.contains(c)) {
                    whatToDelete.add(c);
                }
            }
        }
        return 0;
    }
    @Override
    public int count(ArrayList<Card> hand) {
        /*
        ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>();
        copyDeckToMakeChanges.addAll(hand);
        for(Card c: copyDeckToMakeChanges){
            if(types.contains(c.type) || (selftype.equals(c.type) && thiscardid != c.id)){
                hand.remove(c);

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
        DeletesAllTypeOrOtherSelftype newm = new DeletesAllTypeOrOtherSelftype(newtypes,this.selftype,this.thiscardid);
        //System.out.println("In cloning DeletesAllTypeOrOtherSelftype: The new types and old types are equal = " + (newm.types == this.types));
        return newm;
    }
}
