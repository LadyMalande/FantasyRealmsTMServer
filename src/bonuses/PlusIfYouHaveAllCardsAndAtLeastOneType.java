package bonuses;

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
        this.text = "+" + howMuch + " if you have " + giveListOfCardsWithSeparator(idCardsNeeded, " and ") + " and at least one " + giveListOfTypesWithSeparator(types, " or ");
        this.howMuch = howMuch;
        this.idCardsNeeded = idCardsNeeded;
        this.types = types;
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {

        int hascards = 0;
        boolean hasoneofthese = false;
        for(Card c: hand){
            if(idCardsNeeded.contains(c.id)){
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
