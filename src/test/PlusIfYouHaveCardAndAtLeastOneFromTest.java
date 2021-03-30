package test;

import bonuses.*;
import maluses.DeletesAllTypeExceptCard;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Card;
import server.Deck;
import server.Type;

import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class PlusIfYouHaveCardAndAtLeastOneFromTest {
    ArrayList<Card> cardsInHands;
    Card cardToBeTested;

    @BeforeEach
    void initHand(){
        cardsInHands = new ArrayList<Card>();
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardToBeTested = new Card(49, rb.getString("tornado"),13, Type.WEATHER, new ArrayList<>() {{add(new PlusIfYouHaveCardAndAtLeastOneFrom(40, new ArrayList<>() {{add(47);add(33);}}, 50));}}, null, null);
        cardsInHands.add(cardToBeTested);
        cardsInHands.add(new Card(43, rb.getString("eelemental"),4, Type.LAND, new ArrayList<>() {{add(new PlusForEachSelftypeExceptThis(15,43,Type.LAND));}}, null, null));;
        cardsInHands.add(new Card(44, rb.getString("cavern"),6, Type.LAND, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneCard(25,new ArrayList<>() {{add(5);add(24);}}));add(new DeleteAllMalusesOnType(Type.WEATHER));}}, null, null));
        cardsInHands.add(new Card(45, rb.getString("mountain"),9, Type.LAND, new ArrayList<>() {{add(new PlusIfYouHaveAll(50, new ArrayList<>() {{add(51);add(41);}}));add(new DeleteAllMalusesOnType(Type.FLOOD));}}, null, null));
        cardsInHands.add(new Card(46, rb.getString("forest"),7, Type.LAND, new ArrayList<>() {{add(new PlusForEachTypeAndForEachCard(12,new ArrayList<>() {{add(Type.BEAST);}}, new ArrayList<>() {{add(25);}}));}}, null, null));
    }

    @Test
    void countConditionSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(new Card(33, rb.getString("greatflood"),32, Type.FLOOD, null, new ArrayList<>() {{add(new DeletesAllTypeExceptCard(33,new ArrayList<>() {{add(Type.ARMY);add(Type.LAND);add(Type.FLAME);}},new ArrayList<>() {{add(38);add(45);}}));}}, null));
        cardsInHands.add(new Card(50, rb.getString("storm"),8, Type.WEATHER, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.FLOOD);}},10));}}, new ArrayList<>() {{add(new DeletesAllTypeExceptCard(50,new ArrayList<>() {{add(Type.FLAME);}},new ArrayList<>() {{add(38);}}));}}, null));
        Bonus bonus = cardToBeTested.bonuses.get(0);
        assertEquals(bonus.count(cardsInHands), 40);
    }

    @Test
    void countConditionNotSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(new Card(50, rb.getString("storm"),8, Type.WEATHER, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.FLOOD);}},10));}}, new ArrayList<>() {{add(new DeletesAllTypeExceptCard(50,new ArrayList<>() {{add(Type.FLAME);}},new ArrayList<>() {{add(38);}}));}}, null));
        cardsInHands.add(new Card(39, rb.getString("candle"),2, Type.FLAME, new ArrayList<>() {{add(new PlusIfYouHaveAllCardsAndAtLeastOneType(100, new ArrayList<>() {{add(31);add(42);}},new ArrayList<>() {{add(Type.WIZARD);}}));}}, null, null));
        Bonus bonus = cardToBeTested.bonuses.get(0);
        assertEquals(bonus.count(cardsInHands), 0);
    }

    @Test
    void countConditionNotSatisfied2() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(new Card(33, rb.getString("greatflood"),32, Type.FLOOD, null, new ArrayList<>() {{add(new DeletesAllTypeExceptCard(33,new ArrayList<>() {{add(Type.ARMY);add(Type.LAND);add(Type.FLAME);}},new ArrayList<>() {{add(38);add(45);}}));}}, null));
        cardsInHands.add(new Card(39, rb.getString("candle"),2, Type.FLAME, new ArrayList<>() {{add(new PlusIfYouHaveAllCardsAndAtLeastOneType(100, new ArrayList<>() {{add(31);add(42);}},new ArrayList<>() {{add(Type.WIZARD);}}));}}, null, null));
        Bonus bonus = cardToBeTested.bonuses.get(0);
        assertEquals(bonus.count(cardsInHands), 0);
    }
}