package test;

import bonuses.*;
import interactive.*;
import maluses.*;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.ResourceBundle;

public final class OriginalDeck {
    public static ArrayList<Card> deck = new ArrayList<Card>() {
        {   ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
            add(new Card(1, rb.getString("unicorn"), 9, Type.BEAST, new ArrayList<>() {{add(new BonusOrBonus(new PlusIfYouHaveAll(30, new ArrayList<>(){{add(17);}}), new PlusIfYouHaveAtLeastOneCard(15, new ArrayList<>(){{add(15);add(19); add(21);}})));}}, null, null));
            add(new Card(2, rb.getString("hydra"),12 , Type.BEAST, new ArrayList<>(){{add(new PlusIfYouHaveAll(28, new ArrayList<>(){{add(35);}}));}}, null, null));
            add(new Card(3, rb.getString("basilisk"),35 , Type.BEAST, null, new ArrayList<>() {{add(new DeletesAllTypeOrOtherSelftype(new ArrayList<>(){{add(Type.ARMY); add(Type.LEADER);}}, Type.BEAST, 3));}}, null));
            add(new Card(4, rb.getString("warhorse"), 6 , Type.BEAST, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneType(14, new ArrayList<>(){{add(Type.LEADER); add(Type.WIZARD);}}));}}, null, null));
            add(new Card(5, rb.getString("dragon"), 30, Type.BEAST, null, new ArrayList<>() {{add(new MinusIfYouDontHaveAtLeastOneType(-40, new ArrayList<>(){{add(Type.WIZARD);}}));}}, null));
            add(new Card(6, rb.getString("wand"), 1, Type.WEAPON, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneType(25, new ArrayList<>(){{add(Type.WIZARD);}}));}}, null, null));
            add(new Card(7, rb.getString("dirigible"),35 , Type.WEAPON, null, new ArrayList<>() {{add(new CardIsDeletedIfYouDontHaveAtLeastOneType(7, new ArrayList<>(){{add(Type.ARMY);}})); add(new CardIsDeletedIfYouHaveAnyType(7, new ArrayList<>(){{add(Type.WEATHER);}}));}}, null));
            add(new Card(8, rb.getString("warship"), 23, Type.WEAPON, new ArrayList<>() {{add(new DeleteTypeFromAllMalusesOnType(Type.ARMY, Type.FLOOD));}}, new ArrayList<>() {{add(new CardIsDeletedIfYouDontHaveAtLeastOneType(8, new ArrayList<>(){{add(Type.FLOOD);}}));}}, null));
            add(new Card(9, rb.getString("bow"), 3, Type.WEAPON, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneCard(30, new ArrayList<>() {{add(11);add(18);add(25);}}));}}, null, null));
            add(new Card(10, rb.getString("sword"),7, Type.WEAPON, new ArrayList<>() {{add(new BonusOrBonus(new PlusIfYouHaveAtLeastOneType(10, new ArrayList<>() {{add(Type.LEADER);}}), new PlusIfYouHaveAllCardsAndAtLeastOneType(40, new ArrayList<>(){{add(27);}}, new ArrayList<>() {{add(Type.LEADER);}})));}}, null, null));
            add(new Card(11, rb.getString("beastmaster"),9, Type.WIZARD, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.BEAST);}},9) );add(new DeleteAllMalusesOnType(Type.BEAST));}}, null, null));
            add(new Card(12, rb.getString("collector"),7, Type.WIZARD, new ArrayList<>() {{add(new PlusForSameColorCards());}}, null, null));
            add(new Card(13, rb.getString("necromancer"),3, Type.WIZARD, null, null, new ArrayList<>() {{add(new TakeCardOfTypeAtTheEnd(13,new ArrayList<>() {{add(Type.ARMY); add(Type.LEADER);  add(Type.WIZARD); add(Type.BEAST);}}));}}));
            add(new Card(14, rb.getString("jester"),3, Type.WIZARD, new ArrayList<>() {{add(new BonusOrBonus(new PlusForEachOdd(3, true,14), new PlusIfAllAreOdd(50, true)));}}, null, null));
            add(new Card(15, rb.getString("enchantress"),5, Type.WIZARD, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.LAND); add(Type.WEATHER); add(Type.FLOOD); add(Type.FLAME);}},5));}}, null, null));
            add(new Card(16, rb.getString("warlock"),25, Type.WIZARD, null, new ArrayList<>() {{add(new MinusForEachOtherSelftypeOrType(-10,new ArrayList<>() {{add(Type.LEADER);}}, Type.WIZARD, 16));}}, null));
            add(new Card(17, rb.getString("princess"),2, Type.LEADER, new ArrayList<>() {{add(new PlusForEachTypeOrSelfType(8, 17, new ArrayList<>(){{add(Type.ARMY);add(Type.WIZARD);}}, Type.LEADER));}}, null, null));
            add(new Card(18, rb.getString("warlord"),4, Type.LEADER, new ArrayList<>() {{add(new PlusSumOfStrengthsType(Type.ARMY));}}, null, null));
            add(new Card(19, rb.getString("queen"),6, Type.LEADER, new ArrayList<>() {{add(new BonusOrBonus(new PlusForEachType(new ArrayList<>() {{add(Type.ARMY);}},5), new PlusForEachTypeIfYouHaveCard(20,new ArrayList<>() {{add(Type.ARMY);}},20)));}}, null, null));
            add(new Card(20, rb.getString("king"),8, Type.LEADER, new ArrayList<>() {{add(new BonusOrBonus(new PlusForEachType(new ArrayList<>() {{add(Type.ARMY);}},5), new PlusForEachTypeIfYouHaveCard(20,new ArrayList<>() {{add(Type.ARMY);}},19)));}}, null, null));
            add(new Card(21, rb.getString("empress"),15, Type.LEADER, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.ARMY);}},10));}}, new ArrayList<>() {{add(new MinusForEachOtherSelftypeOrType(-5, null, Type.LEADER, 21));}}, null));
            add(new Card(22, rb.getString("knights"),20, Type.ARMY, null, new ArrayList<>() {{add(new MinusIfYouDontHaveAtLeastOneType(-8,new ArrayList<>() {{add(Type.LEADER);}}));}}, null));
            add(new Card(23, rb.getString("rangers"),5, Type.ARMY, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.LAND);}},10));add(new DeleteSelftypeFromAllMaluses(Type.ARMY));}}, null, null));
            add(new Card(24, rb.getString("dwarfs"),15, Type.ARMY, null, new ArrayList<>() {{add(new MinusForEachOtherSelftypeOrType(-2, null, Type.ARMY, 24));}}, null));
            add(new Card(25, rb.getString("archers"),10, Type.ARMY, new ArrayList<>() {{add(new PlusIfYouDontHaveType(5,Type.WEATHER));}}, null, null));
            add(new Card(26, rb.getString("cavalry"),17, Type.ARMY, null, new ArrayList<>() {{add(new MinusForEachType(-2,new ArrayList<>() {{add(Type.LAND);}}));}}, null));
            add(new Card(27, rb.getString("shield"),4, Type.ARTIFACT, new ArrayList<>() {{add(new BonusOrBonus(new PlusIfYouHaveAtLeastOneType(15,new ArrayList<>() {{add(Type.LEADER);}}), new PlusIfYouHaveAllCardsAndAtLeastOneType(40, new ArrayList<>() {{add(10);}}, new ArrayList<>() {{add(Type.LEADER);}})));}}, null, null));
            add(new Card(28, rb.getString("rune"),1, Type.ARTIFACT, new ArrayList<>() {{add(new DeleteAllMaluses());}}  , null, null));
            add(new Card(29, rb.getString("gem"),5, Type.ARTIFACT, new ArrayList<>() {{add(new PlusForStrengthsInRow());}}, null, null));
            add(new Card(30, rb.getString("tree"),2, Type.ARTIFACT, new ArrayList<>() {{add(new PlusIfTypesAreUnique(50));}}, null, null));
            add(new Card(31, rb.getString("book"),3, Type.ARTIFACT, null, null, new ArrayList<>(){{add(new ChangeColor(31));}}));
            add(new Card(32, rb.getString("fountain"),1, Type.FLOOD, new ArrayList<>() {{add(new PlusStrengthOfAnyCardOfType(new ArrayList<>(){{add(Type.WEAPON); add(Type.FLOOD); add(Type.FLAME); add(Type.LAND); add(Type.WEATHER);}}));}}, null, null));
            add(new Card(33, rb.getString("greatflood"),32, Type.FLOOD, null, new ArrayList<>() {{add(new DeletesAllTypeExceptCard(33,new ArrayList<>() {{add(Type.ARMY);add(Type.LAND);add(Type.FLAME);}},new ArrayList<>() {{add(38);add(45);}}));}}, null));
            add(new Card(34, rb.getString("welemental"),4, Type.FLOOD, new ArrayList<>() {{add(new PlusForEachSelftypeExceptThis(15,34,Type.FLOOD));}}, null, null));
            add(new Card(35, rb.getString("swamp"),18, Type.FLOOD, null, new ArrayList<>() {{add(new MinusForEachType(-3,new ArrayList<>() {{add(Type.ARMY);add(Type.FLAME);}}));}}, null));
            add(new Card(36, rb.getString("island"),14, Type.FLOOD, null, null, new ArrayList<>() {{add(new DeleteOneMalusOnType(36,new ArrayList<>() {{add(Type.FLOOD);add(Type.FLAME);}}));}}));
            add(new Card(37, rb.getString("felemental"),4, Type.FLAME, new ArrayList<>() {{add(new PlusForEachSelftypeExceptThis(15,37,Type.FLAME));}}, null, null));
            add(new Card(38, rb.getString("lightning"),11, Type.FLAME, new ArrayList<>() {{add(new PlusIfYouHaveAll(30,new ArrayList<>() {{add(50);}}));}}, null, null));
            add(new Card(39, rb.getString("candle"),2, Type.FLAME, new ArrayList<>() {{add(new PlusIfYouHaveAllCardsAndAtLeastOneType(100, new ArrayList<>() {{add(31);add(42);}},new ArrayList<>() {{add(Type.WIZARD);}}));}}, null, null));
            add(new Card(40, rb.getString("forge"),9, Type.FLAME, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.WEAPON);add(Type.ARTIFACT);}},9));}}, null, null));
            add(new Card(41, rb.getString("wildfire"),40, Type.FLAME, null, new ArrayList<>() {{add(new DeletesAllExceptTypeOrCard(new ArrayList<>() {{add(Type.FLAME);add(Type.WIZARD);add(Type.WEATHER);add(Type.WEAPON);add(Type.ARTIFACT);}},new ArrayList<>() {{add(45);add(33);add(1);add(36);add(5);}},41));}}, null));
            add(new Card(42, rb.getString("belfry"),8, Type.LAND, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneType(15, new ArrayList<>(){{add(Type.WIZARD);}}));}}, null, null));
            add(new Card(43, rb.getString("eelemental"),4, Type.LAND, new ArrayList<>() {{add(new PlusForEachSelftypeExceptThis(15,43,Type.LAND));}}, null, null));
            add(new Card(44, rb.getString("cavern"),6, Type.LAND, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneCard(25,new ArrayList<>() {{add(5);add(24);}}));add(new DeleteAllMalusesOnType(Type.WEATHER));}}, null, null));
            add(new Card(45, rb.getString("mountain"),9, Type.LAND, new ArrayList<>() {{add(new PlusIfYouHaveAll(50, new ArrayList<>() {{add(51);add(41);}}));add(new DeleteAllMalusesOnType(Type.FLOOD));}}, null, null));
            add(new Card(46, rb.getString("forest"),7, Type.LAND, new ArrayList<>() {{add(new PlusForEachTypeAndForEachCard(12,new ArrayList<>() {{add(Type.BEAST);}}, new ArrayList<>() {{add(25);}}));}}, null, null));
            add(new Card(47, rb.getString("blizzard"),30, Type.WEATHER, null, new ArrayList<>() {{add(new DeletesAllType(47,new ArrayList<>() {{add(Type.FLOOD);}}));add( new MinusForEachType(-5,new ArrayList<>() {{add(Type.ARMY);add(Type.LEADER);add(Type.BEAST); add(Type.FLAME);}}));}}, null));
            add(new Card(48, rb.getString("aelemental"),4, Type.WEATHER, new ArrayList<>() {{add(new PlusForEachSelftypeExceptThis(15,48,Type.WEATHER));}}, null, null));
            add(new Card(49, rb.getString("tornado"),13, Type.WEATHER, new ArrayList<>() {{add(new PlusIfYouHaveCardAndAtLeastOneFrom(40, new ArrayList<>() {{add(47);add(33);}}, 50));}}, null, null));
            add(new Card(50, rb.getString("storm"),8, Type.WEATHER, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.FLOOD);}},10));}}, new ArrayList<>() {{add(new DeletesAllTypeExceptCard(50,new ArrayList<>() {{add(Type.FLAME);}},new ArrayList<>() {{add(38);}}));}}, null));
            add(new Card(51, rb.getString("smoke"),27, Type.WEATHER, null, new ArrayList<>() {{add(new CardIsDeletedIfYouDontHaveAtLeastOneType(51,new ArrayList<>(){{add(Type.FLAME);}}));}}, null));
            add(new Card(52, rb.getString("shapeshifter"),0, Type.WILD, null, null, new ArrayList<>() {{add(new CopyNameAndType(52,new ArrayList<>() {{add(Type.ARTIFACT); add(Type.LEADER); add(Type.WIZARD); add(Type.WEAPON); add(Type.BEAST);}}));}}));
            add(new Card(53, rb.getString("mirage"),0, Type.WILD, null, null, new ArrayList<>() {{add(new CopyNameAndType(53,new ArrayList<>() {{add(Type.WEATHER); add(Type.ARMY); add(Type.LAND); add(Type.FLOOD); add(Type.FLAME);}}));}}));
            add(new Card(54, rb.getString("doppleganger"),0, Type.WILD, null, null, new ArrayList<>() {{add(new CopyCardFromHand(54));}}));

        }
    };
    private OriginalDeck () { // private constructor

    }

    public static ArrayList<Card> getDeck(){
        return deck;
    }
}
