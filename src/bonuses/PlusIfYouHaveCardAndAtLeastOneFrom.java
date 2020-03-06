package bonuses;

import server.BigSwitches;
import server.Card;

import java.util.ArrayList;

public class PlusIfYouHaveCardAndAtLeastOneFrom extends Bonus{
    public long serialVersionUID = 23;
    public final String text;
    private int how_much;
    private ArrayList<Integer> idsOfCardsNeeded;
    private int cardNeeded;

    public PlusIfYouHaveCardAndAtLeastOneFrom(int hm, ArrayList<Integer> cards, int idcardneeded){
        this.how_much = hm;
        this.idsOfCardsNeeded = cards;
        this.cardNeeded = idcardneeded;
        this.text = "+" + how_much + " if with " + BigSwitches.switchIdForName(cardNeeded) + " and at least one: " + giveListOfCardsWithSeparator(cards, " or ");
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        ArrayList<String> names = new ArrayList<>();
        for(Integer i: idsOfCardsNeeded){
            names.add(BigSwitches.switchIdForName(i));
        }
        boolean hascard = false;
        boolean hasoneofthese = false;
        for(Card c: hand){
            if(c.name.equals(BigSwitches.switchIdForName(cardNeeded))){
                hascard = true;
            }
            else if(names.contains(c.name)){
                hasoneofthese = true;
            }
            if(hascard == true && hasoneofthese == true){
                return how_much;
            }
        }
        return 0;
    }
}
