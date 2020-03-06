package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class PlusIfYouDontHaveType extends Bonus  {
    public long serialVersionUID = 18;
    public final String text;
    public int howMuch;
    public Type type;

    public PlusIfYouDontHaveType( int howMuch, Type type) {
        this.text = "+" + howMuch + " if no " + BigSwitches.switchTypeForName(type);
        this.howMuch = howMuch;
        this.type = type;
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(c.type.equals(type)){
                return 0;
            }
        }
        return howMuch;
    }
}
