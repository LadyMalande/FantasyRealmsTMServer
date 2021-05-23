package util;

import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Support class for creating localized texts.
 * @author Tereza Miklóšová
 */
public class TextCreators {

    /**
     * Creates a part of the text for the bonus by linking names of cards with given separator.
     * @param ids Ids of cards to link to text.
     * @param separator Separator to use to link the names.
     * @param locale Language in which the text should be.
     * @param grammar Which case should be used for declination of the names of the cards.
     * @return Localized linked names of the cards.
     */
    public static String giveListOfCardsWithSeparator(ArrayList<Integer> ids, String separator, String locale, int grammar){
        Locale loc = new Locale(locale);
        ResourceBundle rs = ResourceBundle.getBundle("server.CardNames",loc);
        ResourceBundle types = ResourceBundle.getBundle("server.CardTypes",loc);
        StringBuilder listtypes = new StringBuilder();
        boolean first = true;
        String pl = "";
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

    /**
     * Creates a part of the text for the bonus by linking types with given separator.
     * @param types Types to link into text.
     * @param separator Separator to use to link the types.
     * @param locale Language in which the text should be.
     * @param grammar Which case should be used for declination of the types.
     * @return Localized linked types.
     */
    public static String giveListOfTypesWithSeparator(ArrayList<Type> types, String separator, String locale, int grammar){
        Locale loc = new Locale(locale);
        ResourceBundle rs = ResourceBundle.getBundle("server.CardTypes",loc);
        StringBuilder listtypes = new StringBuilder();
        boolean first = true;
        String pl = "";
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

    /**
     * Creates a part of the text for the malus by linking types with given separator.
     * @param types Types to link into text.
     * @param separator Separator used to join the types together.
     * @param locale Language in which the text should be.
     * @param grammar Which case should be used for declination of the types.
     * @param plural Plural number of the word or not.
     * @return Text of localized list of gramatically edited types.
     */
    public static String giveListOfTypesWithSeparator(ArrayList<Type> types, String separator, String locale, int grammar, boolean plural){
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

    /**
     * Makes a text of card names lined together by separator.
     * @param cards Cards to join together in the text.
     * @param locale Language in which the text should be.
     * @param grammar Which case should be used for declination of the types.
     * @return Text of localized names of cards joined together.
     */
    public static String giveListOfCardsWithSeparator(ArrayList<Integer> cards, String locale, int grammar) {
        Locale loc = new Locale(locale);
        ResourceBundle rs = ResourceBundle.getBundle("server.CardNames",loc);
        StringBuilder listcards = new StringBuilder();
        boolean first = true;
        String pl = "";
        for (Integer c : cards) {
            if(!first && cards.get(cards.size() - 1).equals(c)){
                listcards.append(", ");

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
}
