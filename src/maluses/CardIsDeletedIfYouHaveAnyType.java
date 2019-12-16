package maluses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class CardIsDeletedIfYouHaveAnyType extends Malus {
    public int priority = 7;
    public String text;
    private int thiscardid;
    public ArrayList<Type> types;

    public CardIsDeletedIfYouHaveAnyType(int thiscardid,  ArrayList<Type> types) {
        this.text = "This card is deleted if you have any " + giveListOfTypesWithSeparator(types, " or " );
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
        boolean delete = false;
        for(Card c: hand){
            if(types.contains(c.type)){
                delete = true;
                break;
            }
        }
        if(delete){
            hand.removeIf(x -> x.id == thiscardid);
        }
        return 0;
    }
}
