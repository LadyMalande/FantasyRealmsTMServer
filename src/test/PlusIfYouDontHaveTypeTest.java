package test;

import bonuses.Bonus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Card;

import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class PlusIfYouDontHaveTypeTest {
    ArrayList<Card> deck;
    ArrayList<Card> cardsInHands;
    Card cardToBeTested;

    //cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("")).findAny().get());

    @BeforeEach
    void initHand(){
        deck = OriginalDeck.getDeck();
        cardsInHands = new ArrayList<Card>();
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardToBeTested = deck.stream().filter(card -> card.getNameLoc("cs").equals("Lučištníci")).findAny().get();
        cardsInHands.add(cardToBeTested);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Král")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Svíčka")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Elfský luk")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Princezna")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Požár")).findAny().get());

    }

    @Test
    void countConditionSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Císařovna")).findAny().get());
        Bonus bonus = cardToBeTested.bonuses.get(0);
        assertEquals(bonus.count(cardsInHands), 5);
    }

    @Test
    void countConditionNotSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Kouř")).findAny().get());


        Bonus bonus = cardToBeTested.bonuses.get(0);
        assertEquals(bonus.count(cardsInHands), 0);
    }


}