package util;

import server.Card;
import server.DeckInitializer;
import server.Type;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Contains static methods for making various ArrayLists.
 * @author Tereza Miklóšová
 */
public class ArrayListCreator {

    /**
     * Creates list of names of members of given types.
     * @param types Types of the members we want to collect.
     * @param locale Target language.
     * @return ArrayList of localized names of cards of given types.
     */
    public static ArrayList<String> createListOfNamesFromTypes(ArrayList<Type> types, Locale locale){
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Card> deck = DeckInitializer.loadDeckFromFile();
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames", locale);
        for(Type t: types){
            for(Card c: deck.stream().filter(card -> card.getType().equals(t)).collect(Collectors.toList())){
                result.add(rb.getString(BigSwitches.switchIdForSimplifiedName(c.getId())));
            }
        }
        return result;
    }

}
