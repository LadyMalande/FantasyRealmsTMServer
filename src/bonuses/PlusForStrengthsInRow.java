package bonuses;

import artificialintelligence.State;
import server.Card;

import java.util.*;

public class PlusForStrengthsInRow extends Bonus  {
    public long serialVersionUID = 15;
    public final String text;

    public PlusForStrengthsInRow() {
        this.text = "+10 for 3 SIR, +30 for 4 SIR, +60 for 5 SIR, +100 for 6 SIR or +150 for 7 SIR\n*SIR = strengths in a row";
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
        sb.append(rb.getString("plusForStrengthsInRow"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        hand.sort(Comparator.comparingInt((Card c) -> c.strength));
        int howMuchInARow = 1;
        int laststrength = -100;
        ArrayList<Integer> allrows = new ArrayList<>();
        for(Card c: hand ){
            if(laststrength + 1 == c.strength){
                howMuchInARow++;
            }
            else if(laststrength == c.strength){
                // nothing happens
            }
            else{
                allrows.add(howMuchInARow);
                howMuchInARow = 1;
            }
            laststrength = c.strength;
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
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        // TODO
        return potential;
    }
}
