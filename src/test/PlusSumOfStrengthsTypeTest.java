package test;

import bonuses.*;
import maluses.DeletesAllTypeExceptCard;
import maluses.MinusForEachOtherSelftypeOrType;
import maluses.MinusForEachType;
import maluses.MinusIfYouDontHaveAtLeastOneType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class PlusSumOfStrengthsTypeTest {

    ArrayList<Card> cardsInHands;
    Card cardToBeTested;

    @BeforeEach
    void initHand(){
        cardsInHands = new ArrayList<Card>();
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardToBeTested = new Card(18, rb.getString("warlord"),4, Type.LEADER, new ArrayList<>() {{add(new PlusSumOfStrengthsType(Type.ARMY));}}, null, null);
        cardsInHands.add(cardToBeTested);
        cardsInHands.add(new Card(43, rb.getString("eelemental"),4, Type.LAND, new ArrayList<>() {{add(new PlusForEachSelftypeExceptThis(15,43,Type.LAND));}}, null, null));;
         }

    @Test
    void countConditionSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(new Card(22, rb.getString("knights"),20, Type.ARMY, null, new ArrayList<>() {{add(new MinusIfYouDontHaveAtLeastOneType(-8,new ArrayList<>() {{add(Type.LEADER);}}));}}, null));
        cardsInHands.add(new Card(50, rb.getString("storm"),8, Type.WEATHER, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.FLOOD);}},10));}}, new ArrayList<>() {{add(new DeletesAllTypeExceptCard(50,new ArrayList<>() {{add(Type.FLAME);}},new ArrayList<>() {{add(38);}}));}}, null));
        cardsInHands.add(new Card(44, rb.getString("cavern"),6, Type.LAND, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneCard(25,new ArrayList<>() {{add(5);add(24);}}));add(new DeleteAllMalusesOnType(Type.WEATHER));}}, null, null));
        cardsInHands.add(new Card(45, rb.getString("mountain"),9, Type.LAND, new ArrayList<>() {{add(new PlusIfYouHaveAll(50, new ArrayList<>() {{add(51);add(41);}}));add(new DeleteAllMalusesOnType(Type.FLOOD));}}, null, null));
        cardsInHands.add(new Card(46, rb.getString("forest"),7, Type.LAND, new ArrayList<>() {{add(new PlusForEachTypeAndForEachCard(12,new ArrayList<>() {{add(Type.BEAST);}}, new ArrayList<>() {{add(25);}}));}}, null, null));

        Bonus bonus = cardToBeTested.bonuses.get(0);
        assertEquals(bonus.count(cardsInHands), 20);
    }

    @Test
    void countConditionSatisfied2() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(new Card(22, rb.getString("knights"),20, Type.ARMY, null, new ArrayList<>() {{add(new MinusIfYouDontHaveAtLeastOneType(-8,new ArrayList<>() {{add(Type.LEADER);}}));}}, null));
        cardsInHands.add(new Card(50, rb.getString("storm"),8, Type.WEATHER, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.FLOOD);}},10));}}, new ArrayList<>() {{add(new DeletesAllTypeExceptCard(50,new ArrayList<>() {{add(Type.FLAME);}},new ArrayList<>() {{add(38);}}));}}, null));
        cardsInHands.add(new Card(44, rb.getString("cavern"),6, Type.LAND, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneCard(25,new ArrayList<>() {{add(5);add(24);}}));add(new DeleteAllMalusesOnType(Type.WEATHER));}}, null, null));
        cardsInHands.add(new Card(45, rb.getString("mountain"),9, Type.LAND, new ArrayList<>() {{add(new PlusIfYouHaveAll(50, new ArrayList<>() {{add(51);add(41);}}));add(new DeleteAllMalusesOnType(Type.FLOOD));}}, null, null));
        cardsInHands.add(new Card(23, rb.getString("rangers"),5, Type.ARMY, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.LAND);}},10));add(new DeleteSelftypeFromAllMaluses(Type.ARMY));}}, null, null));
        Bonus bonus = cardToBeTested.bonuses.get(0);
        assertEquals(bonus.count(cardsInHands), 25);
    }

    @Test
    void countConditionSatisfied3() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(new Card(22, rb.getString("knights"),20, Type.ARMY, null, new ArrayList<>() {{add(new MinusIfYouDontHaveAtLeastOneType(-8,new ArrayList<>() {{add(Type.LEADER);}}));}}, null));
        cardsInHands.add(new Card(50, rb.getString("storm"),8, Type.WEATHER, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.FLOOD);}},10));}}, new ArrayList<>() {{add(new DeletesAllTypeExceptCard(50,new ArrayList<>() {{add(Type.FLAME);}},new ArrayList<>() {{add(38);}}));}}, null));
        cardsInHands.add(new Card(24, rb.getString("dwarfs"),15, Type.ARMY, null, new ArrayList<>() {{add(new MinusForEachOtherSelftypeOrType(-2, null, Type.ARMY, 24));}}, null));
        cardsInHands.add(new Card(45, rb.getString("mountain"),9, Type.LAND, new ArrayList<>() {{add(new PlusIfYouHaveAll(50, new ArrayList<>() {{add(51);add(41);}}));add(new DeleteAllMalusesOnType(Type.FLOOD));}}, null, null));
        cardsInHands.add(new Card(23, rb.getString("rangers"),5, Type.ARMY, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.LAND);}},10));add(new DeleteSelftypeFromAllMaluses(Type.ARMY));}}, null, null));
        Bonus bonus = cardToBeTested.bonuses.get(0);
        assertEquals(bonus.count(cardsInHands), 40);
    }

    @Test
    void countConditionSatisfied4() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(new Card(22, rb.getString("knights"),20, Type.ARMY, null, new ArrayList<>() {{add(new MinusIfYouDontHaveAtLeastOneType(-8,new ArrayList<>() {{add(Type.LEADER);}}));}}, null));
        cardsInHands.add(new Card(50, rb.getString("storm"),8, Type.WEATHER, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.FLOOD);}},10));}}, new ArrayList<>() {{add(new DeletesAllTypeExceptCard(50,new ArrayList<>() {{add(Type.FLAME);}},new ArrayList<>() {{add(38);}}));}}, null));
        cardsInHands.add(new Card(24, rb.getString("dwarfs"),15, Type.ARMY, null, new ArrayList<>() {{add(new MinusForEachOtherSelftypeOrType(-2, null, Type.ARMY, 24));}}, null));
        cardsInHands.add(new Card(25, rb.getString("archers"),10, Type.ARMY, new ArrayList<>() {{add(new PlusIfYouDontHaveType(5,Type.WEATHER));}}, null, null));
        cardsInHands.add(new Card(23, rb.getString("rangers"),5, Type.ARMY, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.LAND);}},10));add(new DeleteSelftypeFromAllMaluses(Type.ARMY));}}, null, null));
        Bonus bonus = cardToBeTested.bonuses.get(0);
        assertEquals(bonus.count(cardsInHands), 50);
    }

    @Test
    void countConditionSatisfied5() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(new Card(22, rb.getString("knights"),20, Type.ARMY, null, new ArrayList<>() {{add(new MinusIfYouDontHaveAtLeastOneType(-8,new ArrayList<>() {{add(Type.LEADER);}}));}}, null));
        cardsInHands.add(new Card(26, rb.getString("cavalry"),17, Type.ARMY, null, new ArrayList<>() {{add(new MinusForEachType(-2,new ArrayList<>() {{add(Type.LAND);}}));}}, null));
        cardsInHands.add(new Card(24, rb.getString("dwarfs"),15, Type.ARMY, null, new ArrayList<>() {{add(new MinusForEachOtherSelftypeOrType(-2, null, Type.ARMY, 24));}}, null));
        cardsInHands.add(new Card(25, rb.getString("archers"),10, Type.ARMY, new ArrayList<>() {{add(new PlusIfYouDontHaveType(5,Type.WEATHER));}}, null, null));
        cardsInHands.add(new Card(23, rb.getString("rangers"),5, Type.ARMY, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.LAND);}},10));add(new DeleteSelftypeFromAllMaluses(Type.ARMY));}}, null, null));
        Bonus bonus = cardToBeTested.bonuses.get(0);
        assertEquals(bonus.count(cardsInHands), 67);
    }

    @Test
    void countConditionNotSatisfied() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardsInHands.add(new Card(50, rb.getString("storm"),8, Type.WEATHER, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.FLOOD);}},10));}}, new ArrayList<>() {{add(new DeletesAllTypeExceptCard(50,new ArrayList<>() {{add(Type.FLAME);}},new ArrayList<>() {{add(38);}}));}}, null));
        cardsInHands.add(new Card(44, rb.getString("cavern"),6, Type.LAND, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneCard(25,new ArrayList<>() {{add(5);add(24);}}));add(new DeleteAllMalusesOnType(Type.WEATHER));}}, null, null));
        cardsInHands.add(new Card(45, rb.getString("mountain"),9, Type.LAND, new ArrayList<>() {{add(new PlusIfYouHaveAll(50, new ArrayList<>() {{add(51);add(41);}}));add(new DeleteAllMalusesOnType(Type.FLOOD));}}, null, null));
        cardsInHands.add(new Card(46, rb.getString("forest"),7, Type.LAND, new ArrayList<>() {{add(new PlusForEachTypeAndForEachCard(12,new ArrayList<>() {{add(Type.BEAST);}}, new ArrayList<>() {{add(25);}}));}}, null, null));
        cardsInHands.add(new Card(39, rb.getString("candle"),2, Type.FLAME, new ArrayList<>() {{add(new PlusIfYouHaveAllCardsAndAtLeastOneType(100, new ArrayList<>() {{add(31);add(42);}},new ArrayList<>() {{add(Type.WIZARD);}}));}}, null, null));
        Bonus bonus = cardToBeTested.bonuses.get(0);
        assertEquals(bonus.count(cardsInHands), 0);
    }


}