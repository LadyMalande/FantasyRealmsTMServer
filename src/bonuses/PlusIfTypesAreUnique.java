package bonuses;

import server.Card;
import server.Type;

import java.util.ArrayList;

public class PlusIfTypesAreUnique extends Bonus  {
    public long serialVersionUID = 17;
    public final String text;
    public int howMuch;

    public PlusIfTypesAreUnique(int howMuch) {
        this.text = "+" + howMuch +" if all types in your hand are unique";
        this.howMuch = howMuch;
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        ArrayList<Type> types = new ArrayList<Type>();
        for(Card c: hand){
            if(types.contains(c.type)){
                return 0;
            } else{
                types.add(c.type);
            }
        }

        return howMuch;
    }
}
