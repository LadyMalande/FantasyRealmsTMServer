package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

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
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        ResourceBundle rbcards = ResourceBundle.getBundle("server.CardNames",loc);
        sb.append("+" + howMuch);
        sb.append(" ");
        sb.append(rb.getString("for"));
        sb.append(" ");
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types,", ",locale, 4,false));
            sb.append(rb.getString("ifYouHave"));
        sb.append(" ");
            sb.append(rbcards.getString(BigSwitches.switchIdForSimplifiedName(cardid) + "4"));
        sb.append(".");
        return sb.toString();
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
