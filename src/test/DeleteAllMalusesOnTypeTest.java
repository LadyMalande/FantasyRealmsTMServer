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

class DeleteAllMalusesOnTypeTest {
    Deck d;
    ArrayList<Card> deck;
    ArrayList<Card> cardsInHands;

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
        cardToBeTested = deck.stream().filter(card -> card.getNameLoc("cs").equals("Hora")).findAny().get();
        cardsInHands.add(cardToBeTested);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Svíčka")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Královna")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Král")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Strom světa")).findAny().get());
    }

    @Test
    void countConditionSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        Card toBeEnhanced = deck.stream().filter(card -> card.getNameLoc("cs").equals("Bažina")).findAny().get();
        cardsInHands.add(toBeEnhanced);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Hydra")).findAny().get());

        Bonus bonus = cardToBeTested.bonuses.get(0);
        bonus.count(cardsInHands);
        assertEquals(toBeEnhanced.getMaluses().size(), 0);
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
        Logger log = Logger.getLogger("Loger");
        String msg = "Text on Malus " + cardsInHands.stream().filter(card -> card.getNameLoc("cs").equals("Stoletá voda")).findAny().get().getMaluses().get(0).getText("cs");
        log.info(cardToBeTested.getName());
        log.info(cardToBeTested.bonuses.get(0).getText("cs"));
        log.info(String.valueOf(toBeEnhanced.maluses.size()));
        for(Malus m : toBeEnhanced.getMaluses()){
            for(Type t: m.getTypes()){
                log.info(t.name()
                );
            }
        }

        assertTrue(cardsInHands.stream().filter(card -> card.getNameLoc("cs").equals("Stoletá voda")).findAny().get().getMaluses().size() == 0, "howmanhy " + cardsInHands.stream().filter(card -> card.getNameLoc("cs").equals("Stoletá voda")).findAny().get().getMaluses().size());
    }
}