package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusIfTypesAreUnique extends Bonus  {
    public long serialVersionUID = 17;
    public final String text;
    public int howMuch;

    public PlusIfTypesAreUnique(int howMuch) {
        this.text = "+" + howMuch +" if every non-BLANKED card is a different type ";
        this.howMuch = howMuch;
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
        sb.append("+" + howMuch);
        sb.append(bonuses.getString("plusIfTypesAreUnique"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        ArrayList<Type> types = new ArrayList<Type>();
        for(Card c: hand){
            if(types.contains(c.type)){
                return 0;
            } else{
                types.add(c.type);
            }
        }

        return howMuch;
    }
}
