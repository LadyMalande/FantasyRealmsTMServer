package bonuses;

import artificialintelligence.State;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusForSameColorCards extends Bonus  {
    public long serialVersionUID = 14;
    public final String text;

    public PlusForSameColorCards() {
        this.text = "+10 for 3,\n +40 for 4,\n +100 for 5 cards of the same type";
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
        sb.append(rb.getString("plusForSameColorCards"));
        return sb.toString();
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

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        potential += getRewardForSame(state.getMostFromOneType());
        if(potential < 100){
            long oddsOnTable = table.stream().filter(c -> state.getWhichTypeIsMost().contains(c.getType())).count();
            potential += (oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/table.size()) * getRewardForSame(state.getMostFromOneType() + 1);
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> state.getWhichTypeIsMost().contains(c.getType())).count();
            potential += (deckSize / unknownCards) * oddsOnDeck/deckSize * getRewardForSame(state.getMostFromOneType() + 1);
        }
        return potential;
    }

    private int getRewardForSame(int value){
        if(value == 3){
            return 10;
        } else if(value == 4){
            return 40;
        } else if(value == 5){
            return 100;
        }
        return 0;
    }
}
