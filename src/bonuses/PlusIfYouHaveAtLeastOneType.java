package bonuses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusIfYouHaveAtLeastOneType extends Bonus  {
    public long serialVersionUID = 22;
    public final String text;
    private int how_much;
    private ArrayList<Type> types;

    public PlusIfYouHaveAtLeastOneType(int hm, ArrayList<Type> types){

        this.how_much = hm;
        this.types = types;

        String listtypes = "";
        boolean first = true;
        for(Type t: types){
            if(!first){
                listtypes +=" or ";
            }
            listtypes += BigSwitches.switchTypeForName(t);
            first = false;
        }
        this.text = "+" + how_much + " if you have any type " + listtypes;
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
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append("+" + how_much);
        sb.append(bonuses.getString("ifYouHave"));
        sb.append(" ");
        sb.append(rb.getString("atLeast"));
        sb.append(" ");
        sb.append(rb.getString("one4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types, "or",locale,4,false));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(types.contains(c.type)){
                return how_much;
            }
        }
        return 0;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potentialTable, potentialDeck;

        if(hand.stream().filter(c -> types.contains(c.getType())).count() >= 1){
            return how_much;
        }
        long suitableOnTable = table.stream().filter(c -> types.contains(c.getType())).count();
        potentialTable = Math.min(suitableOnTable - state.getNumberOfEnemies() * suitableOnTable / table.size() * how_much, how_much);
        long suitableInDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).count();
        potentialDeck = Math.min((deckSize / unknownCards) * suitableInDeck / deckSize * how_much, how_much);
        return Math.max(potentialTable, potentialDeck);
    }
}
