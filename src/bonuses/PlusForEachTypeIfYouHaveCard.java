package bonuses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusForEachTypeIfYouHaveCard extends Bonus  {
    public long serialVersionUID = 12;
    public final String text;
    public int howMuch;
    public ArrayList<Type> types;
    public int cardid;

    public PlusForEachTypeIfYouHaveCard(int howMuch, ArrayList<Type> types, int cardid) {
        String cardname = "";
        cardname = BigSwitches.switchIdForName(cardid);

        String listtypes = "";
        boolean first = true;
        for(Type type: types){
            if(!first){
                listtypes +=", ";
            }
            listtypes += BigSwitches.switchTypeForName(type);
            first = false;
        }

        this.text = "+" + howMuch + " for each " + listtypes + " if with " + cardname;
        this.howMuch = howMuch;
        this.types = types;
        this.cardid = cardid;
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
        ResourceBundle rbcards = ResourceBundle.getBundle("server.CardNames",loc);
        sb.append("+" + howMuch);
        sb.append(" ");
        sb.append(rb.getString("for"));
        sb.append(" ");
        sb.append(rb.getString("each4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types,", ",locale, 4,false));
            sb.append(rb.getString("ifYouHave"));
        sb.append(" ");
            sb.append(rbcards.getString(BigSwitches.switchIdForSimplifiedName(cardid) + "4"));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        boolean countit = false;
        for(Card c: hand){
            if(c.name.equals(BigSwitches.switchIdForName(cardid))){
                countit = true;
            }
                if (types.contains(c.type)) {
                    total += howMuch;
                }

        }
        if(countit){
            return total;
        } else{
            return 0;
        }
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        long numberOfSuitableCards = hand.stream().filter(c -> types.contains(c.getType())).count();
        if(hand.stream().filter(c -> c.name.equals(BigSwitches.switchIdForName(cardid))).count() >= 1){
            potential += (numberOfSuitableCards)*howMuch;
        }
        if(hand.stream().filter(c -> c.name.equals(BigSwitches.switchIdForName(cardid))).count() >= 1) {
            if (numberOfSuitableCards < 5) {
                long oddsOnTable = table.stream().filter(c -> types.contains(c.getType())).count();
                potential += (oddsOnTable - state.getNumberOfEnemies() * oddsOnTable / table.size()) * howMuch;
                long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).count();
                potential += (deckSize / unknownCards) * oddsOnDeck / deckSize * howMuch;
            }
        }
        if(hand.stream().filter(c -> c.name.equals(BigSwitches.switchIdForName(cardid))).count() < 1) {
            long tableCard = table.stream().filter(c -> c.name.equals(BigSwitches.switchIdForName(cardid))).count();
            potential += (tableCard - state.getNumberOfEnemies() * tableCard / table.size()) * howMuch * numberOfSuitableCards;
            long deck = state.getProbablyInDeck().stream().filter(c -> c.name.equals(BigSwitches.switchIdForName(cardid))).count();
            potential += (deck - state.getNumberOfEnemies() * deck / table.size()) * howMuch * numberOfSuitableCards;
        }
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((cardid)))) ||
                types.stream().anyMatch(type -> types.contains(type));

    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(types.contains(t) && hand.stream().anyMatch(card -> card.name.equals(BigSwitches.switchIdForName(cardid)))){
            return howMuch;
        }
        if(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((cardid))) == t){
            if(!hand.stream().anyMatch(card -> card.name.equals(BigSwitches.switchIdForName(cardid)))){
                int numberOfmatchingTypes = 0;
                for(Card c : hand){
                    if(types.contains(c.getType())){
                        numberOfmatchingTypes++;
                    }
                }
                return numberOfmatchingTypes * howMuch;
            }
        }
        return 0;
    }
}
