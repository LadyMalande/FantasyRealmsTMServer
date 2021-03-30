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

class DeletesAllTypeOrOtherSelftypeTest {

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
        cardToBeTested = deck.stream().filter(card -> card.getNameLoc("cs").equals("Bazilišek")).findAny().get();
        cardsInHands.add(cardToBeTested);
        Logger log = Logger.getLogger("Loger");
        String msg = String.valueOf((cardToBeTested.getMaluses() == null));
        log.info(msg);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Kouzelnice")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Meč")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Štít")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Strom světa")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bouře")).findAny().get());
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
    void countConditionSatisfied2() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card c = deck.stream().filter(card -> card.getNameLoc("cs").equals("Královna")).findAny().get();
        cardsInHands.add(c);

        Logger log = Logger.getLogger("Loger");
        String msg = String.valueOf((cardToBeTested.getMaluses() == null));
        log.info(msg);
        Malus malus = cardToBeTested.getMaluses().get(0);
        malus.count(cardsInHands, cardsToDelete);
        assertTrue(cardsToDelete.contains(c));
    }

    @Test
    void countConditionSatisfied3() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card c = deck.stream().filter(card -> card.getNameLoc("cs").equals("Jednorožec")).findAny().get();
        cardsInHands.add(c);
        Malus malus = cardToBeTested.getMaluses().get(0);
        malus.count(cardsInHands, cardsToDelete);
        assertTrue(cardsToDelete.contains(c));
    }


    @Test
    void countConditionNotSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card c = deck.stream().filter(card -> card.getNameLoc("cs").equals("Hora")).findAny().get();
        cardsInHands.add(c);
        Malus malus = cardToBeTested.getMaluses().get(0);
        malus.count(cardsInHands, cardsToDelete);
        assertFalse(cardsToDelete.contains(c));
    }

    @Test
    void countConditionNotSatisfied2() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card c = deck.stream().filter(card -> card.getNameLoc("cs").equals("Blesk")).findAny().get();
        cardsInHands.add(c);
        Malus malus = cardToBeTested.getMaluses().get(0);
        malus.count(cardsInHands, cardsToDelete);
        assertFalse(cardsToDelete.contains(c));
    }

    @Test
    void countConditionNotSatisfied3() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card c = deck.stream().filter(card -> card.getNameLoc("cs").equals("Magická hůl")).findAny().get();
        cardsInHands.add(c);
        Malus malus = cardToBeTested.getMaluses().get(0);
        malus.count(cardsInHands, cardsToDelete);
        assertFalse(cardsToDelete.contains(c));
    }
}