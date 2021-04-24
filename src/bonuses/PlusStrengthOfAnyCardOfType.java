package bonuses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.*;
import java.util.stream.Collectors;

public class PlusStrengthOfAnyCardOfType extends Bonus  {
    public long serialVersionUID = 24;
    public final String text;
    public List<Type> types;

    public PlusStrengthOfAnyCardOfType(List<Type> types) {
        String listtypes = "";
        boolean first = true;
        for(Type type: types){
            if(!first){
                listtypes +=" or ";
            }
            listtypes += BigSwitches.switchTypeForName(type);
            first = false;
        }
        this.text = "Plus the strength of any card of type " + listtypes + " in your hand";
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
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses", loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes", loc);
        sb.append(bonuses.getString("plusStrengthOfAnyCardOfType"));
        sb.append(" ");
        sb.append(rb.getString("any2" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator((ArrayList<Type>) types, ", ", locale, 2, false));
        sb.append(" ");
        sb.append(bonuses.getString("plusStrengthOfAnyCardOfType2"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int max_on_hand = 0;
        for(Card c: hand){
            if(types.contains(c.type) && c.strength>max_on_hand){
                max_on_hand = c.strength;
            }
        }
        return max_on_hand;

    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){

        double now = this.count(hand);
        double potentialTableDeck = 0.0;
       List<Card> potential = table.stream().filter(c -> types.contains(c.getType())).collect(Collectors.toList());
       int max = 0;
       for(Card c : potential){
           if(c.getStrength() > max){
               max = c.getStrength();
           }
       }
        double tableStrength = Math.max((1 - state.getNumberOfEnemies()*1/table.size()) * max,0) ;
        List<Card> potentialDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).collect(Collectors.toList());
        double deckStrength = 0.0;
        for(Card c : potentialDeck){
            deckStrength += c.getStrength() * deckSize/unknownCards * (1/deckSize);
        }
        potentialTableDeck = Math.max(tableStrength, deckStrength);
        return Math.max(now, potentialTableDeck);
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand) {
        return 0;
    }
}
