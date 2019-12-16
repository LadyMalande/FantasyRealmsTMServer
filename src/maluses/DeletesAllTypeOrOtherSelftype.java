package maluses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class DeletesAllTypeOrOtherSelftype  extends Malus{
    public String text;
    public ArrayList<Type> types;
    private Type selftype;
    private int thiscardid;

    public DeletesAllTypeOrOtherSelftype(ArrayList<Type> types, Type type, int thiscardid) {
        this.text = "Deletes all " + giveListOfTypesWithSeparator(types, ", ") + " or other " + BigSwitches.switchTypeForName(type);
        this.types = types;
        this.selftype = type;
        this.thiscardid = thiscardid;
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>();
        copyDeckToMakeChanges.addAll(hand);
        for(Card c: copyDeckToMakeChanges){
            if(types.contains(c.type) || (selftype.equals(c.type) && thiscardid != c.id)){
                hand.remove(c);
            }
        }
        return 0;
    }
}
