package bonuses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;


import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PlusForEachCard extends Bonus {
    public long serialVersionUID = 7;
    private int how_much;



    private ArrayList<Integer> idsOfCardsNeeded;
    public final String text;

    public PlusForEachCard(int hm, ArrayList<Integer> ids){
        StringBuilder s = new StringBuilder();
        this.how_much = hm;
        this.idsOfCardsNeeded = ids;
        boolean first = true;
        for(Integer i: idsOfCardsNeeded){
            if(!first){
                s.append(", ");
            }
            s.append(BigSwitches.switchIdForName(i, "en"));
            first = false;
        }
        this.text = "+" + how_much + " for each of these: " + s;
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }

    public ArrayList<Integer> getIdsOfCardsNeeded() {
        return idsOfCardsNeeded;
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
        sb.append("+" + how_much);
        sb.append(" ");
        sb.append(rb.getString("forEachOfThese"));
        sb.append(giveListOfCardsWithSeparator(idsOfCardsNeeded,", ",locale,1,false));
        return sb.toString();
    }

    @Override
    public int count(ArrayList<Card> hand) {
        int sum = 0;
        for(Card c: hand){
            for(int id: idsOfCardsNeeded) {
                if (BigSwitches.switchIdForName(id, "en").equals(c.name)) {
                    sum += how_much;
                }
            }
        }
        return sum;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        // TODO
        return potential;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return idsOfCardsNeeded.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id)))));
    }
    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        if(idsOfCardsNeeded.stream().anyMatch(id -> t == (BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id)))))){
            return how_much;
        }
        return 0;
    }
}
