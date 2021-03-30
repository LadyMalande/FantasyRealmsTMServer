package test;

import bonuses.Bonus;
import maluses.Malus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Card;

import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class DeletesAllExceptTypeOrCardTest {

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
        cardToBeTested = deck.stream().filter(card -> card.getNameLoc("cs").equals("Požár")).findAny().get();
        cardsInHands.add(cardToBeTested);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Svíčka")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Meč")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Štít")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Strom světa")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Jednorožec")).findAny().get());
    }

    @Test
    void countConditionSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card c = deck.stream().filter(card -> card.getNameLoc("cs").equals("Rytíři")).findAny().get();
        cardsInHands.add(c);
        Malus malus = cardToBeTested.getMaluses().get(0);
        malus.count(cardsInHands, cardsToDelete);
        assertTrue(cardsToDelete.contains(c));
    }

    @Test
    void countConditionNotSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card c = deck.stream().filter(card -> card.getNameLoc("cs").equals("Bouře")).findAny().get();
        cardsInHands.add(c);
        Malus malus = cardToBeTested.getMaluses().get(0);
        malus.count(cardsInHands, cardsToDelete);
        assertFalse(cardsToDelete.contains(c));
    }
}