package bonuses;

import artificialintelligence.State;
import maluses.*;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import static server.Type.NONE;

public class DeleteSelftypeFromAllMaluses extends Bonus  {
    public long serialVersionUID = 5;
    public int priority = 5;
    private Type deleteThisTypeFromMaluses;
    public final String text;

    public DeleteSelftypeFromAllMaluses(Type t){

        this.deleteThisTypeFromMaluses = t;
        this.text = "Remove word " + BigSwitches.switchTypeForName(deleteThisTypeFromMaluses) + " from all maluses";
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
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses", loc);
        ResourceBundle types = ResourceBundle.getBundle("server.CardTypes", loc);
        sb.append(bonuses.getString("deleteSelfTypeFromAllMaluses"));
        sb.append(types.getString(Objects.requireNonNull(BigSwitches.switchTypeForName(deleteThisTypeFromMaluses)).toLowerCase()));
        sb.append(bonuses.getString("deleteSelfTypeFromAllMaluses2"));
        return sb.toString();
    }
    @Override
    public int getPriority(){ return this.priority; }
    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(c.maluses != null && !c.maluses.isEmpty()){
                for(Malus m: c.maluses){

                    if(m.types != null && m.types.contains(deleteThisTypeFromMaluses)) {
                        if(!(m instanceof DeletesAllExceptTypeOrCard) && !(m instanceof MinusIfYouDontHaveAtLeastOneType)) {
                            m.types.remove(deleteThisTypeFromMaluses);
                        }
                        //System.out.println("------------------Mazu typ "  + deleteThisTypeFromMaluses + "  z karty " + c.name);
                    }
                    if(m instanceof DeletesAllTypeOrOtherSelftype){
                        if(deleteThisTypeFromMaluses == ((DeletesAllTypeOrOtherSelftype) m).selftype){
                            ((DeletesAllTypeOrOtherSelftype) m).selftype = NONE;
                        }
                    }
                    if(m instanceof MinusForEachOtherSelftypeOrType){
                        if(deleteThisTypeFromMaluses == ((MinusForEachOtherSelftypeOrType) m).selftype){
                            ((MinusForEachOtherSelftypeOrType) m).selftype = NONE;
                        }
                    }
                }
            }

        }
        return 0;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double saved = 0.0;
        double minus = 0.0;
        double withoutType = 0.0;
        for(Card c : hand){
            for(Malus m: c.getMaluses()){
                if(m instanceof DeletesAllType || m instanceof DeletesAllTypeExceptCard || m instanceof DeletesAllTypeOrOtherSelftype){
                    ArrayList<Card> whatToRemove = new ArrayList<>();
                    m.count(hand, whatToRemove);
                    minus = giveValue(hand, whatToRemove);
                    m.types.remove(deleteThisTypeFromMaluses);
                    withoutType += giveValue(hand, whatToRemove);
                    m.types.add(deleteThisTypeFromMaluses);
                }
                if(m instanceof MinusForEachType || m instanceof MinusForEachType){
                    minus += m.count(hand);
                    m.types.remove(deleteThisTypeFromMaluses);
                    withoutType += m.count(hand);
                    m.types.add(deleteThisTypeFromMaluses);
                }
            }
        }
        saved = minus - withoutType;
        return saved;
    }
}
