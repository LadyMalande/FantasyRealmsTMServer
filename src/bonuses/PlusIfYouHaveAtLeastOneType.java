package bonuses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PlusIfYouHaveAtLeastOneType extends Bonus  {
    public long serialVersionUID = 22;
    public final String text;
    private int howMuch;
    public ArrayList<Type> types;

    public PlusIfYouHaveAtLeastOneType(int hm, ArrayList<Type> types){

        this.howMuch = hm;
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
        this.text = "+" + howMuch + " if you have any type " + listtypes;
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }

    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        if(hand.stream().anyMatch(card -> types.contains(card.getType()))){
            return null;
        } else{
            return types;
        }
    }
    @Override
    public Card satisfiesCondition(ArrayList<Card> hand)
    {
        List<Card> cards = hand.stream().filter(card -> types.contains(card.getType())).collect(Collectors.toList());
        //Says ids of cards that cant be recolored if the size of this array is only 1
        if(cards.size() == 1){
            if(count(hand) != 0){
                return cards.get(0);
            }
            return null;
        } else{
            return null;
        }
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
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append("+" + howMuch);
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
                return howMuch;
            }
        }
        return 0;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potentialTable, potentialDeck;

        if(hand.stream().filter(c -> types.contains(c.getType())).count() >= 1){
            return howMuch;
        }
        long suitableOnTable = table.stream().filter(c -> types.contains(c.getType())).count();
        potentialTable = Math.min(suitableOnTable - state.getNumberOfEnemies() * suitableOnTable / table.size() * howMuch, howMuch);
        long suitableInDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).count();
        potentialDeck = Math.min((deckSize / unknownCards) * suitableInDeck / deckSize * howMuch, howMuch);
        return Math.max(potentialTable, potentialDeck);
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return types.stream().anyMatch(type -> types.contains(type));
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        // Nothing to do better
        if(hand.stream().anyMatch(card -> types.contains(card.getType()))){
            return 0;
        }
        else{
            if(types.contains(t)){
                return howMuch;
            }
        }
        return 0;
    }
}
