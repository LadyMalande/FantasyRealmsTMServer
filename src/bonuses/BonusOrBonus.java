package bonuses;

import server.Card;

import java.util.ArrayList;

public class BonusOrBonus extends Bonus {
    public long serialVersionUID = 2;
    private String text;
    private Bonus b1;
    private Bonus b2;

    public BonusOrBonus(Bonus b1, Bonus b2) {
        this.text = b1.getText() + "\n----- OR -----\n" + b2.getText();
        this.b1 = b1;
        this.b2 = b2;
        System.out.println("Card INIT: Text: " + getText());
    }



    @Override
    public int count(ArrayList<Card> hand) {
        return Math.max(b1.count(hand), b2.count(hand));
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public Bonus clone() throws CloneNotSupportedException{
        BonusOrBonus newb = (BonusOrBonus)super.clone();
        newb.text = this.text;
        newb.priority = this.priority;
        newb.serialVersionUID = this.serialVersionUID;
        newb.b1 = this.b1.clone();
        newb.b2 = this.b1.clone();
        return newb;
    }
}
