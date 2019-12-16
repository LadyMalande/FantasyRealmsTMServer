package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class PlusIfYouHaveAtLeastOneType extends Bonus  {
    public String text;
    private int how_much;
    private ArrayList<Type> types;

    public PlusIfYouHaveAtLeastOneType(int hm, ArrayList<Type> types){

        this.how_much = hm;
        this.types = types;

        String listtypes = "";
        boolean first = true;
        for(Type t: types){
            if(!first){
                listtypes +=" or ";
            }
            listtypes += BigSwitches.switchTypeForName(t);
            first = false;
        }
        this.text = "+" + how_much + " if you have any card of type " + listtypes;
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(types.contains(c.type)){
                return how_much;
            }
        }
        return 0;
    }
}
