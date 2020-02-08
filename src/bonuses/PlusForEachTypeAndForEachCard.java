package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import javax.imageio.plugins.bmp.BMPImageWriteParam;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlusForEachTypeAndForEachCard extends Bonus  {
    public long serialVersionUID = 11;
    public final String text;
    private int how_Much;
    public ArrayList<Type> types;
    public ArrayList<Integer> cards;

    public PlusForEachTypeAndForEachCard(int hm, ArrayList<Type> types, ArrayList<Integer> cards){
        String listcards = "";
        boolean first = true;
        for(Integer c: cards){
            if(!first){
                listcards +=", ";
            }
            listcards += BigSwitches.switchIdForName(c);
            first = false;
        }

        String listtypes = "";
        first = true;
        for(Type type: types){
            if(!first){
                listtypes +=", ";
            }
            listtypes += BigSwitches.switchTypeForName(type);
            first = false;
        }
        this. text = "+" + hm + " for each card of type " + listtypes + " you have and for each of theese you have: " + listcards;
        this.how_Much = hm;
        this.types = types;
        this.cards = cards;
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        for(Card c: hand){
                if (types.contains(c.type)) {
                    total += how_Much;
                }
                if (cards.contains(c.id)) {
                    total += how_Much;
                }
        }

        return total;
    }
}
