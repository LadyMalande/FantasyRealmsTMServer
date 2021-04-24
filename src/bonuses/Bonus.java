package bonuses;

import artificialintelligence.State;
import maluses.Malus;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Bonus implements ScoringInterface, Serializable, Cloneable {
    public long serialVersionUID = 1;
    public int priority = 8;
    private String text;
    public String getText(){return this.text;}
    public String getText(String locale){
        return this.text;
    }
    public int getPriority(){
        return this.priority;
    }

    @Override
    public int count(ArrayList<Card> hand) {
        return 0;
    }

    String giveListOfCardsWithSeparator(ArrayList<Integer> cards, String separator, String locale) {
        StringBuilder listcards = new StringBuilder();
        boolean first = true;
        for (Integer c : cards) {
            if (!first) {
                listcards.append(separator);
            }
            listcards.append(BigSwitches.switchIdForName(c, locale));
            first = false;
        }
        return listcards.toString();
    }

    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        return potential;
    }

    String giveListOfTypesWithSeparator(ArrayList<Type> types, String separator,String locale, int grammar, boolean plural){
        Locale loc = new Locale(locale);
        ResourceBundle rs = ResourceBundle.getBundle("server.CardTypes",loc);
        StringBuilder listtypes = new StringBuilder();
        boolean first = true;
        String pl = "";
        if(plural){
            pl = "pl";
        }
        for(Type type: types){
            if(!first && types.get(types.size() - 1).equals(type)){
                if(separator.equals("or") || separator.equals("and")){
                    listtypes.append(" ");
                    listtypes.append(rs.getString(separator));
                    listtypes.append(" ");
                } else{
                    listtypes.append(separator);
                }

            } else if (!first){
                listtypes.append(", ");
            }
            if(grammar == 1){
                listtypes.append(rs.getString(Objects.requireNonNull(BigSwitches.switchTypeForName(type)).toLowerCase() + pl));

            } else{
                listtypes.append(rs.getString(Objects.requireNonNull(BigSwitches.switchTypeForName(type)).toLowerCase() + grammar + pl));

            }
            first = false;
        }
        return listtypes.toString();
    }

    String giveListOfCardsWithSeparator(ArrayList<Integer> ids, String separator,String locale, int grammar, boolean plural){
        Locale loc = new Locale(locale);
        ResourceBundle rs = ResourceBundle.getBundle("server.CardNames",loc);
        ResourceBundle types = ResourceBundle.getBundle("server.CardTypes",loc);
        StringBuilder listtypes = new StringBuilder();
        boolean first = true;
        String pl = "";
        if(plural){
            pl = "pl";
        }
        for(Integer id: ids){
            if(!first && ids.get(ids.size() - 1).equals(id)){
                if(separator.equals("or") || separator.equals("and")){
                    listtypes.append(" ");
                    listtypes.append(types.getString(separator));
                    listtypes.append(" ");
                } else{
                    listtypes.append(separator);
                }
            } else if (!first){
                listtypes.append(", ");
            }
            if(grammar == 1){
                listtypes.append(rs.getString(Objects.requireNonNull(BigSwitches.switchIdForSimplifiedName(id)).toLowerCase() + pl));

            } else{
                listtypes.append(rs.getString(Objects.requireNonNull(BigSwitches.switchIdForSimplifiedName(id)).toLowerCase() + grammar + pl));

            }
            first = false;
        }
        return listtypes.toString();
    }

    public String giveListOfTypesWithSeparator(ArrayList<Type> types, String separator){
        StringBuilder listtypes = new StringBuilder();
        boolean first = true;
        for(Type type: types){
            if(!first){
                listtypes.append(separator);
            }
            listtypes.append(BigSwitches.switchTypeForName(type));
            first = false;
        }
        return listtypes.toString();
    }

    public Bonus clone() throws CloneNotSupportedException{
        Bonus newb = (Bonus)super.clone();
        newb.text = this.text;
        newb.priority = this.priority;
        newb.serialVersionUID = this.serialVersionUID;
        return newb;
    }

    public static double giveValue(ArrayList<Card> hand, ArrayList<Card> whatToRemove){
        double sum = 0.0;
        for(Card removed : whatToRemove){
            sum += removed.getStrength();
            for(Bonus b : removed.bonuses){
                sum += b.count(hand);
            }
            for(Malus m : removed.maluses){
                sum += m.count(hand);
            }
        }
        return sum;
    }

    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }

    public int getReaction(Type t, ArrayList<Card> hand){
        return 0;
    }
}
