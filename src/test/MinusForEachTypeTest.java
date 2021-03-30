package test;

import maluses.Malus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Card;
import server.Deck;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class MinusForEachTypeTest {


    Deck d;
    ArrayList<Card> deck;
    ArrayList<Card> cardsInHands;
    ArrayList<Card> cardsToDelete;
    Card cardToBeTested;

    //cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("")).findAny().get());

    @BeforeEach
    void initHand(){
        d = new Deck();
        d.initializeOriginal();
        d.setDeck(false);
        deck = d.getDeck();
        cardsInHands = new ArrayList<Card>();
        cardsToDelete = new ArrayList<Card>();
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardToBeTested = deck.stream().filter(card -> card.getNameLoc("cs").equals("Bažina")).findAny().get();
        cardsInHands.add(cardToBeTested);
        Logger log = Logger.getLogger("Loger");
        String msg = String.valueOf((cardToBeTested.getMaluses() == null));
        log.info(msg);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Kouzelnice")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Meč")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Štít")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Strom světa")).findAny().get());

    }

    @Test
    void countConditionSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card c = deck.stream().filter(card -> card.getNameLoc("cs").equals("Rytíři")).findAny().get();
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bouře")).findAny().get());
        cardsInHands.add(c);
        Malus malus = cardToBeTested.getMaluses().get(0);

        assertEquals(malus.count(cardsInHands), -3);
    }

    @Test
    void countConditionSatisfied2() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card c = deck.stream().filter(card -> card.getNameLoc("cs").equals("Hraničáři")).findAny().get();
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Těžká jízda")).findAny().get());
        cardsInHands.add(c);
        Malus malus = cardToBeTested.getMaluses().get(0);

        assertEquals(malus.count(cardsInHands), -6);
    }

    @Test
    void countConditionSatisfied3() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card c = deck.stream().filter(card -> card.getNameLoc("cs").equals("Svíčka")).findAny().get();
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Požár")).findAny().get());
        cardsInHands.add(c);
        Malus malus = cardToBeTested.getMaluses().get(0);

        assertEquals(malus.count(cardsInHands), -6);
    }



    @Test
    void countConditionNotSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card c = deck.stream().filter(card -> card.getNameLoc("cs").equals("Hora")).findAny().get();
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bouře")).findAny().get());
        cardsInHands.add(c);
        Malus malus = cardToBeTested.getMaluses().get(0);

        assertEquals(malus.count(cardsInHands), 0);
    }
}