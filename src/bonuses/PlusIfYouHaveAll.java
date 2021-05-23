package bonuses;

import util.BigSwitches;
import server.Card;
import server.Type;
import util.TextCreators;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Bonus that represents bonus which gives one time points if all the cards are in the hand.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusIfYouHaveAll extends Bonus{
    /**
     * Points given for each completion of the conditions to get it.
     */
    private int how_much;
    /**
     * All of those cards are needed.
     */
    public ArrayList<Integer> idsOfCardsNeeded;

    /**
     * Constructor of the bonus.
     * @param hm How much will the player get when he has all the needed cards in hand.
     * @param cards All cards needed for getting the bonus.
     */
    public PlusIfYouHaveAll(int hm, ArrayList<Integer> cards){
        this.how_much = hm;
        this.idsOfCardsNeeded = cards;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rbbonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+").append(how_much);
        sb.append(rbbonuses.getString("ifYouHave"));
        sb.append(" ");
            sb.append(TextCreators.giveListOfCardsWithSeparator(idsOfCardsNeeded, "and",locale,4));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int completed = 0;
        for(int id: idsOfCardsNeeded){
            for(Card card: hand){
                if(BigSwitches.switchIdForName(id).equals(card.getName())){
                    completed++;
                }
            }
        }
        // if I have more princesses I still should get points for Unicorn
        if(idsOfCardsNeeded.size() <= completed){
            return how_much;
        } else {
            return 0;
        }
    }
    /*
    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        int completed = 0;
        for(int id: idsOfCardsNeeded){
            for(Card card: hand){
                if(BigSwitches.switchIdForName(id).equals(card.name)){
                    completed++;
                }
            }
        }
        // if I have more princesses I still should get points for Unicorn
        if(idsOfCardsNeeded.size() <= completed){
            return how_much;
        } else if(idsOfCardsNeeded.size() - completed < 2){
            long oddsOnTable = table.stream().filter(c -> types.contains(c.getType())).count() + table.stream().filter(c -> selftype == c.getType()).count();
            potential += (oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/table.size()) * howMuch;
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).count() + state.getProbablyInDeck().stream().filter(c -> selftype == c.getType()).count();
            potential += (deckSize / unknownCards) * oddsOnDeck/deckSize * howMuch;
        }
        return potential;
    }
*/
    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return idsOfCardsNeeded.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id)))));
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        int completed = 0;
        ArrayList<Integer> notcompleted = new ArrayList<>(idsOfCardsNeeded);
        for(int id: idsOfCardsNeeded){
            for(Card card: hand){
                if(BigSwitches.switchIdForName(id).equals(card.getName())){
                    completed++;
                    notcompleted.remove(card.getId());
                }
            }
        }
        // if I have more princesses I still should get points for Unicorn
        if(idsOfCardsNeeded.size() <= completed){
            return 0;
        }
        if(idsOfCardsNeeded.size() - completed == 1 && BigSwitches.switchNameForType(BigSwitches.switchIdForName(notcompleted.get(0))) == t){
            return how_much;
        }
        return 0;
    }
}
