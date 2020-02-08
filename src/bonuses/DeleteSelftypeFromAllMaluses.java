package bonuses;

import maluses.Malus;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class DeleteSelftypeFromAllMaluses extends Bonus  {
    public long serialVersionUID = 5;
    public int priority = 5;
    private Type deleteThisTypeFromMaluses;
    public final String text;

    public DeleteSelftypeFromAllMaluses(Type t){

        this.deleteThisTypeFromMaluses = t;
        this.text = "Remove word " + BigSwitches.switchTypeForName(deleteThisTypeFromMaluses) + " from all maluses";
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
            if(c.maluses != null && !c.maluses.isEmpty()){
                for(Malus m: c.maluses){
                    if(m.types.contains(deleteThisTypeFromMaluses)) {
                        m.types.remove(deleteThisTypeFromMaluses);
                    }
                }
            }

        }
        return 0;
    }
}
