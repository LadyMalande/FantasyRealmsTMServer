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
        this.text = "Deletes all "+ giveListOfTypesWithSeparator(types, ", ")+" except " + giveListOfCardsWithSeparator(cards, ", ");
        this.types = types;
        this.cards = cards;
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        if(!hand.stream().filter(card -> card.id == this.thiscardid).findAny().isEmpty()) {
            ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>();
            copyDeckToMakeChanges.addAll(hand);
            for (Card c : copyDeckToMakeChanges) {
                if (types.contains(c.type) && !cards.contains(c.id)) {
                    hand.remove(c);
                }
            }
            return 0;
        } else{
            return 0;
        }
    }
}
