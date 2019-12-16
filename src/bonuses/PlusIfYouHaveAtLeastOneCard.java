package bonuses;

import server.BigSwitches;
import server.Card;

import java.util.ArrayList;

public class PlusIfYouHaveAtLeastOneCard extends Bonus {
    public String text;
    private int how_much;
    private ArrayList<Integer> idsOfCardsNeeded;

    public PlusIfYouHaveAtLeastOneCard(int hm, ArrayList<Integer> cards){

        this.how_much = hm;
        this.idsOfCardsNeeded = cards;

        String listcards = "";
        boolean first = true;
        for(Integer c: cards){
            if(!first){
                listcards +=" or ";
            }
            listcards += BigSwitches.switchIdForName(c);
            first = false;
        }
        this.text = "+" + how_much + " if you have any of these: " + listcards;
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(idsOfCardsNeeded.contains(c.id)){
                return how_much;
            }
        }
        return 0;
    }
}
