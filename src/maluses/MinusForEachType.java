package maluses;

import server.Card;
import server.Type;

import java.util.ArrayList;

public class MinusForEachType extends Malus {
    public int priority = 8;
    public final String text;
    public int howMuch;
    public ArrayList<Type> types;

    public MinusForEachType( int howMuch, ArrayList<Type> types) {
        this.text = howMuch + " for each " + giveListOfTypesWithSeparator(types, " or ");
        this.howMuch = howMuch;
        this.types = types;
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
            if(types.contains(c.type)){
                total += howMuch;
            }
        }
        return total;
    }
}
