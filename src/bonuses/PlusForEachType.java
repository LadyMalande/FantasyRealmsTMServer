package bonuses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusForEachType extends Bonus  {
    public long serialVersionUID = 10;
    public final String text;
    public ArrayList<Type> types;
    public int how_much;

    public PlusForEachType( ArrayList<Type> types, int how_much) {
        this.types = types;
        this.how_much = how_much;
        StringBuilder s = new StringBuilder();
        boolean first = true;
        for(Type t: types){
            if(!first){
                s.append(" or ");
            }
            s.append(BigSwitches.switchTypeForName(t));
            first = false;
        }
        this.text = "+" + how_much + " for each type " + s;
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
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types,"or",locale, 4,false));
        sb.append(" ");
        sb.append(rb.getString("inYourHand"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int sum = 0;
        for(Card c: hand){
                if (types.contains(c.type)) {
                    sum += how_much;
                }
        }

        return sum;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        long numberOfSuitableCards = hand.stream().filter(c -> types.contains(c.getType())).count();
        potential += (numberOfSuitableCards)*how_much;
        if(numberOfSuitableCards <6){
            long oddsOnTable = table.stream().filter(c -> types.contains(c.getType())).count();
            potential += (oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/table.size()) * how_much;
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).count();
            potential += (deckSize / unknownCards) * oddsOnDeck/deckSize * how_much;
        }
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return this.types.stream().anyMatch(type -> types.contains(type));

    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(types.contains(t)){
            return how_much;
        }
        return 0;
    }
}
