package maluses;

import server.Card;
import server.Type;

import java.util.ArrayList;

public class MinusIfYouDontHaveAtLeastOneType extends Malus {
    public int priority = 8;
    public String text;
    int howMuch;
    ArrayList<Type> types;

    public MinusIfYouDontHaveAtLeastOneType( int howMuch, ArrayList<Type> types) {
        this.text = howMuch + " unless with at least one " + giveListOfTypesWithSeparator(types, " or ");
        this.howMuch = howMuch;
        this.types = types;
        //System.out.println("Card INIT: Text: " + getText());
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

    @Override
    public Malus clone() throws CloneNotSupportedException{
        ArrayList<Type> newtypes = new ArrayList<Type>();
        for(Type t: types){
            newtypes.add(t);
        }
        MinusIfYouDontHaveAtLeastOneType newm = new MinusIfYouDontHaveAtLeastOneType(this.howMuch, newtypes);
        //System.out.println("In cloning CardIsDeletedIfYouDontHaveAtLeastOneType: The new types and old types are equal = " + (newm.types == this.types));
        return newm;
    }
}
