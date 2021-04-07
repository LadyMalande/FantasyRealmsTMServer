package server;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;


public class Deck implements Serializable {
    ArrayList<Card> deck;
    ArrayList<Card> deckOriginal;
    ArrayList<Card> deckRandom;
    public int maxStrength;

    public ArrayList<Card> getDeck(){
        return deck;
    }

    public void initializeOriginal(){
        DeckInitializer di = new DeckInitializer();
        di.storeDecktoFile();
        deckOriginal = DeckInitializer.loadDeckFromFile();
        maxStrength = 0;
        for(Card c: deckOriginal){
            if(c.strength > maxStrength){
                maxStrength = c.strength;
            }
        }
    }

    public void initializeRandom(){
        DeckGenerator dg = new DeckGenerator();
        deckRandom = dg.generateDeck();
    }

    public void setDeck(boolean random){
        if(random){
            deck = new ArrayList<>(deckRandom);

        } else{
            deck = new ArrayList<>(deckOriginal);
        }
    }

    public void shuffleDeck(){
        Collections.shuffle(deck);
        for(Card c: deck){
            System.out.print(c.getName()+ ", ");
        }
        System.out.println();
    }
}
