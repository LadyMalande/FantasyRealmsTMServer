package server;


import java.io.Serializable;
import java.util.ArrayList;


public class Deck implements Serializable {
    ArrayList<Card> deck;
    public int maxStrength;

    ArrayList<Card> getDeck(){
        return deck;
    }
    Deck(){
        DeckInitializer di = new DeckInitializer();
        di.storeDecktoFile();
        deck = di.loadDeckFromFile();
        maxStrength = 0;
        for(Card c: deck){
            if(c.strength > maxStrength){
                maxStrength = c.strength;
            }
        }
    }
}
