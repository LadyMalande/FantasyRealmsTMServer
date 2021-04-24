package bonuses;

import artificialintelligence.State;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusForEachOdd extends Bonus  {
    public long serialVersionUID = 8;
    public final String text;
    public int how_much;
    private boolean odd;
    private int thiscardid;

    public PlusForEachOdd(int how_much, boolean odd, int thiscardid) {
        this.odd = odd;
        this.thiscardid = thiscardid;
        this.how_much = how_much;
        if(odd) {
            this.text = "+" + how_much + " for each OTHER card in your hand with odd strength";
        } else{
            this.text = "+" + how_much + " for each OTHER card in your hand with even strength";
        }
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
        ResourceBundle rb = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+" + how_much);
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
                if(((c.strength % 2) != 0) && (c.id != thiscardid)){
                    sum += how_much;
                }
            }
            else{
                if(((c.strength % 2) == 0) && (c.id != thiscardid)){
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
            long oddsOnTable = table.stream().filter(c -> c.isOdd() == true).count();
            potential += Math.max((oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/table.size()) * how_much,0) ;

            // check the deck
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> c.isOdd() == true).count();
            potential += Math.max((deckSize / unknownCards) * oddsOnDeck/deckSize * how_much,0) ;
        }


        //

        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }
}
