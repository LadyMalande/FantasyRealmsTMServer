package bonuses;

import server.BigSwitches;
import server.Card;

import java.util.ArrayList;

public class PlusIfYouHaveCardAndAtLeastOneFrom extends Bonus{
    public String text;
    private int how_much;
    private ArrayList<Integer> idsOfCardsNeeded;
    private int cardNeeded;

    public PlusIfYouHaveCardAndAtLeastOneFrom(int hm, ArrayList<Integer> cards, int idcardneeded){
        this.how_much = hm;
        this.idsOfCardsNeeded = cards;
        this.cardNeeded = idcardneeded;
        this.text = "+" + how_much + " if you have " + BigSwitches.switchIdForName(cardNeeded) + " and at least one of these: " + giveListOfCardsWithSeparator(cards, " or ");
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        boolean hascard = false;
        boolean hasoneofthese = false;
        for(Card c: hand){
            if(c.id == cardNeeded){
                hascard = true;
            }
            else if(idsOfCardsNeeded.contains(c.id)){
                hasoneofthese = true;
            }
            if(hascard == true && hasoneofthese == true){
                return how_much;
            }
        }
        return 0;
    }
}
