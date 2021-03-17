package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusSumOfStrengthsType extends Bonus  {
    public long serialVersionUID = 25;
    public final String text;
    public Type type;

    public PlusSumOfStrengthsType(Type type) {
        this.text = "Plus the sum of all cards of type " + BigSwitches.switchTypeForName(type) + " you have";
        this.type = type;
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
        sb.append(bonuses.getString("plusSumOfStrengthsType"));
        sb.append(rb.getString(BigSwitches.switchTypeForName(type).toLowerCase() + "2pl"));
        sb.append(bonuses.getString("plusSumOfStrengthsType2"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        for(Card c: hand){
            if(type.equals(c.type)){
                total += c.strength;
            }
        }
        return total;
    }
}
