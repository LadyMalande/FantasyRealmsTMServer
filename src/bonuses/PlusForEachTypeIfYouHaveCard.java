package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class PlusForEachTypeIfYouHaveCard extends Bonus  {
    public long serialVersionUID = 12;
    public final String text;
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

        this.text = "+" + howMuch + " for each " + listtypes + " if with " + cardname;
        this.howMuch = howMuch;
        this.types = types;
        this.cardid = cardid;
        System.out.println("Card INIT: Text: " + getText());
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
            if(c.name.equals(BigSwitches.switchIdForName(cardid))){
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
