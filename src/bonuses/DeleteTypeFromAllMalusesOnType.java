package bonuses;

import maluses.Malus;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;

public class DeleteTypeFromAllMalusesOnType extends Bonus  {
    public int priority = 5;
    private Type deleteThisTypeFromMaluses;
    private Type onWhichType;
    public String text;

    public DeleteTypeFromAllMalusesOnType(Type whichType, Type onWhichType){
        this.deleteThisTypeFromMaluses = whichType;
        this.onWhichType = onWhichType;
        text = "Remove word " + BigSwitches.switchTypeForName(whichType) + " on cards of type "+BigSwitches.switchTypeForName(onWhichType);
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
            if(c.type.equals(onWhichType)) {
                if (c.maluses!=null && !c.maluses.isEmpty()) {
                    for (Malus m : c.maluses) {
                        if(m.types.contains(deleteThisTypeFromMaluses)) {
                            m.types.remove(deleteThisTypeFromMaluses);
                        }
                    }
                }
            }

        }
        return 0;
    }
}
