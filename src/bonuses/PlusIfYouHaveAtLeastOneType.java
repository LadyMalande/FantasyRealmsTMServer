package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusIfYouHaveAtLeastOneType extends Bonus  {
    public long serialVersionUID = 22;
    public final String text;
    private int how_much;
    private ArrayList<Type> types;

    public PlusIfYouHaveAtLeastOneType(int hm, ArrayList<Type> types){

        this.how_much = hm;
        this.types = types;

        String listtypes = "";
        boolean first = true;
        for(Type t: types){
            if(!first){
                listtypes +=" or ";
            }
            listtypes += BigSwitches.switchTypeForName(t);
            first = false;
        }
        this.text = "+" + how_much + " if you have any type " + listtypes;
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
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append("+" + how_much);
        sb.append(bonuses.getString("ifYouHave"));
        sb.append(" ");
        sb.append(rb.getString("atLeast"));
        sb.append(" ");
        sb.append(rb.getString("one4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types, "or",locale,4,false));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(types.contains(c.type)){
                return how_much;
            }
        }
        return 0;
    }
}
