package maluses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class CardIsDeletedIfYouDontHaveAtLeastOneType extends Malus {
    public int priority = 7;
    public String text;
    int thiscardid;
    public ArrayList<Type> types;

    public CardIsDeletedIfYouDontHaveAtLeastOneType( int thiscardid, ArrayList<Type> types) {
        this.text = "Blanked unless with at least one " + giveListOfTypesWithSeparator(types, " or ");
        this.thiscardid = thiscardid;
        this.types = types;
        //System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }
    @Override
    public int getPriority(){ return this.priority; }
    @Override
    public int count(ArrayList<Card> hand,  ArrayList<Card> whatToRemove) {
        boolean delete = true;
        //System.out.println("Resolving card "+ BigSwitches.switchIdForName(thiscardid));
        for(Card c: hand){
            if(!whatToRemove.contains(c)){
                if(types.contains(c.type)){
                    delete = false;
                    //System.out.println("c.type==" + BigSwitches.switchTypeForName(c.type) + " je v seznamu types");
                } else{
                //System.out.println("c.type==" + BigSwitches.switchTypeForName(c.type) + " NENI v seznamu types");
                }
            }
        }
        if(delete){
            //whatToRemove.add(hand.stream().filter(cardOnHand -> thiscardid == (cardOnHand.id)).findFirst().get());
            hand.removeIf(x -> x.id == thiscardid);
            System.out.println("Card with id " + thiscardid + " was put to whatToDelete, now cards in hand: " + hand.size());
        }
        return 0;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        return 0;
    }

    @Override
    public Malus clone() throws CloneNotSupportedException{


        ArrayList<Type> newtypes = new ArrayList<Type>();
        for(Type t: types){
            newtypes.add(t);
        }
        CardIsDeletedIfYouDontHaveAtLeastOneType newm = new CardIsDeletedIfYouDontHaveAtLeastOneType(this.thiscardid, newtypes);
        //System.out.println("In cloning CardIsDeletedIfYouDontHaveAtLeastOneType: The new types and old types are equal = " + (newm.types == this.types));
        return newm;
    }
}
