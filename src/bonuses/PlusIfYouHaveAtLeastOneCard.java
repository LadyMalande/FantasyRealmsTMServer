package bonuses;

import server.BigSwitches;
import server.Card;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusIfYouHaveAtLeastOneCard extends Bonus {
    public long serialVersionUID = 21;
    public final String text;
    private int how_much;
    private ArrayList<Integer> idsOfCardsNeeded;

    public PlusIfYouHaveAtLeastOneCard(int hm, ArrayList<Integer> cards){

        this.how_much = hm;
        this.idsOfCardsNeeded = cards;

        String listcards = "";
        boolean first = true;
        for(Integer c: cards){
            if(!first){
                listcards +=" or ";
            }
            listcards += BigSwitches.switchIdForName(c);
            first = false;
        }
        this.text = "+" + how_much + " if with any of these: " + listcards;
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
        sb.append("+" + how_much);
        sb.append(rbbonuses.getString("ifYouHave"));
        sb.append(" ");
        sb.append(rbbonuses.getString("atLeastOneOfTheseCards"));
        sb.append(" ");
        sb.append(giveListOfCardsWithSeparator(idsOfCardsNeeded, "or",locale, 4,false));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        ArrayList<String> names = new ArrayList<>();
        for(Integer i : idsOfCardsNeeded){
            names.add(BigSwitches.switchIdForName(i));
        }
        for(Card c: hand){
            if(names.contains(c.name)){
                return how_much;
            }
        }
        return 0;
    }
}
