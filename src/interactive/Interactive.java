package interactive;

import server.BigSwitches;
import server.Card;
import server.ClientHandler;
import server.Type;

import java.io.Serializable;
import java.net.Socket;
import java.util.*;

public class Interactive implements InteractiveBonusInterface , Serializable, Cloneable  {
    public String text;
    public int priority=3;

    public String getText(){
        return this.text;
    }

    public String getText(String locale){ return this.text; }
    public int getPriority(){
        return this.priority;
    }
    @Override
    public boolean askPlayer(ClientHandler client) {
        return true;
    }

    public String giveListOfTypesWithSeparator(ArrayList<Type> types, String separator){
        String listtypes = "";
        boolean first = true;
        for(Type type: types){
            if(!first){
                listtypes += separator;
            }
            listtypes += BigSwitches.switchTypeForName(type);
            first = false;
        }
        return listtypes;
    }

    String giveListOfTypesWithSeparator(ArrayList<Type> types, String separator,String locale, int grammar){
        Locale loc = new Locale(locale);
        ResourceBundle rs = ResourceBundle.getBundle("server.CardTypes",loc);
        StringBuilder listtypes = new StringBuilder();
        boolean first = true;
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
                listtypes.append(rs.getString(Objects.requireNonNull(BigSwitches.switchTypeForName(type)).toLowerCase()));

            } else{
                listtypes.append(rs.getString(Objects.requireNonNull(BigSwitches.switchTypeForName(type)).toLowerCase() + grammar));

            }
            first = false;
        }
        return listtypes.toString();
    }

    public String giveStringOfStringsWithSeparator(ArrayList<String> strings, String separator){
        String listtypes = "";
        boolean first = true;
        for(String str: strings){
            if(!first){
                listtypes += separator;
            }
            listtypes += str;
            first = false;
        }
        return listtypes;
    }

    public Interactive clone() throws CloneNotSupportedException{
        Interactive newi = (Interactive)super.clone();
        return newi;
    }

    public void changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {

    }

    public void writeAllCardsAndTheirAttributes(List<Card> cards){
        for(Card c: cards){
            System.out.print(c.name + "(" + c.strength + "): ");
            if(c.bonuses != null){
                System.out.print(c.bonuses.size() + " bonusu, ");
            }
            if(c.maluses != null){
                System.out.print(c.maluses.size() + " postihu, ");
            }
            if(c.interactives != null){
                System.out.print(c.interactives.size() + " interaktivnich");
            }
            System.out.println();
        }

    }
}
