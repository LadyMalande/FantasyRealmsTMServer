package bonuses;

import server.BigSwitches;
import server.Card;


import java.util.ArrayList;

public class PlusForEachCard extends Bonus {
    public long serialVersionUID = 7;
    private int how_much;
    private ArrayList<Integer> idsOfCardsNeeded;
    public final String text;

    public PlusForEachCard(int hm, ArrayList<Integer> ids){
        StringBuilder s = new StringBuilder();
        this.how_much = hm;
        this.idsOfCardsNeeded = ids;
        boolean first = true;
        for(Integer i: idsOfCardsNeeded){
            if(!first){
                s.append(", ");
            }
            s.append(BigSwitches.switchIdForName(i));
            first = false;
        }
        this.text = "+" + how_much + " for each of these cards: " + s;
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
            for(int id: idsOfCardsNeeded) {
                if (c.id == (id)) {
                    sum += how_much;
                }
            }
        }
        return sum;
    }
}
