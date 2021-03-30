package test;

import bonuses.Bonus;
import maluses.Malus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Card;
import server.Deck;
import server.Type;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class DeleteTypeFromAllMalusesOnTypeTest {


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
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardToBeTested = deck.stream().filter(card -> card.getNameLoc("cs").equals("Válečná loď")).findAny().get();
        cardsInHands.add(cardToBeTested);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Svíčka")).findAny().get());
    }

    @Test
    void countConditionSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card toBeEnhanced = deck.stream().filter(card -> card.getNameLoc("cs").equals("Bažina")).findAny().get();
        cardsInHands.add(toBeEnhanced);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Hydra")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Královna")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Hraničáři")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Strom světa")).findAny().get());
        Bonus bonus = cardToBeTested.bonuses.get(0);
        bonus.count(cardsInHands);
        assertFalse(toBeEnhanced.getMaluses().get(0).getTypes().stream().anyMatch(type -> type == Type.ARMY));
    }

    @Test
    void countConditionSatisfied2() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card toBeEnhanced = deck.stream().filter(card -> card.getNameLoc("cs").equals("Stoletá voda")).findAny().get();
        cardsInHands.add(toBeEnhanced);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Hydra")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Královna")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Hraničáři")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Strom světa")).findAny().get());
        Bonus bonus = cardToBeTested.bonuses.get(0);
        bonus.count(cardsInHands);

        assertNotNull(toBeEnhanced.maluses.get(0).getTypes());
        assertFalse(toBeEnhanced.maluses.get(0).getTypes().contains(Type.ARMY));
    }

}