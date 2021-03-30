package test;

import maluses.Malus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Card;

import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class CardIsDeletedIfYouHaveAnyTypeTest {

    ArrayList<Card> deck;
    ArrayList<Card> cardsInHands;
    ArrayList<Card> cardsToDelete;
    Card cardToBeTested;

    //cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("")).findAny().get());

    @BeforeEach
    void initHand(){
        deck = OriginalDeck.getDeck();

        cardsInHands = new ArrayList<Card>();
        cardsToDelete = new ArrayList<Card>();
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardToBeTested = deck.stream().filter(card -> card.getNameLoc("cs").equals("Vzducholoď")).findAny().get();
        cardsInHands.add(cardToBeTested);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Svíčka")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Královna")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Král")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Strom světa")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Hydra")).findAny().get());
    }

    @Test
    void countConditionSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Rytíři")).findAny().get());

        Malus malus = cardToBeTested.getMaluses().get(0);
        malus.count(cardsInHands, cardsToDelete);
        assertTrue(cardsInHands.contains(cardToBeTested));
    }

    @Test
    void countConditionNotSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bouře")).findAny().get());

        Malus malus = cardToBeTested.getMaluses().get(0);
        malus.count(cardsInHands, cardsToDelete);
        assertFalse(cardsInHands.contains(cardToBeTested)); }
}