package maluses;

import bonuses.ScoringInterface;
import interactive.Interactive;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;


public class Malus implements ScoringInterface , Serializable, Cloneable  {

    public int priority = 6;
    public ArrayList<Type> types;
    public ArrayList<Card> cards;
    public String text;

    public String getText(){
        return this.text;
    }
    public String getText(String locale){  return this.text; }
    public int getPriority(){
        return this.priority;
    }
    @Override
    public int count(ArrayList<Card> hand){
        return 0;
    }
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToRemove){return 0;}

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

    String giveListOfCardsWithSeparator(ArrayList<Integer> cards, String separator, String locale, int grammar, boolean plural) {
        Locale loc = new Locale(locale);
        ResourceBundle rs = ResourceBundle.getBundle("server.CardNames",loc);
        ResourceBundle maluses = ResourceBundle.getBundle("maluses.CardMaluses",loc);
        StringBuilder listcards = new StringBuilder();
        boolean first = true;
        String pl = "";
        if(plural){
            pl = "pl";
        }
        for (Integer c : cards) {
            if (!first) {
                if(separator.equals("or") || separator.equals("and")){
                    listcards.append(" ");
                    listcards.append(maluses.getString(separator));
                    listcards.append(" ");
                } else{
                    listcards.append(separator);
                }
            }
            if(grammar == 1){
                listcards.append(rs.getString(Objects.requireNonNull(BigSwitches.switchIdForSimplifiedName(c)).toLowerCase() + pl));

            } else{
                listcards.append(rs.getString(Objects.requireNonNull(BigSwitches.switchIdForSimplifiedName(c)).toLowerCase() + grammar + pl));

            }
            first = false;
        }
        return listcards.toString();
    }

    String giveListOfTypesWithSeparator(ArrayList<Type> types, String separator,String locale, int grammar, boolean plural){
        Locale loc = new Locale(locale);
        ResourceBundle rs = ResourceBundle.getBundle("server.CardTypes",loc);
        ResourceBundle maluses = ResourceBundle.getBundle("maluses.CardMaluses",loc);
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
                    listtypes.append(maluses.getString(separator));
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

    String giveListOfTypesWithSeparator(ArrayList<Type> types, String separator){
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

    public Malus clone() throws CloneNotSupportedException{
        Malus newm = (Malus)super.clone();
        //System.out.println("Clone malus in Malus class");
        return newm;
    }
}
