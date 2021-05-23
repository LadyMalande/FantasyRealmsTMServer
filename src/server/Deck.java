package server;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class representing deck full of cards for the game.
 * @author Tereza Miklóšová
 */
public class Deck implements Serializable {
    /**
     * The list of cards in the deck.
     */
    private ArrayList<Card> deck;
    /**
     * List of original cards.
     */
    private ArrayList<Card> deckOriginal;
    /**
     * List of randomly initialized cards.
     */
    private ArrayList<Card> deckRandom;

    /**
     * Maximal strength of the cards in the deck.
     */
    private int maxStrength;

    /**
     * Get {@link Deck#deck}.
     * @return {@link Deck#deck}
     */
    public ArrayList<Card> getDeck(){
        return deck;
    }

    /**
     * Initializes the original deck. Fills the deck holder with cards.
     */
    public void initializeOriginal(){
        DeckInitializer di = new DeckInitializer();
        di.storeDecktoFile();
        deckOriginal = DeckInitializer.loadDeckFromFile();
        maxStrength = 0;
        for(Card c: deckOriginal){
            if(c.getStrength() > maxStrength){
                maxStrength = c.getStrength();
            }
        }
    }

    /**
     * Creates new deck full of randomly initialized cards.
     */
    public void initializeRandom(){
        DeckGenerator dg = new DeckGenerator();
        deckRandom = dg.generateDeck();
    }

    /**
     * Sets {@link Deck#deck}.
     * @param random True if player wants to play with random deck. False if he wants to play with original one.
     */
    public void setDeck(boolean random){
        if(random){
            initializeRandom();
            deck = new ArrayList<>(deckRandom);
        } else{
            deck = new ArrayList<>(deckOriginal);
        }
    }

    /**
     * Shuffles the initialized deck to randomize the order of cards on the deck.
     */
    public void shuffleDeck(){
        Collections.shuffle(deck);
    }
}
