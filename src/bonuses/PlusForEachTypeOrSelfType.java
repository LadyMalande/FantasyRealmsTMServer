package bonuses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusForEachTypeOrSelfType extends Bonus  {
    public long serialVersionUID = 13;
    public final String text;
    public int howMuch;
    public ArrayList<Type> types;
    public Type selftype;
    public int thiscardid;

    public PlusForEachTypeOrSelfType(int howMuch, int thiscardid, ArrayList<Type> types, Type sefltype) {
        this.howMuch = howMuch;
        this.types = types;
        this.selftype = sefltype;
        this.thiscardid = thiscardid;
        String listtypes = "";
        boolean first = true;
        for(Type type: types){
            if(!first){
                listtypes +=", ";
            }
            listtypes += BigSwitches.switchTypeForName(type);
            first = false;
        }
        text = "+" + howMuch + " for each " + listtypes + " or any other " + BigSwitches.switchTypeForName(selftype) + " you have";
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
        sb.append("+" + howMuch);
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
    public int count(ArrayList<Card> hand) {
        int total = 0;

        for(Card c: hand){
            if(types.contains(c.type) || (c.type.equals(selftype) && c.id != thiscardid)){
                total += howMuch;
            }
        }

       return total;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        long numberOfSuitableCards = hand.stream().filter(c -> types.contains(c.getType())).count() + hand.stream().filter(c -> selftype == c.getType()).count() - 1;
        potential += (numberOfSuitableCards)*howMuch;
        if(numberOfSuitableCards <6){
            long oddsOnTable = table.stream().filter(c -> types.contains(c.getType())).count() + table.stream().filter(c -> selftype == c.getType()).count();
            potential += (oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/table.size()) * howMuch;
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).count() + state.getProbablyInDeck().stream().filter(c -> selftype == c.getType()).count();
            potential += (deckSize / unknownCards) * oddsOnDeck/deckSize * howMuch;
        }
        return potential;
    }
}
