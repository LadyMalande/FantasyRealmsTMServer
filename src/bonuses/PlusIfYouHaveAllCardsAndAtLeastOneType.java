package bonuses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PlusIfYouHaveAllCardsAndAtLeastOneType extends Bonus  {
    public long serialVersionUID = 20;
    public final String text;
    private int howMuch;
    public ArrayList<Integer> idCardsNeeded;
    public ArrayList<Type> types;

    public PlusIfYouHaveAllCardsAndAtLeastOneType( int howMuch, ArrayList<Integer> idCardsNeeded, ArrayList<Type> types) {
        this.text = "+" + howMuch + " if with " + giveListOfCardsWithSeparator(idCardsNeeded, " and ", "en",1,false) + " and at least one " + giveListOfTypesWithSeparator(types, " or ");
        this.howMuch = howMuch;
        this.idCardsNeeded = idCardsNeeded;
        this.types = types;
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
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
            boolean hasoneofthese = false;
            for(Card c: hand){
                if(names.contains(c.name)){
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
                return null;
        } else{
            return null;
        }
    }

    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        return howMuch;
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        ResourceBundle rbbonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+" + howMuch);
        sb.append(rbbonuses.getString("ifYouHave"));
        sb.append(" ");
        sb.append(giveListOfCardsWithSeparator(idCardsNeeded, "and",locale,4,false));
        sb.append(" ");
        sb.append(rb.getString("and"));
        sb.append(" ");
        sb.append(rb.getString("atLeast"));
        sb.append(" ");
        sb.append(rb.getString("one4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types, "or",locale, 4,false));
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
            if(names.contains(c.name)){
                hascards++;
            }
            else if(types.contains(c.type)){
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
        double potential = 0.0;
        // TODO
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return idCardsNeeded.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id))))) ||
                types.stream().anyMatch(type -> types.contains(type)) ;

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

    private ArrayList<Integer> howManyCardsAreMissing(ArrayList<Card> hand){
        ArrayList<Integer> missingCards = new ArrayList<>(idCardsNeeded);
        for(int id: idCardsNeeded){
            for(Card card: hand){
                if(BigSwitches.switchIdForName(id).equals(card.name)){
                    idCardsNeeded.remove(card.getId());
                }
            }
        }
        return missingCards;
    }
}
