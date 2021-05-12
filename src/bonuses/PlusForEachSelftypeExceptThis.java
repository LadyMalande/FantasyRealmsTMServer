package bonuses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusForEachSelftypeExceptThis extends Bonus  {
    public long serialVersionUID = 9;
    public int howMuch;
    public final String text;
    public Type type;
    private int thiscardid;

    public PlusForEachSelftypeExceptThis(int how_much, int thiscardid, Type type) {
        this.howMuch = how_much;
        this.type = type;
        this.thiscardid = thiscardid;
        this.text = "+" + how_much + " for each other type " + BigSwitches.switchTypeForName(type) + " in your hand";
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }

    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        return new ArrayList<Type>() {{add(type);}};
    }
    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        return howMuch;
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
        sb.append("+" + howMuch);
        sb.append(" ");
        sb.append(rb.getString("for"));
        sb.append(" ");
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(type)));
        sb.append(" ");
        sb.append(rb.getString("other4" + BigSwitches.switchTypeForGender(type)));
        sb.append(" ");
        sb.append(rb.getString(BigSwitches.switchTypeForName(type).toLowerCase() + "4"));
        sb.append(" ");
        sb.append(rb.getString("inYourHand"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int sum = 0;
        if(hand.stream().filter(card -> card.id == thiscardid).count() > 1 ){
            for(Card c: hand){
                if(c.type.equals(type)){
                    sum += howMuch;
                }
            }
            sum -= howMuch;
        }
        else {
            for (Card c : hand) {
                if (c.type.equals(type) && c.id != thiscardid) {
                    sum += howMuch;
                }
            }
        }
        return sum;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        potential += (state.getNumOfType(type) - 1)* howMuch;
        long oddsOnTable = table.stream().filter(c -> c.getType() == type).count();
        potential += (oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/table.size()) * howMuch;
        long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> c.getType() == type).count();
        potential += (deckSize / unknownCards) * oddsOnDeck/deckSize * howMuch;
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return types.contains(type);
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(type == t){
            return howMuch;
        }
        return 0;
    }
}
