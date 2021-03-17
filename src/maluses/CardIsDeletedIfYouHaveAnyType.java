package maluses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class CardIsDeletedIfYouHaveAnyType extends Malus {
    public int priority = 7;
    public String text;
    private int thiscardid;
    public ArrayList<Type> types;

    public CardIsDeletedIfYouHaveAnyType(int thiscardid,  ArrayList<Type> types) {
        this.text = "Blanked with any " + giveListOfTypesWithSeparator(types, " or " );
        this.thiscardid = thiscardid;
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
        ResourceBundle maluses = ResourceBundle.getBundle("maluses.CardMaluses",loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(maluses.getString("blankedWith"));
        sb.append(" ");
        sb.append(rb.getString("any4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types, "or",locale,4,false));
        sb.append(".");
        return sb.toString();
    }
    @Override
    public int getPriority(){ return this.priority; }
    @Override
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToRemove) {
        boolean delete = false;
        //System.out.println("Resolving card "+ BigSwitches.switchIdForName(thiscardid));
        for(Card c: hand){
            if (!whatToRemove.contains(c)) {
                if(types.contains(c.type)){
                    delete = true;
                    //System.out.println("c.type==" + BigSwitches.switchTypeForName(c.type) + " je v seznamu types");
                    break;
                }
            }
        }
        if(delete){

            //whatToRemove.add(hand.stream().filter(cardOnHand -> thiscardid == (cardOnHand.id)).findFirst().get());
            hand.removeIf(x -> x.id == thiscardid);
            //System.out.println("Card with id " + thiscardid + " was put to whatToremove, now cards in hand: " + hand.size());
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
        CardIsDeletedIfYouHaveAnyType newm = new CardIsDeletedIfYouHaveAnyType(this.thiscardid, newtypes);
        //System.out.println("In cloning CardIsDeletedIfYouHaveAnyType: The new types and old types are equal = " + (newm.types == this.types));
        return newm;
    }
}
