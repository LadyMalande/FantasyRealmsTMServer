package bonuses;

import artificialintelligence.State;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Bonus that represents bonus which gives points for each odd/even card.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusForEachOdd extends Bonus  {
    /**
     * Points given for each completion of the conditions to get it.
     */
    public int how_much;
    /**
     * If true this bonus gives points for every odd card in hand. If false it gives points for every even card in hand.
     */
    private boolean odd;
    /**
     * This card won't award points.
     */
    private int thiscardid;

    /**
     * Constructor for this bonus.
     * @param how_much Points given for each even/odd card in hand.
     * @param odd True if odd cards are awarded. False if even cards are awarded.
     * @param thiscardid This card won't award points.
     */
    public PlusForEachOdd(int how_much, boolean odd, int thiscardid) {
        this.odd = odd;
        this.thiscardid = thiscardid;
        this.how_much = how_much;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+").append(how_much);
        if(!odd){
            sb.append(rb.getString("plusForEachEven"));
        } else{
            sb.append(rb.getString("plusForEachOdd"));
        }
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int sum = 0;
        for(Card c: hand){
            if(odd){
                if(((c.getStrength() % 2) != 0) && (c.getId() != thiscardid)){
                    sum += how_much;
                }
            }
            else{
                if(((c.getStrength() % 2) == 0) && (c.getId() != thiscardid)){
                    sum += how_much;
                }
            }
        }
        return sum;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        // This points are sure, we have them in our hand
        potential += state.getNumOdd() * how_much;
        // there is fitting card on the table, will it last there?
        if(state.getNumOdd() < 7){
            long oddsOnTable = table.stream().filter(Card::isOdd).count();
            potential += Math.max((oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/table.size()) * how_much,0) ;

            // check the deck
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(Card::isOdd).count();
            potential += Math.max((deckSize / unknownCards) * oddsOnDeck/deckSize * how_much,0) ;
        }

        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }
}
