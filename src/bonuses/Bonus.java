package bonuses;

import server.BigSwitches;
import server.Type;
import server.Card;

import java.io.Serializable;
import java.util.ArrayList;

public class Bonus implements ScoringInterface, Serializable {
    public long serialVersionUID = 1;
    public int priority = 8;
    private String text;
    public String getText(){
        return this.text;
    }
    public int getPriority(){
        return this.priority;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        return 0;
    }
    String giveListOfCardsWithSeparator(ArrayList<Integer> cards, String separator) {
        StringBuilder listcards = new StringBuilder();
        boolean first = true;
        for (Integer c : cards) {
            if (!first) {
                listcards.append(separator);
            }
            listcards.append(BigSwitches.switchIdForName(c));
            first = false;
        }
        return listcards.toString();
    }

    public String giveListOfTypesWithSeparator(ArrayList<Type> types, String separator){
        StringBuilder listtypes = new StringBuilder();
        boolean first = true;
        for(Type type: types){
            if(!first){
                listtypes.append(separator);
            }
            listtypes.append(BigSwitches.switchTypeForName(type));
            first = false;
        }
        return listtypes.toString();
    }
}
