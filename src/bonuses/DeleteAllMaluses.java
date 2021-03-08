package bonuses;

import server.Card;

import java.io.Serializable;
import java.util.ArrayList;

public class DeleteAllMaluses extends Bonus implements Serializable {
    public long serialVersionUID = 2;
    public int priority = 5;
    public final String text = "Remove all maluses from all cards";

    @Override
    public String getText(){
        return this.text;
    }
    @Override
    public int getPriority(){ return this.priority; }

    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(c.maluses != null) {
                c.maluses = null;
                System.out.println("----------- Mazu vsechny postihy z karet, nyni z karty " + c.name);
            }
        }
        return 0;
    }
}
