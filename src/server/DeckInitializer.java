package server;

import bonuses.*;
import maluses.*;
import interactive.*;
import java.io.*;
import java.util.ArrayList;

public class DeckInitializer implements Serializable{
    void storeDecktoFile() {
        ArrayList<Card> tempDeck = new ArrayList<>();
        tempDeck.add(new Card(1, "Unicorn", 9, Type.CREATURE, new ArrayList<>() {{add(new BonusOrBonus(new PlusIfYouHaveAll(30, new ArrayList<>(){{add(17);}}), new PlusIfYouHaveAtLeastOneCard(15, new ArrayList<>(){{add(15);add(19); add(21);}})));}}, null, null));
        tempDeck.add( new Card(2,"Magic Staff", 1, Type.WEAPON, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneType(25, new ArrayList<>(){{add(Type.WIZARD);}}));}}, null, null));
        tempDeck.add(new Card(3,"Hydra",12 , Type.CREATURE, new ArrayList<>(){{add(new PlusIfYouHaveAll(28, new ArrayList<>(){{add(35);}}));}}, null, null));
        tempDeck.add(new Card(4,"Basilisk",35 , Type.CREATURE, null, new ArrayList<>() {{add(new DeletesAllTypeOrOtherSelftype(new ArrayList<>(){{add(Type.ARMY); add(Type.LEADER);}}, Type.CREATURE, 4));}}, null));
        tempDeck.add(new Card(5,"Warhorse",6 , Type.CREATURE, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneType(14, new ArrayList<>(){{add(Type.LEADER); add(Type.WIZARD);}}));}}, null, null));
        tempDeck.add(new Card(6,"Dragon", 30, Type.CREATURE, null, new ArrayList<>() {{add(new MinusIfYouDontHaveAtLeastOneType(-40, new ArrayList<>(){{add(Type.WIZARD);}}));}}, null));
        tempDeck.add(new Card(7,"Zeppelin",35 , Type.WEAPON, null, new ArrayList<>() {{add(new CardIsDeletedIfYouDontHaveAtLeastOneType(7, new ArrayList<>(){{add(Type.ARMY);}})); add(new CardIsDeletedIfYouHaveAnyType(7, new ArrayList<>(){{add(Type.WEATHER);}}));}}, null));
        tempDeck.add(new Card(8,"Warship", 23, Type.WEAPON, new ArrayList<>() {{add(new DeleteTypeFromAllMalusesOnType(Type.ARMY, Type.FLOOD));}}, new ArrayList<>() {{add(new CardIsDeletedIfYouDontHaveAtLeastOneType(8, new ArrayList<>(){{add(Type.FLOOD);}}));}}, null));
        tempDeck.add(new Card(9,"Bow", 3, Type.WEAPON, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneCard(30, new ArrayList<>() {{add(11);add(18);add(25);}}));}}, null, null));
        tempDeck.add(new Card(10,"Keth Sword",7, Type.WEAPON, new ArrayList<>() {{add(new BonusOrBonus(new PlusIfYouHaveAtLeastOneType(10, new ArrayList<>() {{add(Type.LEADER);}}), new PlusIfYouHaveAllCardsAndAtLeastOneType(40, new ArrayList<>(){{add(27);}}, new ArrayList<>() {{add(Type.LEADER);}})));}}, null, null));
        tempDeck.add(new Card(11,"Lord of Beasts",9, Type.WIZARD, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.CREATURE);}},9) );add(new DeleteAllMalusesOnType(Type.CREATURE));}}, null, null));
        tempDeck.add(new Card(12,"Collector",7, Type.WIZARD, new ArrayList<>() {{add(new PlusForSameColorCards());}}, null, null));
        tempDeck.add(new Card(13,"Necromant",3, Type.WIZARD, null, null, new ArrayList<>() {{add(new TakeCardOfTypeAtTheEnd(13,new ArrayList<>() {{add(Type.ARMY); add(Type.LEADER);  add(Type.WIZARD); add(Type.CREATURE);}}));}}));
        tempDeck.add( new Card(14,"Jester",3, Type.WIZARD, new ArrayList<>() {{add(new BonusOrBonus(new PlusForEachOdd(3, true,14), new PlusIfAllAreOdd(50, true)));}}, null, null));
        tempDeck.add(new Card(15,"Witch",5, Type.WIZARD, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.EARTH); add(Type.WEATHER); add(Type.FLOOD); add(Type.FIRE);}},5));}}, null, null));
        tempDeck.add(new Card(16,"Archmage",25, Type.WIZARD, null, new ArrayList<>() {{add(new MinusForEachOtherSelftypeOrType(-10,new ArrayList<>() {{add(Type.LEADER);}}, Type.WIZARD, 17));}}, null));
        tempDeck.add(new Card(17,"Princess",2, Type.LEADER, new ArrayList<>() {{add(new PlusForEachTypeOrSelfType(8, 17, new ArrayList<>(){{add(Type.ARMY);add(Type.WIZARD);}}, Type.LEADER));}}, null, null));
        tempDeck.add(new Card(18,"Commander",4, Type.LEADER, new ArrayList<>() {{add(new PlusSumOfStrengthsType(Type.ARMY));}}, null, null));
        tempDeck.add(new Card(19,"Queen",6, Type.LEADER, new ArrayList<>() {{add(new BonusOrBonus(new PlusForEachType(new ArrayList<>() {{add(Type.ARMY);}},5), new PlusForEachTypeIfYouHaveCard(20,new ArrayList<>() {{add(Type.ARMY);}},20)));}}, null, null));
        tempDeck.add(new Card(20,"King",8, Type.LEADER, new ArrayList<>() {{add(new BonusOrBonus(new PlusForEachType(new ArrayList<>() {{add(Type.ARMY);}},5), new PlusForEachTypeIfYouHaveCard(20,new ArrayList<>() {{add(Type.ARMY);}},19)));}}, null, null));
        tempDeck.add(new Card(21,"Empress",15, Type.LEADER, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.ARMY);}},10));}}, new ArrayList<>() {{add(new MinusForEachOtherSelftypeOrType(-5, null, Type.LEADER, 21));}}, null));
        tempDeck.add(new Card(22,"Swordswomen",20, Type.ARMY, null, new ArrayList<>() {{add(new MinusIfYouDontHaveAtLeastOneType(-8,new ArrayList<>() {{add(Type.LEADER);}}));}}, null));
        tempDeck.add(new Card(23,"Striders",5, Type.ARMY, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.EARTH);}},10));add(new DeleteSelftypeFromAllMaluses(Type.ARMY));}}, null, null));
        tempDeck.add(new Card(24,"Dwarf Infantry",15, Type.ARMY, null, new ArrayList<>() {{add(new MinusForEachOtherSelftypeOrType(-2, null, Type.ARMY, 24));}}, null));
        tempDeck.add(new Card(25,"Elven Bowmen",10, Type.ARMY, new ArrayList<>() {{add(new PlusIfYouDontHaveType(5,Type.WEATHER));}}, null, null));
        tempDeck.add(new Card(26,"Cavalry",17, Type.ARMY, null, new ArrayList<>() {{add(new MinusForEachType(-2,new ArrayList<>() {{add(Type.EARTH);}}));}}, null));
        tempDeck.add(new Card(27,"Keth Shield",4, Type.ARTIFACT, new ArrayList<>() {{add(new BonusOrBonus(new PlusIfYouHaveAtLeastOneType(15,new ArrayList<>() {{add(Type.LEADER);}}), new PlusIfYouHaveAllCardsAndAtLeastOneType(40, new ArrayList<>() {{add(10);}}, new ArrayList<>() {{add(Type.LEADER);}})));}}, null, null));
        tempDeck.add(new Card(28,"Guard Rune",1, Type.ARTIFACT, new ArrayList<>() {{add(new DeleteAllMaluses());}}  , null, null));
        tempDeck.add(new Card(29,"Crystal of Order",5, Type.ARTIFACT, new ArrayList<>() {{add(new PlusForStrengthsInRow());}}, null, null));
        tempDeck.add(new Card(30,"World Tree",2, Type.ARTIFACT, new ArrayList<>() {{add(new PlusIfTypesAreUnique(50));}}, null, null));
        tempDeck.add(new Card(31,"Book of Spells",3, Type.ARTIFACT, null, null, new ArrayList<>(){{add(new ChangeColor(31));}}));
        tempDeck.add(new Card(32,"Fountain of Life",1, Type.FLOOD, new ArrayList<>() {{add(new PlusStrengthOfAnyCardOfType(new ArrayList<>(){{add(Type.WEAPON); add(Type.FLOOD); add(Type.FIRE); add(Type.EARTH); add(Type.WEATHER);}}));}}, null, null));
        tempDeck.add(new Card(33,"Great Flood",32, Type.FLOOD, null, new ArrayList<>() {{add(new DeletesAllTypeExceptCard(33,new ArrayList<>() {{add(Type.ARMY);add(Type.EARTH);add(Type.FIRE);}},new ArrayList<>() {{add(38);add(45);}}));}}, null));
        tempDeck.add(new Card(34,"Elemental of Water",4, Type.FLOOD, new ArrayList<>() {{add(new PlusForEachSelftypeExceptThis(15,34,Type.FLOOD));}}, null, null));
        tempDeck.add(new Card(35,"Swamp",18, Type.FLOOD, null, new ArrayList<>() {{add(new MinusForEachType(-3,new ArrayList<>() {{add(Type.ARMY);add(Type.FIRE);}}));}}, null));
        tempDeck.add(new Card(36,"Island",14, Type.FLOOD, null, null, new ArrayList<>() {{add(new DeleteOneMalusOnType(36,new ArrayList<>() {{add(Type.FLOOD);add(Type.FIRE);}}));}}));
        tempDeck.add(new Card(37,"Elemental of Fire",4, Type.FIRE, new ArrayList<>() {{add(new PlusForEachSelftypeExceptThis(15,37,Type.FIRE));}}, null, null));
        tempDeck.add(new Card(38,"Lightning",11, Type.FIRE, new ArrayList<>() {{add(new PlusIfYouHaveAll(30,new ArrayList<>() {{add(50);}}));}}, null, null));
        tempDeck.add(new Card(39,"Candle",2, Type.FIRE, new ArrayList<>() {{add(new PlusIfYouHaveAllCardsAndAtLeastOneType(100, new ArrayList<>() {{add(31);add(42);}},new ArrayList<>() {{add(Type.WIZARD);}}));}}, null, null));
        tempDeck.add(new Card(40,"Forge",9, Type.FIRE, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.WEAPON);add(Type.ARTIFACT);}},9));}}, null, null));
        tempDeck.add(new Card(41,"Conflagration",40, Type.FIRE, null, new ArrayList<>() {{add(new DeletesAllExceptTypeOrCard(new ArrayList<>() {{add(Type.FIRE);add(Type.WIZARD);add(Type.WEATHER);add(Type.WEAPON);add(Type.ARTIFACT);}},new ArrayList<>() {{add(45);add(33);add(1);add(36);add(6);}},41));}}, null));
        tempDeck.add(new Card(42,"Belfry",8, Type.EARTH, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneType(15, new ArrayList<>(){{add(Type.WIZARD);}}));}}, null, null));
        tempDeck.add(new Card(43,"Elemental of Earth",4, Type.EARTH, new ArrayList<>() {{add(new PlusForEachSelftypeExceptThis(15,43,Type.EARTH));}}, null, null));
        tempDeck.add(new Card(44,"Cave",6, Type.EARTH, new ArrayList<>() {{add(new PlusIfYouHaveAtLeastOneCard(25,new ArrayList<>() {{add(6);add(24);}}));add(new DeleteAllMalusesOnType(Type.WEATHER));}}, null, null));
        tempDeck.add(new Card(45,"Mountain",9, Type.EARTH, new ArrayList<>() {{add(new PlusIfYouHaveAll(50, new ArrayList<>() {{add(51);add(41);}}));add(new DeleteAllMalusesOnType(Type.FLOOD));}}, null, null));
        tempDeck.add(new Card(46,"Forest",7, Type.EARTH, new ArrayList<>() {{add(new PlusForEachTypeAndForEachCard(12,new ArrayList<>() {{add(Type.CREATURE);}}, new ArrayList<>() {{add(25);}}));}}, null, null));
        tempDeck.add(new Card(47,"Blizzard",30, Type.WEATHER, null, new ArrayList<>() {{add(new DeletesAllType(47,new ArrayList<>() {{add(Type.FLOOD);}}));add( new MinusForEachType(-5,new ArrayList<>() {{add(Type.ARMY);add(Type.LEADER);add(Type.CREATURE); add(Type.FIRE);}}));}}, null));
        tempDeck.add(new Card(48,"Elemental of Air",4, Type.WEATHER, new ArrayList<>() {{add(new PlusForEachSelftypeExceptThis(15,48,Type.WEATHER));}}, null, null));
        tempDeck.add(new Card(49,"Tornado",13, Type.WEATHER, new ArrayList<>() {{add(new PlusIfYouHaveCardAndAtLeastOneFrom(40, new ArrayList<>() {{add(47);add(33);}}, 50));}}, null, null));
        tempDeck.add(new Card(50,"Storm",8, Type.WEATHER, new ArrayList<>() {{add(new PlusForEachType(new ArrayList<>() {{add(Type.FLOOD);}},10));}}, new ArrayList<>() {{add(new DeletesAllTypeExceptCard(50,new ArrayList<>() {{add(Type.FIRE);}},new ArrayList<>() {{add(38);}}));}}, null));
        tempDeck.add(new Card(51,"Smoke",27, Type.WEATHER, null, new ArrayList<>() {{add(new CardIsDeletedIfYouDontHaveAtLeastOneType(51,new ArrayList<>(){{add(Type.FIRE);}}));}}, null));
        tempDeck.add(new Card(52,"Skinchanger",0, Type.WILD, null, null, new ArrayList<>() {{add(new CopyNameAndType(52,new ArrayList<>() {{add(Type.ARTIFACT); add(Type.LEADER); add(Type.WIZARD); add(Type.WEAPON); add(Type.CREATURE);}}));}}));
        tempDeck.add(new Card(53,"Doppleganger",0, Type.WILD, null, null, new ArrayList<>() {{add(new CopyNameColorStrengthMalusFromHand(53));}}));
        tempDeck.add(new Card(54,"Mirage",0, Type.WILD, null, null, new ArrayList<>() {{add(new CopyNameAndType(54,new ArrayList<>() {{add(Type.WEATHER); add(Type.ARMY); add(Type.EARTH); add(Type.FLOOD); add(Type.FIRE);}}));}}));


        try {
            FileOutputStream f = new FileOutputStream(new File("DefaultGameDeckCardsObjects.txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            for(Card c: tempDeck){
                o.writeObject(c);
            }

            o.close();
            f.close();



        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error initializing stream");
        }
    }

    public static ArrayList<Card> loadDeckFromFile(){
        ArrayList<Card> deck = new ArrayList<>();

        try{
            FileInputStream fi = new FileInputStream(new File("DefaultGameDeckCardsObjects.txt"));
            ObjectInputStream oi = new ObjectInputStream(fi);

            // Read objects
            for(int i = 0; i < 54; i++){
                deck.add((Card) oi.readObject());
            }

            oi.close();
            fi.close();
        } catch (IOException e) {
            System.out.println("Error initializing stream while loading");
        }
        catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return deck;
    }
}
