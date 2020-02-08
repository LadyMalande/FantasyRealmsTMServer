package maluses;

import server.Card;
import server.Type;

import java.util.ArrayList;

public class MinusIfYouDontHaveAtLeastOneType extends Malus {
    public int priority = 8;
    public final String text;
    int howMuch;
    ArrayList<Type> types;

    public MinusIfYouDontHaveAtLeastOneType( int howMuch, ArrayList<Type> types) {
        this.text = howMuch + " if you don't have at least one " + giveListOfTypesWithSeparator(types, " or ");
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

        for(Card c: hand){
            if(types.contains(c.type)){
                return 0;
            }
        }
        return howMuch;
    }
}
