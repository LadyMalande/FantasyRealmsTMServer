package test;

import bonuses.Bonus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Card;

import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlusIfYouHaveAtLeastOneCardTest {
    ArrayList<Card> deck;
    ArrayList<Card> cardsInHands;
    Card cardToBeTested;

    //cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("")).findAny().get());

    @BeforeEach
    void initHand(){
        deck = OriginalDeck.getDeck();
        cardsInHands = new ArrayList<Card>();
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardToBeTested = deck.stream().filter(card -> card.getNameLoc("cs").equals("Elfský luk")).findAny().get();
        cardsInHands.add(cardToBeTested);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Král")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Svíčka")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Elfský luk")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Princezna")).findAny().get());


    }

    @Test
    void countConditionSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Šašek")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Velitel")).findAny().get());
        Bonus bonus = cardToBeTested.getBonuses().get(0);
        assertEquals(bonus.count(cardsInHands), 30);
    }

    @Test
    void countConditionSatisfied2() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Šašek")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Lučištníci")).findAny().get());
        Bonus bonus = cardToBeTested.getBonuses().get(0);
        assertEquals(bonus.count(cardsInHands), 30);
    }

    @Test
    void countConditionSatisfied3() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Šašek")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Pán šelem")).findAny().get());

        Bonus bonus = cardToBeTested.getBonuses().get(0);
        assertEquals(bonus.count(cardsInHands), 30);
    }

    @Test
    void countConditionSatisfied4() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Pán šelem")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Lučištníci")).findAny().get());

        Bonus bonus = cardToBeTested.getBonuses().get(0);
        assertEquals(bonus.count(cardsInHands), 30);
    }

    @Test
    void countConditionNotSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Šašek")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bouře")).findAny().get());

        Bonus bonus = cardToBeTested.getBonuses().get(0);
        assertEquals(bonus.count(cardsInHands), 0);
    }
}