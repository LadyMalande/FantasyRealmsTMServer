package bonuses;

import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class BonusOrBonus extends Bonus {
    public long serialVersionUID = 2;
    private String text;
    private Bonus b1;
    private Bonus b2;

    public BonusOrBonus(Bonus b1, Bonus b2) {
        this.text = b1.getText() + "\n----- OR -----\n" + b2.getText();
        this.b1 = b1;
        this.b2 = b2;
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }

    @Override
    public int count(ArrayList<Card> hand) {
        return Math.max(b1.count(hand), b2.count(hand));
    }

    @Override
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        ArrayList<Type> b1Types = b1.getTypesAvailable(hand);
        ArrayList<Type> b2Types = b2.getTypesAvailable(hand);

        if(b1Types != null && b2Types != null){
            if(b2.getHowMuch(hand) > b1.getHowMuch(hand)){
                return b2Types;
            }
            return b1Types;
        }
        if(b2Types != null){
            return  b2Types;
        }
        if(b1Types != null){
            return b1Types;
        }
        return null;

    }
    @Override
    public int getHowMuch(ArrayList<Card> hand) {
        ArrayList<Type> b1Types = b1.getTypesAvailable(hand);
        ArrayList<Type> b2Types = b2.getTypesAvailable(hand);
        if(b1Types != null && b2Types != null){
            return Math.max(b1.getHowMuch(hand), b2.getHowMuch(hand));
        }
        if(b2Types != null){
            return  b2.getHowMuch(hand);
        }
        if(b1Types != null){
            return b1.getHowMuch(hand);
        }
        return 0;
    }

    @Override
    public Card satisfiesCondition(ArrayList<Card> hand)
    {
        if(b1 instanceof PlusIfYouHaveAtLeastOneType){
            return b1.satisfiesCondition(hand);
        }
        if(b2 instanceof PlusIfYouHaveAtLeastOneType){
            return b2.satisfiesCondition(hand);
        }
        return null;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        sb.append(this.b1.getText(locale));
        sb.append("\n----- ");
        sb.append(bonuses.getString("or"));
        sb.append(" -----\n");
        sb.append(this.b2.getText(locale));
        return sb.toString();
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        double p1 = b1.getPotential(hand, table, deckSize, unknownCards, state);
        double p2 = b2.getPotential(hand, table, deckSize, unknownCards, state);
        return Math.max(p1,p2);
    }

    @Override
    public Bonus clone() throws CloneNotSupportedException{
        BonusOrBonus newb = (BonusOrBonus)super.clone();
        newb.text = this.text;
        newb.priority = this.priority;
        newb.serialVersionUID = this.serialVersionUID;
        newb.b1 = this.b1.clone();
        newb.b2 = this.b1.clone();
        return newb;
    }

    public boolean reactsWithTypes(ArrayList<Type> types){
        if(reactsBonusWithTypes(b1, types) || reactsBonusWithTypes(b2, types)){
            return true;
        }
        return false;
    }

    public boolean reactsBonusWithTypes(Bonus b, ArrayList<Type> types){

        if(b instanceof PlusForEachType){
            return ((PlusForEachType) b).types.stream().anyMatch(type -> types.contains(type));
        }
        if(b instanceof PlusForEachTypeIfYouHaveCard){
            String name = BigSwitches.switchIdForSimplifiedName(((PlusForEachTypeIfYouHaveCard) b).cardid);
            return ((PlusForEachTypeIfYouHaveCard) b).types.stream().anyMatch(type -> types.contains(type)) ||
                    types.contains(BigSwitches.switchNameForType(name));
        }
        if(b instanceof PlusIfYouHaveAtLeastOneType){
            return ((PlusIfYouHaveAtLeastOneType) b).types.stream().anyMatch(type -> types.contains(type));
        }
        if(b instanceof PlusIfYouHaveAllCardsAndAtLeastOneType){
            return ((PlusIfYouHaveAllCardsAndAtLeastOneType) b).types.stream().anyMatch(type -> types.contains(type)) ||
                    ((PlusIfYouHaveAllCardsAndAtLeastOneType) b).idCardsNeeded.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id))))) ;
        }
        if(b instanceof PlusIfYouHaveAll){
            return ((PlusIfYouHaveAll) b).idsOfCardsNeeded.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id))))) ;
        }
        if(b instanceof PlusIfYouHaveAtLeastOneCard){
            return ((PlusIfYouHaveAtLeastOneCard) b).idsOfCardsNeeded.stream().anyMatch(id -> types.contains(BigSwitches.switchNameForType(BigSwitches.switchIdForSimplifiedName((id))))) ;
        }
        return false;
    }

    @Override
    public int getReaction(Type t, ArrayList<Card> hand){
        return Math.max(b1.getReaction(t, hand), b2.getReaction(t, hand));
    }
}
