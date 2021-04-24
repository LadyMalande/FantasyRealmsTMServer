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
    public int how_much;
    public final String text;
    public Type type;
    private int thiscardid;

    public PlusForEachSelftypeExceptThis(int how_much, int thiscardid, Type type) {
        this.how_much = how_much;
        this.type = type;
        this.thiscardid = thiscardid;
        this.text = "+" + how_much + " for each other type " + BigSwitches.switchTypeForName(type) + " in your hand";
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
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append("+" + how_much);
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
                    sum += how_much;
                }
            }
            sum -= how_much;
        }
        else {
            for (Card c : hand) {
                if (c.type.equals(type) && c.id != thiscardid) {
                    sum += how_much;
                }
            }
        }
        return sum;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        potential += (state.getNumOfType(type) - 1)*how_much;
        long oddsOnTable = table.stream().filter(c -> c.getType() == type).count();
        potential += (oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/table.size()) * how_much;
        long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> c.getType() == type).count();
        potential += (deckSize / unknownCards) * oddsOnDeck/deckSize * how_much;
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return types.contains(type);
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(type == t){
            return how_much;
        }
        return 0;
    }
}
