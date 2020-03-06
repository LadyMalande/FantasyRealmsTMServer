package maluses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class MinusForEachOtherSelftypeOrType extends Malus {
    public int priority = 8;
    public final String text;
    public int howMuch;
    public ArrayList<Type> types;
    public Type selftype;
    public int thiscardid;

    public MinusForEachOtherSelftypeOrType(int howMuch, ArrayList<Type> types, Type selftype, int thiscardid) {
        if(types == null){
            this.text =  howMuch + " for other " + BigSwitches.switchTypeForName(selftype);
        }else {
            this.text = howMuch + " for each " + giveListOfTypesWithSeparator(types, ", ") + " or other " + BigSwitches.switchTypeForName(selftype);
        }
        this.howMuch = howMuch;
        this.types = types;
        this.selftype = selftype;
        this.thiscardid = thiscardid;
        System.out.println("Card INIT: Text: " + getText());
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
            if(c.type.equals(selftype) && c.id != thiscardid){
                total += howMuch;
            }
            if(types != null && !types.isEmpty()){
                if (types.contains(c.type) && (c.id != thiscardid)) {
                    total += howMuch;
                }
            }
        }
        return total;
    }
}
