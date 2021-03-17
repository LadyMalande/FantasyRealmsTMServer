package bonuses;

import interactive.Interactive;
import server.BigSwitches;
import server.Type;
import server.Card;

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
            if(!first){
                if(separator.equals("or") || separator.equals("and")){
                    listtypes.append(" ");
                    listtypes.append(rs.getString(separator));
                    listtypes.append(" ");
                } else{
                    listtypes.append(separator);
                }

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
            if(!first){
                if(separator.equals("or") || separator.equals("and")){
                    listtypes.append(" ");
                    listtypes.append(types.getString(separator));
                    listtypes.append(" ");
                } else{
                    listtypes.append(separator);
                }

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
}
