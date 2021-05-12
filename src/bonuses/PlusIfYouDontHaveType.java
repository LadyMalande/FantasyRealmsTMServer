package bonuses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusIfYouDontHaveType extends Bonus  {
    public long serialVersionUID = 18;
    public final String text;
    public int howMuch;
    public Type type;

    public PlusIfYouDontHaveType( int howMuch, Type type) {
        this.text = "+" + howMuch + " if no " + BigSwitches.switchTypeForName(type);
        this.howMuch = howMuch;
        this.type = type;
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }
    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        // You get -howMuch if u have the foribidden type
        return new ArrayList<>() {{add(type);}};
    }
    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        return -howMuch;
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        ResourceBundle rbbonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+" + howMuch);
        sb.append(rbbonuses.getString("ifYouDontHave"));
        sb.append(" ");
            sb.append(rb.getString("no4" + BigSwitches.switchTypeForGender(type)));
            sb.append(" ");
        sb.append(rb.getString(BigSwitches.switchTypeForName(type).toLowerCase() + "4"));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(c.type.equals(type)){
                return 0;
            }
        }
        return howMuch;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        if(!state.getHaveTheseTypes().contains(type)){
            return howMuch;
        }
        potential += howMuch * 1/(state.getNumOfType(type) + 1);

        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return types.contains(type);
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(type == t){
            return -howMuch;
        }
        return 0;
    }
}
