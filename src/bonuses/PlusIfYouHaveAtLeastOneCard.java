package bonuses;

import server.BigSwitches;
import server.Card;

import java.util.ArrayList;

public class PlusIfYouHaveAtLeastOneCard extends Bonus {
    public long serialVersionUID = 21;
    public final String text;
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
        this.text = "+" + how_much + " if with any of these: " + listcards;
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        ArrayList<String> names = new ArrayList<>();
        for(Integer i : idsOfCardsNeeded){
            names.add(BigSwitches.switchIdForName(i));
        }
        for(Card c: hand){
            if(names.contains(c.name)){
                return how_much;
            }
        }
        return 0;
    }
}
