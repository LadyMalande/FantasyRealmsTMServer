package maluses;

import artificialintelligence.State;
import bonuses.ScoringInterface;
import server.BigSwitches;
import server.Card;
import server.Type;

import java.io.Serializable;
import java.util.*;


public class Malus implements ScoringInterface , Serializable, Cloneable  {

    public int priority = 6;
    public ArrayList<Type> types;
    public ArrayList<Integer> cards;
    public String text;

    public String getText(){
        return this.text;
    }
    public String getText(String locale){  return this.text; }
    public int getPriority(){
        return this.priority;
    }
    public  ArrayList<Type> getTypes(){
        return this.types;
    }
    public ArrayList<Integer> getCards(){ return null;}
    @Override
    public int count(ArrayList<Card> hand){
        return 0;
    }
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToRemove){return 0;}


    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {

        return types;
    }

    public Card satisfiesCondition(ArrayList<Card> hand)
    {
        //Says ids of cards that cant be recolored if the size of this array is only 1
        return null;
    }
    public int getHowMuch(ArrayList<Card> hand) {
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
            if(!first && cards.get(cards.size() - 1).equals(c)){
                if(separator.equals("or") || separator.equals("and")){
                    listcards.append(" ");
                    listcards.append(maluses.getString(separator));
                    listcards.append(" ");
                } else{
                    listcards.append(separator);
                }

            } else if (!first){
                listcards.append(", ");
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
            if(!first && types.get(types.size() - 1).equals(type)){
                if(separator.equals("or") || separator.equals("and")){
                    listtypes.append(" ");
                    listtypes.append(maluses.getString(separator));
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

    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        // TODO
        return potential;
    }

    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }

    public int getReaction(Type t, ArrayList<Card> hand) {
        return 0;
    }

    public ArrayList<Integer> returnWillBeDeleted(){
        return null;
    }
}
