package bonuses;

import artificialintelligence.State;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Bonus that represents bonus which gives points if all cards in hands are odd/even.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusIfAllAreOdd extends Bonus  {
    /**
     * Points given for each completion of the conditions to get it.
     */
    public int how_much;

    /**
     * True if all the cards need to be odd. False if they need to be even.
     */
    public boolean odd;

    /**
     * Constructor for this bonus.
     * @param how_much Points if all cards in hand are odd/even.
     * @param odd Characteristics of the strength they have to possess to count for the bonus.
     */
    public PlusIfAllAreOdd(int how_much, boolean odd) {
        this.how_much = how_much;
        this.odd = odd;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+").append(how_much);
        if(!odd){
            sb.append(rb.getString("plusIfAllAreEven"));
        } else{
            sb.append(rb.getString("plusIfAllAreOdd"));
        }
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int count = 0;
        for(Card c: hand){
            if(odd){
                if((c.getStrength() % 2) != 0){
                    count++;
                }
            }
            else{
                if((c.getStrength() % 2) == 0){
                    count++;
                }
            }
        }
        if(count == hand.size()){
            return how_much;
        }
        else{
            return 0;
        }
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        if(state.getNumOdd() == 7){
            return how_much;
        } else{
            long oddsOnTable = table.stream().filter(Card::isOdd).count();
            double tableodd = Math.max((oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/table.size()) * (state.getNumOdd() + 1 )/7* how_much,0) ;

            // check the deck
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(Card::isOdd).count();
            double deck = Math.max((deckSize / unknownCards) * oddsOnDeck/deckSize  * (state.getNumOdd() + 1 )/7*  how_much,0) ;
            potential += Math.max(tableodd,deck);
        }
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }
}
