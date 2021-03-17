package maluses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class DeletesAllTypeOrOtherSelftype  extends Malus{
    public String text;
    public ArrayList<Type> types;
    public Type selftype;
    private int thiscardid;

    public DeletesAllTypeOrOtherSelftype(ArrayList<Type> types, Type type, int thiscardid) {
        this.text = "Blanks all " + giveListOfTypesWithSeparator(types, ", ") + " or other " + BigSwitches.switchTypeForName(type);
        this.types = types;
        this.selftype = type;
        this.thiscardid = thiscardid;
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
        sb.append(maluses.getString("blanks"));
        sb.append(" ");
        if(types != null){
            sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
            sb.append(" ");
            sb.append(giveListOfTypesWithSeparator(types, "or",locale,4,false));
            sb.append(" ");
            sb.append(rb.getString("or"));
            sb.append(" ");
        }
        sb.append(rb.getString("other4" + BigSwitches.switchTypeForGender(selftype)));
        sb.append(" ");
        sb.append(rb.getString(BigSwitches.switchTypeForName(selftype).toLowerCase() + "4"));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToDelete) {
        ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>();
        copyDeckToMakeChanges.addAll(hand);
        for(Card c: copyDeckToMakeChanges){
            if(types.contains(c.type) || (selftype.equals(c.type) && thiscardid != c.id)){

                if(!whatToDelete.contains(c)) {
                    whatToDelete.add(c);
                }
            }
        }
        return 0;
    }
    @Override
    public int count(ArrayList<Card> hand) {
        /*
        ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>();
        copyDeckToMakeChanges.addAll(hand);
        for(Card c: copyDeckToMakeChanges){
            if(types.contains(c.type) || (selftype.equals(c.type) && thiscardid != c.id)){
                hand.remove(c);

            }
        }

         */
        return 0;
    }

    @Override
    public Malus clone() throws CloneNotSupportedException{
        ArrayList<Type> newtypes = new ArrayList<Type>();
        for(Type t: types){
            newtypes.add(t);
        }
        DeletesAllTypeOrOtherSelftype newm = new DeletesAllTypeOrOtherSelftype(newtypes,this.selftype,this.thiscardid);
        //System.out.println("In cloning DeletesAllTypeOrOtherSelftype: The new types and old types are equal = " + (newm.types == this.types));
        return newm;
    }
}
