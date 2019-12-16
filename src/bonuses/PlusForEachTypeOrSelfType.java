package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class PlusForEachTypeOrSelfType extends Bonus  {
    public String text;
    public int howMuch;
    public ArrayList<Type> types;
    public Type selftype;
    public int thiscardid;

    public PlusForEachTypeOrSelfType(int howMuch, int thiscardid, ArrayList<Type> types, Type sefltype) {
        this.howMuch = howMuch;
        this.types = types;
        this.selftype = sefltype;
        this.thiscardid = thiscardid;
        String listtypes = "";
        boolean first = true;
        for(Type type: types){
            if(!first){
                listtypes +=", ";
            }
            listtypes += BigSwitches.switchTypeForName(type);
            first = false;
        }
        text = "+" + howMuch + " for each " + listtypes + " or any other " + BigSwitches.switchTypeForName(selftype) + " you have";
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;

        for(Card c: hand){
            if(types.contains(c.type) || (c.type.equals(selftype) && c.id != thiscardid)){
                total += howMuch;
            }
        }

       return total;
    }
}
