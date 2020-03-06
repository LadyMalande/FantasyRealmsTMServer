package maluses;

import server.Card;
import server.Type;

import java.util.ArrayList;

public class DeletesAllTypeExceptCard extends Malus {
    public final String text;
    public int thiscardid;
    public ArrayList<Type> types;
    public ArrayList<Integer> cards;

    public DeletesAllTypeExceptCard( int thiscardid, ArrayList<Type> types, ArrayList<Integer> cards) {
        this.thiscardid = thiscardid;
        this.text = "Blanks all "+ giveListOfTypesWithSeparator(types, ", ")+" except " + giveListOfCardsWithSeparator(cards, ", ");
        this.types = types;
        this.cards = cards;
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToRemove) {
        if(hand.stream().anyMatch(card -> card.id == this.thiscardid)) {
            ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(hand);
            for (Card c : copyDeckToMakeChanges) {
                if (types.contains(c.type) && !cards.contains(c.id)) {
                    if(!whatToRemove.contains(c)){
                        whatToRemove.add(c);
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        if(hand.stream().anyMatch(card -> card.id == this.thiscardid)) {
            ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(hand);
            for (Card c : copyDeckToMakeChanges) {
                if (types.contains(c.type) && !cards.contains(c.id)) {
                    hand.remove(c);
                }
            }
        }
        return 0;
    }
}
