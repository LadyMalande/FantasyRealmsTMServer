package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import javax.imageio.plugins.bmp.BMPImageWriteParam;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusForEachTypeAndForEachCard extends Bonus  {
    public long serialVersionUID = 11;
    public final String text;
    private int how_much;
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
        this. text = "+" + hm + " for each " + listtypes + " and each " + listcards;
        this.how_much = hm;
        this.types = types;
        this.cards = cards;
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
        sb.append("+" + how_much);
        sb.append(" ");
        sb.append(rb.getString("for"));
        sb.append(" ");
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types,", ",locale, 4,false));
        if(cards != null){
            sb.append(", ");
            sb.append(giveListOfCardsWithSeparator(cards,", ",locale, 4,false));
        }
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        ArrayList<String> names = new ArrayList<>();
        for(Integer i: cards){
            names.add(BigSwitches.switchIdForName(i));
        }
        for(Card c: hand){
                if (types.contains(c.type)) {
                    total += how_much;
                }
                if (names.contains(c.name)) {
                    total += how_much;
                }
        }

        return total;
    }
}
