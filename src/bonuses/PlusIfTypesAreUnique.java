package bonuses;

import artificialintelligence.State;
import server.Card;
import server.Type;

import java.util.*;
import java.util.stream.Collectors;

public class PlusIfTypesAreUnique extends Bonus  {
    public long serialVersionUID = 17;
    public final String text;
    public int howMuch;

    public PlusIfTypesAreUnique(int howMuch) {
        this.text = "+" + howMuch +" if every non-BLANKED card is a different type ";
        this.howMuch = howMuch;
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }
    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        // Makes map of colors in hand. If there is more than one color with count 2, Change color cant help
        ArrayList<Type> types = new ArrayList<>();
        int typeDuplicates = 0;
        Map<Type, Integer> typeCounts = new HashMap<>();
        for(Card c : hand){
            if(typeCounts.keySet().contains(c.getType())){
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
                    new ArrayList<Type>(Arrays.asList(Type.ARMY, Type.ARTIFACT, Type.BEAST, Type.FLAME, Type.FLOOD,
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

    public Card whichCardNeedsChange(ArrayList<Card> hand){

        // Makes map of colors in hand. If there is more than one color with count 2, Change color cant help
        ArrayList<Type> types = new ArrayList<>();
        Type duplicate = Type.FLOOD;
        int typeDuplicates = 0;
        Map<Type, Integer> typeCounts = new HashMap<>();
        for(Card c : hand){
            if(typeCounts.keySet().contains(c.getType())){
                int newCount = typeCounts.get(c.getType()) + 1;
                typeCounts.put(c.getType(), newCount);
                typeDuplicates++;
                duplicate = c.getType();
            } else{
                typeCounts.put(c.getType(), 1);
            }
        }
        if(typeDuplicates > 1){
            return null;
        } else{
            if(typeDuplicates == 1){
                Type finalDuplicate = duplicate;
                List<Card> fitsTheDescription = hand.stream().filter(card -> card.getId() != 31 && card.getType() == finalDuplicate).collect(Collectors.toList());
                return fitsTheDescription.get(0);
            }
            return null;
        }

    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+" + howMuch);
        sb.append(bonuses.getString("plusIfTypesAreUnique"));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        if(hand.size() == 2){
            return 0;
        }
        ArrayList<Type> types = new ArrayList<Type>();
        for(Card c: hand){
            if(types.contains(c.type)){
                return 0;
            } else{
                types.add(c.type);
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
            if(haveTypesInHand.containsAll(types)){
                return false;
            } else{
                return true;
            }
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
