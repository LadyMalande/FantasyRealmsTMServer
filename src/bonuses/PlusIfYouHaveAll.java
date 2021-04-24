package bonuses;

import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusIfYouHaveAll extends Bonus{
    public long serialVersionUID = 19;
    public final String text;
    private int how_much;
    public ArrayList<Integer> idsOfCardsNeeded;

    public PlusIfYouHaveAll(int hm, ArrayList<Integer> cards){
        this.text = "+" + hm + " if with " + giveListOfCardsWithSeparator(cards, " and ","en",1,false);
        this.how_much = hm;
        this.idsOfCardsNeeded = cards;
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
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        ResourceBundle rbbonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append("+" + how_much);
        sb.append(rbbonuses.getString("ifYouHave"));
        sb.append(" ");
            sb.append(giveListOfCardsWithSeparator(idsOfCardsNeeded, "and",locale,4,false));
        sb.append(".");
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int completed = 0;
        for(int id: idsOfCardsNeeded){
            for(Card card: hand){
                if(BigSwitches.switchIdForName(id).equals(card.name)){
                    completed++;
                }
            }
        }
        // if I have more princesses I still should get points for Unicorn
        if(idsOfCardsNeeded.size() <= completed){
            return how_much;
        } else {
            return 0;
        }
    }
    /*
    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        int completed = 0;
        for(int id: idsOfCardsNeeded){
            for(Card card: hand){
                if(BigSwitches.switchIdForName(id).equals(card.name)){
                    completed++;
                }
            }
        }
        // if I have more princesses I still should get points for Unicorn
        if(idsOfCardsNeeded.size() <= completed){
            return how_much;
        } else if(idsOfCardsNeeded.size() - completed < 2){
            long oddsOnTable = table.stream().filter(c -> types.contains(c.getType())).count() + table.stream().filter(c -> selftype == c.getType()).count();
            potential += (oddsOnTable - state.getNumberOfEnemies()*oddsOnTable/table.size()) * howMuch;
            long oddsOnDeck = state.getProbablyInDeck().stream().filter(c -> types.contains(c.getType())).count() + state.getProbablyInDeck().stream().filter(c -> selftype == c.getType()).count();
            potential += (deckSize / unknownCards) * oddsOnDeck/deckSize * howMuch;
        }
        return potential;
    }
*/
    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return idsOfCardsNeeded.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id)))));
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        int completed = 0;
        ArrayList<Integer> notcompleted = new ArrayList<>(idsOfCardsNeeded);
        for(int id: idsOfCardsNeeded){
            for(Card card: hand){
                if(BigSwitches.switchIdForName(id).equals(card.name)){
                    completed++;
                    notcompleted.remove(card.getId());
                }
            }
        }
        // if I have more princesses I still should get points for Unicorn
        if(idsOfCardsNeeded.size() <= completed){
            return 0;
        }
        if(idsOfCardsNeeded.size() - completed == 1 && BigSwitches.switchNameForType(BigSwitches.switchIdForName(notcompleted.get(0))) == t){
            return how_much;
        }
        return 0;
    }
}
