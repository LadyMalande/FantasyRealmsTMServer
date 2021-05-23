package bonuses;

import artificialintelligence.State;
import util.BigSwitches;
import server.Card;
import server.Type;
import util.TextCreators;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Bonus that represents bonus which gives the points if all cards are in hand and at least one type.
 * Counted last as it has default priority = 8.
 * @author Tereza Miklóšová
 */
public class PlusIfYouHaveAllCardsAndAtLeastOneType extends Bonus  {
    /**
     * Points given for each completion of the conditions to get it.
     */
    private int howMuch;
    /**
     * All of these cards of these ids are needed to complete the bonus.
     */
    public ArrayList<Integer> idCardsNeeded;
    /**
     * At least one card of this type is needed to complete the one time bonus.
     */
    public ArrayList<Type> types;

    /**
     * Constructor of the bonus.
     * @param howMuch * Points given for each completion of the conditions to get it.
     * @param idCardsNeeded All of these cards of these ids are needed to complete the bonus.
     * @param types At least one card of this type is needed to complete the one time bonus.
     */
    public PlusIfYouHaveAllCardsAndAtLeastOneType( int howMuch, ArrayList<Integer> idCardsNeeded, ArrayList<Type> types) {
        this.howMuch = howMuch;
        this.idCardsNeeded = idCardsNeeded;
        this.types = types;
    }

    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        if(hand.stream().anyMatch(card -> types.contains(card.getType()))){
            return null;
        } else{
            ArrayList<String> names = new ArrayList<>();
            for(Integer i: idCardsNeeded){
                names.add(BigSwitches.switchIdForName(i));
            }
            int hascards = 0;
            for(Card c: hand){
                if(names.contains(c.getName())){
                    hascards++;
                }
                if(hascards == idCardsNeeded.size()){
                    return types;
                }
            }
        }
        return null;
    }
    @Override
    public Card satisfiesCondition(ArrayList<Card> hand)
    {
        List<Card> cards = hand.stream().filter(card -> types.contains(card.getType())).collect(Collectors.toList());
        //Says ids of cards that cant be recolored if the size of this array is only 1
        if(cards.size() == 1){
            if(count(hand) != 0){
                return cards.get(0);
            }
        }
        return null;
    }

    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        return howMuch;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        ResourceBundle rbbonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+").append(howMuch);
        sb.append(rbbonuses.getString("ifYouHave"));
        sb.append(" ");
        sb.append(TextCreators.giveListOfCardsWithSeparator(idCardsNeeded, "and",locale,4));
        sb.append(" ");
        sb.append(rb.getString("and"));
        sb.append(" ");
        sb.append(rb.getString("atLeast"));
        sb.append(" ");
        sb.append(rb.getString("one4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(TextCreators.giveListOfTypesWithSeparator(types, "or",locale, 4));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        ArrayList<String> names = new ArrayList<>();
        for(Integer i: idCardsNeeded){
            names.add(BigSwitches.switchIdForName(i));
        }
        int hascards = 0;
        boolean hasoneofthese = false;
        for(Card c: hand){
            if(names.contains(c.getName())){
                hascards++;
            }
            else if(types.contains(c.getType())){
                hasoneofthese = true;
            }
            if(hascards == idCardsNeeded.size() && hasoneofthese){
                return howMuch;
            }
        }
        return 0;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return idCardsNeeded.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id))))) ||
                types.stream().anyMatch(types::contains) ;

    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(hand.stream().anyMatch(card -> types.contains(card.getType()))){
            // The type need is covered, check the cards
            ArrayList<Integer> howManyCardsAreMissing = howManyCardsAreMissing(hand);
            if(howManyCardsAreMissing.size() == 0){
                return 0;
            } else if(howManyCardsAreMissing.size() == 1){
                if(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((howManyCardsAreMissing.get(0)))) == t){
                    return howMuch;
                }
            } else if(howManyCardsAreMissing.size() == 2){
                if(hand.stream().filter(card -> card.getId() == 52 || card.getId() == 53).count() == 2){
                    if((howManyCardsAreMissing.get(0) == 42 || howManyCardsAreMissing.get(0) == 31) && (howManyCardsAreMissing.get(1) == 42 || howManyCardsAreMissing.get(1) == 31))
                    return howMuch;
                }

            }
        } else{
            // The type is not covered, check if the cards are there, then ask if the type can be supplemented
            // The type need is covered, check the cards
            ArrayList<Integer> howManyCardsAreMissing = howManyCardsAreMissing(hand);
            if(howManyCardsAreMissing.size() == 0){
                if(types.contains(t)){
                    return howMuch;
                }
            } else if(howManyCardsAreMissing.size() == 1){
                if(howManyCardsAreMissing.get(0) == 42 && hand.stream().filter(card -> card.getId() == 52 || card.getId() == 53).count() == 2){
                    return howMuch;
                }
            }
        }

        return 0;
    }

    /**
     * Counts how many cards are missing to complete the bonus.
     * @param hand The cards in hand.
     * @return Number of the cards missing to complete the bonus.
     */
    private ArrayList<Integer> howManyCardsAreMissing(ArrayList<Card> hand){
        ArrayList<Integer> missingCards = new ArrayList<>(idCardsNeeded);
        for(int id: idCardsNeeded){
            for(Card card: hand){
                if(BigSwitches.switchIdForName(id).equals(card.getName())){
                    idCardsNeeded.remove(card.getId());
                }
            }
        }
        return missingCards;
    }
}
