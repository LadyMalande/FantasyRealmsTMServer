package bonuses;

import artificialintelligence.State;
import maluses.*;
import util.BigSwitches;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Bonus that represents bonus which removes given type on all maluses of given type.
 * @author Tereza Miklóšová
 */
public class DeleteTypeFromAllMalusesOnType extends Bonus  {
    /**
     * The priority of the bonus. Goes before the deleting penalties are counted.
     */
    public int priority = 5;
    /**
     * This type will be deleted from given types of maluses.
     */
    private Type deleteThisTypeFromMaluses;

    /**
     * On this type of maluses the given type to delete will be removed.
     */
    private Type onWhichType;

    /**
     * Constructor for the bonus.
     * @param whichType Which type is to be deleted from types on maluses.
     * @param onWhichType On which type of maluses will be the type deleted.
     */
    public DeleteTypeFromAllMalusesOnType(Type whichType, Type onWhichType){
        this.deleteThisTypeFromMaluses = whichType;
        this.onWhichType = onWhichType;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle bonuses = ResourceBundle.getBundle("bonuses.CardBonuses",loc);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(bonuses.getString("deletesWord"));
        sb.append(" ");
        sb.append(rb.getString(BigSwitches.switchTypeForName(deleteThisTypeFromMaluses).toLowerCase()));
        sb.append(" ");
        sb.append(bonuses.getString("fromAllMalusesOnAll"));
        sb.append(" ");
        sb.append(rb.getString(BigSwitches.switchTypeForName(onWhichType).toLowerCase() + "6"));
        sb.append(".");
        return sb.toString();
    }
    @Override
    public int getPriority(){ return this.priority; }
    @Override
    public int count(ArrayList<Card> hand) {
        for(Card c: hand){
            if(c.getType().equals(onWhichType)) {
                if (c.getMaluses() !=null && !c.getMaluses().isEmpty()) {
                    for (Malus m : c.getMaluses()) {
                        if((m.types != null) && (!m.types.isEmpty()) && (m.types.contains(deleteThisTypeFromMaluses))) {
                            m.types.remove(deleteThisTypeFromMaluses);
                            Logger log = Logger.getLogger("Loger");
                            String msg = "Removed text ";
                            log.info(msg);
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
            if (c.getType().equals(onWhichType)) {
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
        }
        saved = minus - withoutType;
        return saved;
    }

    @Override
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }
}
