package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusIfYouHaveAllCardsAndAtLeastOneType extends Bonus  {
    public long serialVersionUID = 20;
    public final String text;
    private int howMuch;
    private ArrayList<Integer> idCardsNeeded;
    public ArrayList<Type> types;

    public PlusIfYouHaveAllCardsAndAtLeastOneType( int howMuch, ArrayList<Integer> idCardsNeeded, ArrayList<Type> types) {
        this.text = "+" + howMuch + " if with " + giveListOfCardsWithSeparator(idCardsNeeded, " and ", "en",1,false) + " and at least one " + giveListOfTypesWithSeparator(types, " or ");
        this.howMuch = howMuch;
        this.idCardsNeeded = idCardsNeeded;
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
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        ResourceBundle rbbonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+" + howMuch);
        sb.append(rbbonuses.getString("ifYouHave"));
        sb.append(" ");
        sb.append(giveListOfCardsWithSeparator(idCardsNeeded, "and",locale,4,false));
        sb.append(rb.getString("and"));
        sb.append(rb.getString("atLeast"));
        sb.append(rb.getString("one4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(giveListOfTypesWithSeparator(types, "or",locale, 4,false));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        ArrayList<String> names = new ArrayList<>();
        for(Integer i: idCardsNeeded){
            names.add(BigSwitches.switchIdForName(i));
        }
        int hascards = 0;
        boolean hasoneofthese = false;
        for(Card c: hand){
            if(names.contains(c.name)){
                hascards++;
            }
            else if(types.contains(c.type)){
                hasoneofthese = true;
            }
            if(hascards == idCardsNeeded.size() && hasoneofthese){
                return howMuch;
            }
        }
        return 0;
    }
}
