package bonuses;

import server.BigSwitches;
import server.Card;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusIfYouHaveCardAndAtLeastOneFrom extends Bonus{
    public long serialVersionUID = 23;
    public final String text;
    private int how_much;
    private ArrayList<Integer> idsOfCardsNeeded;
    private int cardNeeded;

    public PlusIfYouHaveCardAndAtLeastOneFrom(int hm, ArrayList<Integer> cards, int idcardneeded){
        this.how_much = hm;
        this.idsOfCardsNeeded = cards;
        this.cardNeeded = idcardneeded;
        this.text = "+" + how_much + " if with " + BigSwitches.switchIdForName(cardNeeded,"en") + " and at least one: " + giveListOfCardsWithSeparator(cards, " or ", "en",1, false);
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
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames",loc);
        ResourceBundle rbbonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+" + how_much);
        sb.append(rbbonuses.getString("ifYouHave"));
        sb.append(" ");
        sb.append(rb.getString(BigSwitches.switchIdForSimplifiedName(cardNeeded) + "4"));
        sb.append(" ");
        sb.append(rbbonuses.getString("and"));
        sb.append(rbbonuses.getString("atLeastOneOfTheseCards"));
        sb.append(giveListOfCardsWithSeparator(idsOfCardsNeeded, "and",locale,4,false));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        ArrayList<String> names = new ArrayList<>();
        for(Integer i: idsOfCardsNeeded){
            names.add(BigSwitches.switchIdForName(i, "en"));
        }
        boolean hascard = false;
        boolean hasoneofthese = false;
        for(Card c: hand){
            if(c.name.equals(BigSwitches.switchIdForName(cardNeeded, "en"))){
                hascard = true;
            }
            else if(names.contains(c.name)){
                hasoneofthese = true;
            }
            if(hascard == true && hasoneofthese == true){
                return how_much;
            }
        }
        return 0;
    }
}
