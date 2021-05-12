package bonuses;

import artificialintelligence.State;
import server.Card;
import server.Type;

import java.util.*;

public class PlusForSameColorCards extends Bonus  {
    public long serialVersionUID = 14;
    public final String text;

    public PlusForSameColorCards() {
        this.text = "+10 for 3,\n +40 for 4,\n +100 for 5 cards of the same type";
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }
    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        ArrayList<Type> types = new ArrayList<>();
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
        // these types can gain from getting +1 type. 1 of type is too little, 5 is already full potential.
        for (Map.Entry<Type,Integer> entry : table.entrySet()) {
            if(entry.getValue() == 2 || entry.getValue() == 3 || entry.getValue() == 4){
                types.add(entry.getKey());
            }
        }
        return types;
    }
    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        // This class has a special method for obtaining bonuses out of colors
        return 0;
    }

    public Map<Type, Integer> howMuchCanTypeGive(ArrayList<Card> hand){
        Map<Type, Integer> typeBenefit = new HashMap<>();
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
        // these types can gain from getting +1 type. 1 of type is too little, 5 is already full potential.
        for (Map.Entry<Type,Integer> entry : table.entrySet()) {
            if(entry.getValue() == 2) {
                typeBenefit.put(entry.getKey(),10);
            } else if(entry.getValue() == 3){
                typeBenefit.put(entry.getKey(),30);
            } else if (entry.getValue() == 4){
                typeBenefit.put(entry.getKey(),60);
            }
        }
        return typeBenefit;
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

    private Map<Type, Integer> getNumberOfTypes(ArrayList<Card> hand){
        Map<Type, Integer> typesNumber = new HashMap<>();
        for(Card c : hand){
            if(typesNumber.get(c.getType()) != null){
                int newValue = typesNumber.get(c.getType()) + 1;
                typesNumber.put(c.getType(),newValue);
            } else{
                typesNumber.put(c.getType(),1);
            }
        }
        return typesNumber;
    }

    private ArrayList<Type> getMostUsedTypes(Map<Type, Integer> typesNumber){
        ArrayList<Type> mostUsedTypes = new ArrayList<>();
        int max = 1;
        for(Map.Entry<Type,Integer> entry : typesNumber.entrySet()){
            if(entry.getValue() > max){
                max = entry.getValue();
            }
        }
        for(Map.Entry<Type,Integer> entry : typesNumber.entrySet()){
            if(entry.getValue() == max){
                mostUsedTypes.add(entry.getKey());
            }
        }
        return mostUsedTypes;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }

    public boolean reactsWithTypes(ArrayList<Type> types, ArrayList<Card> hand){
        Map<Type, Integer> typesNumber = getNumberOfTypes(hand);
        ArrayList<Type> mostUsedTypes = getMostUsedTypes(typesNumber);

        // We can extend mostTypes by one with this change
        if(mostUsedTypes.stream().anyMatch(type -> types.contains(type))){
            return true;
        } else{
            return false;
        }
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        Map<Type, Integer> typesNumber = getNumberOfTypes(hand);
        ArrayList<Type> mostUsedTypes = getMostUsedTypes(typesNumber);
        if(mostUsedTypes.contains(t)){
            return getRewardForSame(typesNumber.get(t) + 1) - getRewardForSame(typesNumber.get(t));
        }
        return 0;
    }


}
