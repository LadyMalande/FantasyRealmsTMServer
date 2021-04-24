package maluses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class MinusForEachOtherSelftypeOrType extends Malus {
    public int priority = 8;
    public String text;
    public int howMuch;
    public ArrayList<Type> types;
    public Type selftype;
    public int thiscardid;

    public MinusForEachOtherSelftypeOrType(int howMuch, ArrayList<Type> types, Type selftype, int thiscardid) {
        if(types == null){
            this.text =  howMuch + " for other " + BigSwitches.switchTypeForName(selftype);
        }else {
            this.text = howMuch + " for each " + giveListOfTypesWithSeparator(types, ", ") + " or other " + BigSwitches.switchTypeForName(selftype);
        }
        this.howMuch = howMuch;
        this.types = types;
        this.selftype = selftype;
        this.thiscardid = thiscardid;
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }

    @Override
    public String getText(){
        return this.text;
    }
    @Override
    public  ArrayList<Type> getTypes(){ return this.types; }
    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(howMuch);
        sb.append(" ");
        sb.append(rb.getString("for"));
        sb.append(" ");
        if(types != null){
            sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
            sb.append(" ");
            sb.append(giveListOfTypesWithSeparator(types, "or",locale,4,false));
            sb.append(" ");
            sb.append(rb.getString("or"));
            sb.append(" ");
        }
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(selftype)));
        sb.append(" ");
        sb.append(rb.getString("other4" + BigSwitches.switchTypeForGender(selftype)));
        sb.append(" ");
        sb.append(rb.getString(BigSwitches.switchTypeForName(selftype).toLowerCase() + "4"));
        sb.append(".");
        return sb.toString();
    }
    @Override
    public int getPriority(){ return this.priority; }
    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        for(Card c: hand){
            if(c.type.equals(selftype) && c.id != thiscardid){
                total += howMuch;
            }
            if(types != null && !types.isEmpty()){
                if (types.contains(c.type) && (c.id != thiscardid)) {
                    total += howMuch;
                }
            }
        }
        //System.out.println("The card with malus contributed with " +  total);
        return total;
    }

    @Override
    public Malus clone() throws CloneNotSupportedException{

        ArrayList<Type> newtypes = new ArrayList<Type>();
        if(this.types != null)
        for(Type t: this.types){
            newtypes.add(t);
        }
        else{
            newtypes = null;
        }
        MinusForEachOtherSelftypeOrType newm = new MinusForEachOtherSelftypeOrType(this.howMuch, newtypes, this.selftype, this.thiscardid);
        //System.out.println("In cloning CardIsDeletedIfYouDontHaveAtLeastOneType: The new types and old types are equal = " + (newm.types == this.types));
        return newm;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        // TODO
        return potential;
    }

    public boolean reactsWithTypes(ArrayList<Type> types) {
        return this.types.stream().anyMatch(type -> types.contains(type))
                || types.contains(selftype);
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand) {
        if(types.contains(t) || selftype == t){
            return howMuch;
        }
        return 0;
    }
}
