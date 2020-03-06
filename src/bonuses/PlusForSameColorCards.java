package bonuses;

import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.HashMap;

public class PlusForSameColorCards extends Bonus  {
    public long serialVersionUID = 14;
    public final String text;

    public PlusForSameColorCards() {
        this.text = "+10 for 3,\n +40 for 4,\n +100 for 5 cards of the same type";
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int total = 0;
        HashMap<Type, Integer> table = new HashMap<>();
        ArrayList<String> alreadyInList = new ArrayList<>();
        for(Card c: hand){
            if(table.containsKey(c.type)){
                if(!alreadyInList.isEmpty()){
                    if(!alreadyInList.contains(c.name)){
                        table.put(c.type, table.get(c.type) + 1);
                        alreadyInList.add(c.name);
                    }
                } else{
                    table.put(c.type, table.get(c.type) + 1);
                    alreadyInList.add(c.name);
                }

            } else{
                table.put(c.type, 1);
                alreadyInList.add(c.name);
            }
        }
        for (Integer value : table.values()) {
            if(value == 3){
                total += 10;
            } else if(value == 4){
                total += 40;
            } else if(value == 5){
                total += 100;
            }
        }
        return total;
    }
}
