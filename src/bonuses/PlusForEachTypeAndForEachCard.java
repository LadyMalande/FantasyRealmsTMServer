package bonuses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusForEachTypeAndForEachCard extends Bonus  {
    public long serialVersionUID = 11;
    public final String text;
    private int howMuch;
    public ArrayList<Type> types;
    public ArrayList<Integer> cards;

    public PlusForEachTypeAndForEachCard(int hm, ArrayList<Type> types, ArrayList<Integer> cards){
        String listcards = "";
        boolean first = true;
        for(Integer c: cards){
            if(!first){
                listcards +=", ";
            }
            listcards += BigSwitches.switchIdForName(c);
            first = false;
        }

        String listtypes = "";
        first = true;
        for(Type type: types){
            if(!first){
                listtypes +=", ";
            }
            listtypes += BigSwitches.switchTypeForName(type);
            first = false;
        }
        this. text = "+" + hm + " for each " + listtypes + " and each " + listcards;
        this.howMuch = hm;
        this.types = types;
        this.cards = cards;
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }
    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {

        return types;
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
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types,", ",locale, 4,false));
        if(cards != null){
            sb.append(", ");
            sb.append(giveListOfCardsWithSeparator(cards,", ",locale, 4,false));
        }
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        ArrayList<String> names = new ArrayList<>();
        for(Integer i: cards){
            names.add(BigSwitches.switchIdForName(i));
        }
        for(Card c: hand){
                if (types.contains(c.type)) {
                    total += howMuch;
                }
                if (names.contains(c.name)) {
                    total += howMuch;
                }
        }

        return total;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        long numberOfSuitableCards = hand.stream().filter(c -> types.contains(c.getType())).count() + hand.stream().filter(c -> cards.contains(c.getId())).count();
        potential += (numberOfSuitableCards)* howMuch;
        if(numberOfSuitableCards <6){
            long oddsOnTable = table.stream().filter(c -> types.contains(c.getType())).count() + table.stream().filter(c -> cards.contains(c.getId())).count();
            potential += (oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/table.size()) * howMuch;
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).count() + state.getProbablyInDeck().stream().filter(c -> cards.contains(c.getId())).count();
            potential += (deckSize / unknownCards) * oddsOnDeck/deckSize * howMuch;
        }
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return this.types.stream().anyMatch(type -> types.contains(type)) ||
                cards.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id)))));

    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(types.contains(t)){
            return howMuch;
        }
        if(cards.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id)))))){
            return howMuch;
        }
        return 0;
    }
}
