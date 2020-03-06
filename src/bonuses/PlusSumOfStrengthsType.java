package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class PlusSumOfStrengthsType extends Bonus  {
    public long serialVersionUID = 25;
    public final String text;
    public Type type;

    public PlusSumOfStrengthsType(Type type) {
        this.text = "Plus the sum of all cards of type " + BigSwitches.switchTypeForName(type) + " you have";
        this.type = type;
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        for(Card c: hand){
            if(type.equals(c.type)){
                total += c.strength;
            }
        }
        return total;
    }
}
