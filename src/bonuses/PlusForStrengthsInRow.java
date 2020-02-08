package bonuses;

import server.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlusForStrengthsInRow extends Bonus  {
    public long serialVersionUID = 15;
    public final String text;

    public PlusForStrengthsInRow() {
        this.text = "+10 for 3 strengths, +30 for 4 strengths, +60 for 5 strengths, +100 for 6 strengths or +150 for 7 strenghts in a row";
    }

    @Override
    public String getText(){
        return this.text;
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
}
