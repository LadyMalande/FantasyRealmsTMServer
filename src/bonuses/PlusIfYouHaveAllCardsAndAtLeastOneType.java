package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class PlusIfYouHaveAllCardsAndAtLeastOneType extends Bonus  {
    public long serialVersionUID = 20;
    public final String text;
    private int howMuch;
    private ArrayList<Integer> idCardsNeeded;
    public ArrayList<Type> types;

    public PlusIfYouHaveAllCardsAndAtLeastOneType( int howMuch, ArrayList<Integer> idCardsNeeded, ArrayList<Type> types) {
        this.text = "+" + howMuch + " if with " + giveListOfCardsWithSeparator(idCardsNeeded, " and ") + " and at least one " + giveListOfTypesWithSeparator(types, " or ");
        this.howMuch = howMuch;
        this.idCardsNeeded = idCardsNeeded;
        this.types = types;
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
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
