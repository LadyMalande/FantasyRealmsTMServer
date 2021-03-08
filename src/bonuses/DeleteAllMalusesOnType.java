package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class DeleteAllMalusesOnType extends Bonus  {
    public long serialVersionUID = 4;
    public int priority = 5;
    public final String text;
    private Type deleteMalusesOnThisType;

    public DeleteAllMalusesOnType(Type t){
        this.deleteMalusesOnThisType = t;
        this.text = "Remove all maluses from type " + BigSwitches.switchTypeForName(deleteMalusesOnThisType);
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }
    @Override
    public int getPriority(){ return this.priority; }
    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(c.type.equals(deleteMalusesOnThisType)){
                if(c.maluses != null){
                    c.maluses.clear();
                    System.out.println("=========Mazu POSTIH NA karte " + c.name + " !!!//////////////---------" + this.getText());
                }

            }

        }
        return 0;
    }
}
