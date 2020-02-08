package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class PlusForEachSelftypeExceptThis extends Bonus  {
    public long serialVersionUID = 9;
    public int how_much;
    public final String text;
    public Type type;
    private int thiscardid;

    public PlusForEachSelftypeExceptThis(int how_much, int thiscardid, Type type) {
        this.how_much = how_much;
        this.type = type;
        this.thiscardid = thiscardid;
        this.text = "+" + how_much + " for each other card of type " + BigSwitches.switchTypeForName(type) + " in your hand";
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int sum = 0;
        if(hand.stream().filter(card -> card.id == thiscardid).count() > 1 ){
            for(Card c: hand){
                if(c.type.equals(type)){
                    sum += how_much;
                }
            }
            sum -= how_much;
        }
        else {
            for (Card c : hand) {
                if (c.type.equals(type) && c.id != thiscardid) {
                    sum += how_much;
                }
            }
        }
        return sum;
    }
}
