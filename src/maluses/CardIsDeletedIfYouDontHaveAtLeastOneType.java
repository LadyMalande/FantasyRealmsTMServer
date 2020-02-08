package maluses;

import server.Card;
import server.Type;

import java.util.ArrayList;

public class CardIsDeletedIfYouDontHaveAtLeastOneType extends Malus {
    public int priority = 7;
    public final String text;
    int thiscardid;
    public ArrayList<Type> types;

    public CardIsDeletedIfYouDontHaveAtLeastOneType( int thiscardid, ArrayList<Type> types) {
        this.text = "This card is deleted if you don't have at least one " + giveListOfTypesWithSeparator(types, " or ");
        this.thiscardid = thiscardid;
        this.types = types;
    }

    @Override
    public String getText(){
        return this.text;
    }
    @Override
    public int getPriority(){ return this.priority; }
    @Override
    public int count(ArrayList<Card> hand) {
        boolean delete = true;
        for(Card c: hand){
            if(types.contains(c.type)){
                delete = false;
            }
        }
        if(delete){
            hand.removeIf(x -> x.id == thiscardid);
        }
        return 0;
    }
}
