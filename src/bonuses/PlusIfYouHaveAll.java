package bonuses;

import server.BigSwitches;
import server.Card;

import java.util.ArrayList;

public class PlusIfYouHaveAll extends Bonus{
    public long serialVersionUID = 19;
    public final String text;
    private int how_much;
    private ArrayList<Integer> idsOfCardsNeeded;

    public PlusIfYouHaveAll(int hm, ArrayList<Integer> cards){
        this.text = "+" + hm + " if with " + giveListOfCardsWithSeparator(cards, " and ");
        this.how_much = hm;
        this.idsOfCardsNeeded = cards;
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int completed = 0;
        for(int id: idsOfCardsNeeded){
            for(Card card: hand){
                if(BigSwitches.switchIdForName(id).equals(card.name)){
                    completed++;
                }
            }
        }
        // if I have more princesses I still should get points for Unicorn
        if(idsOfCardsNeeded.size() <= completed){
            return how_much;
        } else {
            return 0;
        }
    }
}
