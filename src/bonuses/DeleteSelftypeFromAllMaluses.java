package bonuses;

import artificialintelligence.State;
import maluses.*;
import util.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Bonus that represents bonus which deletes its selftype from all maluses.
 * @author Tereza Miklóšová
 */
public class DeleteSelftypeFromAllMaluses extends Bonus  {
    /**
     * The priority of the bonus. Goes before the deleting penalties are counted.
     */
    public int priority = 5;
    /**
     * This type will be deleted in all occurrences of types on maluses.
     */
    private Type deleteThisTypeFromMaluses;

    /**
     * Constructor for DeleteSelftypeFromAllMaluses.
     * @param t Type which will be deleted from all list of types to punish on maluses.
     */
    public DeleteSelftypeFromAllMaluses(Type t){
        this.deleteThisTypeFromMaluses = t;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses", loc);
        ResourceBundle types = ResourceBundle.getBundle("server.CardTypes", loc);
        sb.append(bonuses.getString("deleteSelfTypeFromAllMaluses")).append(" ");
        sb.append(types.getString(Objects.requireNonNull(BigSwitches.switchTypeForName(deleteThisTypeFromMaluses)).toLowerCase()));
        sb.append(bonuses.getString("deleteSelfTypeFromAllMaluses2"));
        return sb.toString();
    }
    @Override
    public int getPriority(){ return this.priority; }

    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(c.getMaluses() != null && !c.getMaluses().isEmpty()){
                for(Malus m: c.getMaluses()){

                    if(m.types != null && m.types.contains(deleteThisTypeFromMaluses)) {
                        if(!(m instanceof DeletesAllExceptTypeOrCard) && !(m instanceof MinusIfYouDontHaveAtLeastOneType)) {
                            m.types.remove(deleteThisTypeFromMaluses);
                        }
                    }
                    if(m instanceof DeletesAllTypeOrOtherSelftype){
                        if(deleteThisTypeFromMaluses == ((DeletesAllTypeOrOtherSelftype) m).selftype){
                            ((DeletesAllTypeOrOtherSelftype) m).selftype = null;
                        }
                    }
                    if(m instanceof MinusForEachOtherSelftypeOrType){
                        if(deleteThisTypeFromMaluses == ((MinusForEachOtherSelftypeOrType) m).selftype){
                            ((MinusForEachOtherSelftypeOrType) m).selftype = null;
                        }
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double saved;
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
                if(m instanceof MinusForEachType){
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

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }
}
