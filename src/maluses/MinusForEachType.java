package maluses;

import server.Card;
import server.Type;

import java.util.ArrayList;

public class MinusForEachType extends Malus {
    public int priority = 8;
    public String text;
    public int howMuch;
    public ArrayList<Type> types;

    public MinusForEachType( int howMuch, ArrayList<Type> types) {
        this.text = howMuch + " for each " + giveListOfTypesWithSeparator(types, " or ");
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

        int total = 0;
        for(Card c: hand){
            if(types.contains(c.type)){
                total += howMuch;
                System.out.println("The hand contains type " + c.type + " and thus will be given " + howMuch);
            }
        }
        //System.out.println("The card with malus contributed with " +  total);
        return total;
    }

    @Override
    public Malus clone() throws CloneNotSupportedException{
        ArrayList<Type> newtypes = new ArrayList<Type>();
        for(Type t: types){
            newtypes.add(t);
        }
        MinusForEachType newm = new MinusForEachType(this.howMuch, newtypes);
        //System.out.println("In cloning CardIsDeletedIfYouDontHaveAtLeastOneType: The new types and old types are equal = " + (newm.types == this.types));
        return newm;
    }
}
