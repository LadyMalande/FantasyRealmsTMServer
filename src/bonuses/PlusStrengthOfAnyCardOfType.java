package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.*;

public class PlusStrengthOfAnyCardOfType extends Bonus  {
    public long serialVersionUID = 24;
    public final String text;
    public List<Type> types;

    public PlusStrengthOfAnyCardOfType(List<Type> types) {
        String listtypes = "";
        boolean first = true;
        for(Type type: types){
            if(!first){
                listtypes +=" or ";
            }
            listtypes += BigSwitches.switchTypeForName(type);
            first = false;
        }
        this.text = "Plus the strength of any card of type " + listtypes + " in your hand";
        this.types = types;
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
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses", loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes", loc);
        sb.append(bonuses.getString("plusStrengthOfAnyCardOfType"));
        sb.append(" ");
        sb.append(rb.getString("any2" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator((ArrayList<Type>) types, ", ", locale, 2, false));
        sb.append(" ");
        sb.append(bonuses.getString("plusStrengthOfAnyCardOfType2"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int max_on_hand = 0;
        for(Card c: hand){
            if(types.contains(c.type) && c.strength>max_on_hand){
                max_on_hand = c.strength;
            }
        }
        return max_on_hand;

    }
}
