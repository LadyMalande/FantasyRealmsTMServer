package bonuses;

import server.Card;

import java.util.ArrayList;

public class PlusForEachOdd extends Bonus  {
    public long serialVersionUID = 8;
    public final String text;
    public int how_much;
    private boolean odd;
    private int thiscardid;

    public PlusForEachOdd(int how_much, boolean odd, int thiscardid) {
        this.odd = odd;
        this.thiscardid = thiscardid;
        this.how_much = how_much;
        if(odd) {
            this.text = "+" + how_much + " for each card in your hand with odd strength";
        } else{
            this.text = "+" + how_much + " for each card in your hand with even strength";
        }
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int sum = 0;
        for(Card c: hand){
            if(odd){
                if(((c.strength % 2) != 0) && c.id != thiscardid){
                    sum += how_much;
                }
            }
            else{
                if(((c.strength % 2) == 0) && c.id != thiscardid){
                    sum += how_much;
                }
            }
        }
        return sum;
    }
}
