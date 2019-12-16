package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class PlusForEachTypeIfYouHaveCard extends Bonus  {
    public String text;
    public int howMuch;
    public ArrayList<Type> types;
    public int cardid;

    public PlusForEachTypeIfYouHaveCard(int howMuch, ArrayList<Type> types, int cardid) {
        String cardname = "";
        cardname = BigSwitches.switchIdForName(cardid);

        String listtypes = "";
        boolean first = true;
        for(Type type: types){
            if(!first){
                listtypes +=", ";
            }
            listtypes += BigSwitches.switchTypeForName(type);
            first = false;
        }

        this.text = "+" + howMuch + " for each " + types + " if you have " + cardname;
        this.howMuch = howMuch;
        this.types = types;
        this.cardid = cardid;
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        boolean countit = false;
        for(Card c: hand){
            if(c.id == cardid){
                countit = true;
            }
                if (types.contains(c.type)) {
                    total += howMuch;
                }

        }
        if(countit){
            return total;
        } else{
            return 0;
        }
    }
}
