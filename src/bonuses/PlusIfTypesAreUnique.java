package bonuses;

import artificialintelligence.State;
import server.Card;
import server.Type;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Bonus that represents bonus which gives points if all the types of cards in hands are unique.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusIfTypesAreUnique extends Bonus  {
    /**
     * Points given for each completion of the conditions to get it.
     */
    public int howMuch;

    /**
     * Constructor for the bonus,
     * @param howMuch Points that will be awarded if there is no duplicated type in the hand.
     */
    public PlusIfTypesAreUnique(int howMuch) {
        this.howMuch = howMuch;
    }
    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        // Makes map of colors in hand. If there is more than one color with count 2, Change color cant help
        int typeDuplicates = 0;
        Map<Type, Integer> typeCounts = new HashMap<>();
        for(Card c : hand){
            if(typeCounts.containsKey(c.getType())){
                int newCount = typeCounts.get(c.getType()) + 1;
                typeCounts.put(c.getType(), newCount);
                typeDuplicates++;
            } else{
                typeCounts.put(c.getType(), 1);
            }
        }
        if(typeDuplicates > 1){
            return null;
        } else{
            ArrayList<Type> colors =
                    new ArrayList<>(Arrays.asList(Type.ARMY, Type.ARTIFACT, Type.BEAST, Type.FLAME, Type.FLOOD,
                            Type.LAND, Type.LEADER, Type.WEAPON, Type.WEATHER, Type.WIZARD));
            for(Map.Entry<Type, Integer> entry : typeCounts.entrySet()){
                colors.remove(entry.getKey());
            }
            return colors;
        }
    }
    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        return howMuch;
    }

    /**
     * returns card that needs to be changed if it helps to get the bonus done.
     * @param hand The hand for inspection.
     * @return The card that must change its type in order to get the bonus. If there is more than one card that needs to be
     * changed, null is returned.
     */
    public Card whichCardNeedsChange(ArrayList<Card> hand){

        // Makes map of colors in hand. If there is more than one color with count 2, Change color cant help
        Type duplicate = Type.FLOOD;
        int typeDuplicates = 0;
        Map<Type, Integer> typeCounts = new HashMap<>();
        for(Card c : hand){
            if(typeCounts.containsKey(c.getType())){
                int newCount = typeCounts.get(c.getType()) + 1;
                typeCounts.put(c.getType(), newCount);
                typeDuplicates++;
                duplicate = c.getType();
            } else{
                typeCounts.put(c.getType(), 1);
            }
        }
        if (typeDuplicates <= 1) {
            if (typeDuplicates == 1) {
                Type finalDuplicate = duplicate;
                List<Card> fitsTheDescription = hand.stream().filter(card -> card.getId() != 31 && card.getType() == finalDuplicate).collect(Collectors.toList());
                return fitsTheDescription.get(0);
            }
        }
        return null;

    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+").append(howMuch);
        sb.append(bonuses.getString("plusIfTypesAreUnique"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        if(hand.size() == 2){
            return 0;
        }
        ArrayList<Type> types = new ArrayList<>();
        for(Card c: hand){
            if(types.contains(c.getType())){
                return 0;
            } else{
                types.add(c.getType());
            }
        }

        return howMuch;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        if(state.getNumTypes() == 7){
            return howMuch;
        } else{
            long oddsOnTable = table.stream().filter(c -> !state.getHaveTheseTypes().contains(c.getType())).count();
            double tableunique = Math.max((oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/table.size()) * (state.getNumTypes() + 1 )/7* howMuch,0) ;

            // check the deck
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> !state.getHaveTheseTypes().contains(c.getType())).count();
            double deck = Math.max((deckSize / unknownCards) * oddsOnDeck/deckSize  * (state.getNumTypes() + 1 )/7* howMuch,0) ;
            potential += Math.max(tableunique,deck);
        }
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }

    /**
     * Tells if the bonus reacts somehow with given types. If the bonus can be made better by changing some cards into those types.
     * @param types Types we want to change cards to.
     * @param hand Cards in hand.
     * @return True if there is some card that is not unique. False if all cards are of unique color.
     */
    public boolean reactsWithTypes(ArrayList<Type> types, ArrayList<Card> hand){
        ArrayList<Type> haveTypesInHand = new ArrayList<>();
        for(Card c : hand){
            if(!haveTypesInHand.contains(c.getType())){
                haveTypesInHand.add(c.getType());
            }
        }
        // We have different cards, no need to use Shapeshifter/Mirage
        if(haveTypesInHand.size() == hand.size()){
            return false;
        } else{
            // There are already all changeable colors, cant react with this card
            return !haveTypesInHand.containsAll(types);
        }
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        ArrayList<Type> haveTypesInHand = new ArrayList<>();
        for(Card c : hand){
            if(!haveTypesInHand.contains(c.getType())){
                haveTypesInHand.add(c.getType());
            }
        }
        // We have different cards, no need to use Shapeshifter/Mirage
        if(hand.size() - haveTypesInHand.size()  == 1){
            if(!haveTypesInHand.contains(t)){
                return howMuch;
            }
        }
        return 0;
    }
}
