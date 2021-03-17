package bonuses;

import server.Card;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusIfAllAreOdd extends Bonus  {
    public long serialVersionUID = 16;
    public final String text;
    public int how_much;

    public PlusIfAllAreOdd(int how_much, boolean odd) {
        this.how_much = how_much;
        this.odd = odd;
        String oddoreven="odd";
        if(!odd){
            oddoreven = "even";
        }
        this.text = "+" + how_much + " if all cards in your hand have " + oddoreven + " strength";
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }

    public boolean odd;

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+" + how_much);
        if(!odd){
            sb.append(rb.getString("plusIfAllAreEven"));
        } else{
            sb.append(rb.getString("plusIfAllAreOdd"));
        }
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int count = 0;
        for(Card c: hand){
            if(odd){
                if((c.strength % 2) != 0){
                    count++;
                }
            }
            else{
                if((c.strength % 2) == 0){
                    count++;
                }
            }
        }
        if(count == hand.size()){
            return how_much;
        }
        else{
            return 0;
        }
    }
}
