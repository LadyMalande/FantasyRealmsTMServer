package server;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ArrayListCreator {

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

}
