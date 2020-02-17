package server;


import maluses.Malus;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ArrayListCreator {
    public static ArrayList<String> createListOfStringsFromTypes(ArrayList<Type> types){
        ArrayList<String> result = new ArrayList<>();
        for(Type t: types){
            result.add(BigSwitches.switchTypeForName(t));
        }
        return result;
    }

    public static ArrayList<String> createListOfNamesFromTypes(ArrayList<Type> types){
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Card> deck = DeckInitializer.loadDeckFromFile();
        for(Type t: types){
            for(Card c: deck.stream().filter(card -> card.type.equals(t)).collect(Collectors.toList())){
                result.add(c.name);
            }
        }
        return result;
    }

    public static ArrayList<String> createListOfNamesFromListOfCards(ArrayList<Card> cards){
        ArrayList<String> result = new ArrayList<>();

        for(Card c: cards){
            result.add(c.name);
        }
        return result;
    }

    public static ArrayList<String> createListOfTextFromMalus(Card card){
        ArrayList<String> result = new ArrayList<>();
        if(card.maluses == null || card.maluses.isEmpty()){
            return null;
        }else {
            for (Malus m : card.maluses) {
                result.add(m.getText());
            }
            return result;
        }
    }

}
