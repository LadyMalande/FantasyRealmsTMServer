package bonuses;

import artificialintelligence.State;
import server.Card;
import server.Type;

import java.util.*;

/**
 * Bonus that represents bonus which gives increasing points for the cards of the same color.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusForSameColorCards extends Bonus  {

    /**
     * Constructor for the bonus.
     */
    public PlusForSameColorCards() {
    }

    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        ArrayList<Type> types = new ArrayList<>();
        HashMap<Type, Integer> table = new HashMap<>();
        ArrayList<String> alreadyInList = new ArrayList<>();
        for(Card c: hand){
            if(table.containsKey(c.getType())){
                if(!alreadyInList.isEmpty()){
                    if(!alreadyInList.contains(c.getName())){
                        table.put(c.getType(), table.get(c.getType()) + 1);
                        alreadyInList.add(c.getName());
                    }
                } else{
                    table.put(c.getType(), table.get(c.getType()) + 1);
                    alreadyInList.add(c.getName());
                }

            } else{
                table.put(c.getType(), 1);
                alreadyInList.add(c.getName());
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

    /**
     * Counts how much each type can give if added to hand.
     * @param hand Cards in hand.
     * @return Map of types and points awarded if one more card of that type is added to hand.
     */
    public Map<Type, Integer> howMuchCanTypeGive(ArrayList<Card> hand){
        Map<Type, Integer> typeBenefit = new HashMap<>();
        HashMap<Type, Integer> table = new HashMap<>();
        ArrayList<String> alreadyInList = new ArrayList<>();
        for(Card c: hand){
            if(table.containsKey(c.getType())){
                if(!alreadyInList.isEmpty()){
                    if(!alreadyInList.contains(c.getName())){
                        table.put(c.getType(), table.get(c.getType()) + 1);
                        alreadyInList.add(c.getName());
                    }
                } else{
                    table.put(c.getType(), table.get(c.getType()) + 1);
                    alreadyInList.add(c.getName());
                }

            } else{
                table.put(c.getType(), 1);
                alreadyInList.add(c.getName());
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
            if(table.containsKey(c.getType())){
                if(!alreadyInList.isEmpty()){
                    if(!alreadyInList.contains(c.getName())){
                        table.put(c.getType(), table.get(c.getType()) + 1);
                        alreadyInList.add(c.getName());
                    }
                } else{
                    table.put(c.getType(), table.get(c.getType()) + 1);
                    alreadyInList.add(c.getName());
                }

            } else{
                table.put(c.getType(), 1);
                alreadyInList.add(c.getName());
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
            potential += (oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/(float)table.size()) * getRewardForSame(state.getMostFromOneType() + 1);
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> state.getWhichTypeIsMost().contains(c.getType())).count();
            potential += (deckSize / (float)unknownCards) * oddsOnDeck/(float)deckSize * getRewardForSame(state.getMostFromOneType() + 1);
        }
        return potential;
    }

    /**
     * Gets the bonus points for given number of the same color cards.
     * @param value Number of same color cards.
     * @return Points to final score.
     */
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

    /**
     * Collects a map of the types in hand and their number.
     * @param hand Cards in hand.
     * @return Map of types in hand and their size.
     */
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

    /**
     * Collects a list of the most same colors.
     * @param typesNumber Map of types and their number in hand.
     * @return List of the types that are most numerous in hand.
     */
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

    /**
     * Tells if the types can yield a profit.
     * @param types Types that we want to know if they give some profit.
     * @param hand Cards in hand.
     * @return True if any of the types gives extra points.
     */
    public boolean reactsWithTypes(ArrayList<Type> types, ArrayList<Card> hand){
        Map<Type, Integer> typesNumber = getNumberOfTypes(hand);
        ArrayList<Type> mostUsedTypes = getMostUsedTypes(typesNumber);

        // We can extend mostTypes by one with this change
        return mostUsedTypes.stream().anyMatch(types::contains);
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
