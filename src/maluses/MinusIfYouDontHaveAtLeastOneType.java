package maluses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class MinusIfYouDontHaveAtLeastOneType extends Malus {
    public int priority = 8;
    public String text;
    int howMuch;
    ArrayList<Type> types;

    public MinusIfYouDontHaveAtLeastOneType( int howMuch, ArrayList<Type> types) {
        this.text = howMuch + " unless with at least one " + giveListOfTypesWithSeparator(types, " or ");
        this.howMuch = howMuch;
        this.types = types;
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
        ResourceBundle maluses = ResourceBundle.getBundle("maluses.CardMaluses",loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(howMuch);
        sb.append(maluses.getString("unlessWith"));
        sb.append(" ");
        sb.append(rb.getString("one4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types, "or",locale,4,false));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int getPriority(){ return this.priority; }
    @Override
    public int count(ArrayList<Card> hand) {

        for(Card c: hand){
            if(types.contains(c.type)){
                return 0;
            }
        }
        return howMuch;
    }

    @Override
    public Malus clone() throws CloneNotSupportedException{
        ArrayList<Type> newtypes = new ArrayList<Type>();
        for(Type t: types){
            newtypes.add(t);
        }
        MinusIfYouDontHaveAtLeastOneType newm = new MinusIfYouDontHaveAtLeastOneType(this.howMuch, newtypes);
        //System.out.println("In cloning CardIsDeletedIfYouDontHaveAtLeastOneType: The new types and old types are equal = " + (newm.types == this.types));
        return newm;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        // TODO
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return this.types.stream().anyMatch(type -> types.contains(type));
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand) {
        if(!hand.stream().anyMatch(card -> types.contains(card.getType()))){
            //We dont have the needed type AND we can change into it
            if(types.contains(t)){
                return howMuch;
            }
        }
        return 0;
    }
}
