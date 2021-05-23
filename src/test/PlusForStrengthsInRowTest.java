package test;

import bonuses.Bonus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Card;

import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class PlusForStrengthsInRowTest {
    ArrayList<Card> deck;
    ArrayList<Card> cardsInHands;
    Card cardToBeTested;

    //cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("")).findAny().get());

    @BeforeEach
    void initHand(){
        deck = OriginalDeck.getDeck();
        cardsInHands = new ArrayList<Card>();
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardToBeTested = deck.stream().filter(card -> card.getNameLoc("cs").equals("Krystal řádu")).findAny().get();
        cardsInHands.add(cardToBeTested);
    }

    @Test
    void countConditionSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");

        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Ochranná runa")).findAny().get()); //1
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Svíčka")).findAny().get()); //2
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Šašek")).findAny().get()); //3

        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Nejvyšší mág")).findAny().get()); //25
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bažina")).findAny().get()); //18
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bazilišek")).findAny().get()); //35
        Bonus bonus = cardToBeTested.getBonuses().get(0);
        assertEquals(bonus.count(cardsInHands), 10);
    }

    @Test
    void countConditionSatisfied2() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Ochranná runa")).findAny().get()); //1
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Svíčka")).findAny().get()); //2
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Šašek")).findAny().get()); //3

        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Královna")).findAny().get()); //6
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Meč")).findAny().get()); //7
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bouře")).findAny().get()); //8
        Bonus bonus = cardToBeTested.getBonuses().get(0);
        assertEquals(bonus.count(cardsInHands), 30);
    }

    @Test
    void countConditionSatisfied3() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Ochranná runa")).findAny().get()); //1
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Svíčka")).findAny().get()); //2
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Elementál vody")).findAny().get()); //4

        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Královna")).findAny().get()); //6
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Meč")).findAny().get()); //7
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bouře")).findAny().get()); //8

        Bonus bonus = cardToBeTested.getBonuses().get(0);
        assertEquals(bonus.count(cardsInHands), 60);
    }

    @Test
    void countConditionSatisfied4() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Ochranná runa")).findAny().get()); //1
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Elfský luk")).findAny().get()); //3
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Elementál vody")).findAny().get()); //4

        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Královna")).findAny().get()); //6
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Meč")).findAny().get()); //7
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bouře")).findAny().get()); //8

        Bonus bonus = cardToBeTested.getBonuses().get(0);
        assertEquals(bonus.count(cardsInHands), 100);
    }

    @Test
    void countConditionSatisfied5() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Svíčka")).findAny().get()); //2
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Elfský luk")).findAny().get()); //3
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Elementál vody")).findAny().get()); //4

        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Královna")).findAny().get()); //6
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Meč")).findAny().get()); //7
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bouře")).findAny().get()); //8

        Bonus bonus = cardToBeTested.getBonuses().get(0);
        assertEquals(bonus.count(cardsInHands), 150);
    }

    @Test
    void countConditionNotSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Ochranná runa")).findAny().get()); //1
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Šašek")).findAny().get()); //3
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bouře")).findAny().get()); //8

        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Nejvyšší mág")).findAny().get()); //25
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bažina")).findAny().get()); //18
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bazilišek")).findAny().get()); //35

        Bonus bonus = cardToBeTested.getBonuses().get(0);
        assertEquals(bonus.count(cardsInHands), 0);
    }
}