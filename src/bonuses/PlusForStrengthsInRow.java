package bonuses;

import artificialintelligence.State;
import server.Card;
import server.Type;
import util.HandCloner;

import java.util.*;

/**
 * Bonus that represents bonus which gives increasing points for cards with strengths in a row.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusForStrengthsInRow extends Bonus  {

    /**
     * Constructor for the bonus.
     */
    public PlusForStrengthsInRow() {
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append(rb.getString("plusForStrengthsInRow"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        try {
            ArrayList<Card> toIterate = HandCloner.cloneHand(null, hand);

            toIterate.sort(Comparator.comparingInt((Card c) -> c.getStrength()));
            int howMuchInARow = 1;
            int laststrength = -100;
            ArrayList<Integer> allrows = new ArrayList<>();
            for(Card c: toIterate ){
                if(laststrength + 1 == c.getStrength()){
                    howMuchInARow++;
                } else{
                    allrows.add(howMuchInARow);
                    howMuchInARow = 1;
                }
                laststrength = c.getStrength();
            }
            allrows.add(howMuchInARow);
            switch (Collections.max(allrows)){
                case 3:return 10;
                case 4: return 30;
                case 5: return 60;
                case 6: return 100;
                case 7: return 150;
                default: return 0;
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }
}
