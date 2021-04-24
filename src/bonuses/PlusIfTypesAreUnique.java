package bonuses;

import artificialintelligence.State;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusIfTypesAreUnique extends Bonus  {
    public long serialVersionUID = 17;
    public final String text;
    public int howMuch;

    public PlusIfTypesAreUnique(int howMuch) {
        this.text = "+" + howMuch +" if every non-BLANKED card is a different type ";
        this.howMuch = howMuch;
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
        sb.append("+" + howMuch);
        sb.append(bonuses.getString("plusIfTypesAreUnique"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        if(hand.size() == 2){
            return 0;
        }
        ArrayList<Type> types = new ArrayList<Type>();
        for(Card c: hand){
            if(types.contains(c.type)){
                return 0;
            } else{
                types.add(c.type);
            }
        }

        return howMuch;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        if(state.getNumTypes() == 7){
            return howMuch;
        } else{
            long oddsOnTable = table.stream().filter(c -> !state.getHaveTheseTypes().contains(c.getType())).count();
            double tableunique = Math.max((oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/table.size()) * (state.getNumTypes() + 1 )/7* howMuch,0) ;

            // check the deck
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> !state.getHaveTheseTypes().contains(c.getType())).count();
            double deck = Math.max((deckSize / unknownCards) * oddsOnDeck/deckSize  * (state.getNumTypes() + 1 )/7* howMuch,0) ;
            potential += Math.max(tableunique,deck);
        }
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }

    public boolean reactsWithTypes(ArrayList<Type> types, ArrayList<Card> hand){
        ArrayList<Type> haveTypesInHand = new ArrayList<>();
        for(Card c : hand){
            if(!haveTypesInHand.contains(c.getType())){
                haveTypesInHand.add(c.getType());
            }
        }
        // We have different cards, no need to use Shapeshifter/Mirage
        if(haveTypesInHand.size() == hand.size()){
            return false;
        } else{
            // There are already all changeable colors, cant react with this card
            if(haveTypesInHand.containsAll(types)){
                return false;
            } else{
                return true;
            }
        }
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        ArrayList<Type> haveTypesInHand = new ArrayList<>();
        for(Card c : hand){
            if(!haveTypesInHand.contains(c.getType())){
                haveTypesInHand.add(c.getType());
            }
        }
        // We have different cards, no need to use Shapeshifter/Mirage
        if(hand.size() - haveTypesInHand.size()  == 1){
            if(!haveTypesInHand.contains(t)){
                return howMuch;
            }
        }
        return 0;
    }
}
