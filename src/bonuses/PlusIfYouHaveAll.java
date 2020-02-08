package bonuses;

import server.Card;

import java.util.ArrayList;

public class PlusIfYouHaveAll extends Bonus{
    public long serialVersionUID = 19;
    public final String text;
    private int how_much;
    private ArrayList<Integer> idsOfCardsNeeded;

    public PlusIfYouHaveAll(int hm, ArrayList<Integer> cards){
        this.text = "+" + hm + " if you have " + giveListOfCardsWithSeparator(cards, " and ");
        this.how_much = hm;
        this.idsOfCardsNeeded = cards;
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
                if(id == card.id){
                    completed++;
                }
            }
        }
        if(idsOfCardsNeeded.size() == completed){
            return how_much;
        } else {
            return 0;
        }
    }
}
