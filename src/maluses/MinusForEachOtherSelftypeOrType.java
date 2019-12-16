package maluses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class MinusForEachOtherSelftypeOrType extends Malus {
    public int priority = 8;
    public String text;
    public int howMuch;
    public ArrayList<Type> types;
    public Type selftype;
    public int thiscardid;

    public MinusForEachOtherSelftypeOrType(int howMuch, ArrayList<Type> types, Type selftype, int thiscardid) {
        if(types == null){
            this.text =  howMuch + " for any other " + BigSwitches.switchTypeForName(selftype);
        }else {
            this.text = howMuch + " for each " + giveListOfTypesWithSeparator(types, ", ") + " or any other " + BigSwitches.switchTypeForName(selftype);
        }
        this.howMuch = howMuch;
        this.types = types;
        this.selftype = selftype;
        this.thiscardid = thiscardid;
    }

    @Override
    public String getText(){
        return this.text;
    }
    @Override
    public int getPriority(){ return this.priority; }
    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        for(Card c: hand){
            if(types == null) {
                if (((selftype.equals(c.type) && c.id != thiscardid))) {
                    total += howMuch;
                }
            }
            else{
                if (types.contains(c.type) || (selftype.equals(c.type) && c.id != thiscardid)) {
                    total += howMuch;
                }
            }
        }
        return total;
    }
}
