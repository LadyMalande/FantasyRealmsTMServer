package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class PlusForEachType extends Bonus  {
    public String text;
    public ArrayList<Type> types;
    public int how_much;

    public PlusForEachType( ArrayList<Type> types, int how_much) {
        this.types = types;
        this.how_much = how_much;
        StringBuilder s = new StringBuilder();
        boolean first = true;
        for(Type t: types){
            if(!first){
                s.append(" or ");
            }
            s.append(BigSwitches.switchTypeForName(t));
            first = false;
        }
        this.text = "+" + how_much + " for each card of type " + s;
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int sum = 0;
        for(Card c: hand){
                if (types.contains(c.type)) {
                    sum += how_much;
                }
        }

        return sum;
    }
}
