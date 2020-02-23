package server;

import bonuses.*;
import interactive.Interactive;
import maluses.*;
import interactive.*;



import java.util.ArrayList;
import java.util.Random;

import static server.Type.*;


public class DeckGenerator {
private final int RANDOM_SIZE = 20;
private final int INTERACTIVES_SIZE = 5;
private final int BONUSES_SIZE = 24;
private final int MALUSES_SIZE = 9;
    public ArrayList<Card> generateDeck(){
        ArrayList<Card> deck = new ArrayList<>();
        for(int i = 1; i < 52;i++){
            Random randomGenerator = new Random();
                int seed = randomGenerator.nextInt(RANDOM_SIZE);
            ArrayList<Bonus> bonuses = generateBonuses(seed, i, false);
            ArrayList<Malus> maluses = generateMaluses(seed, i);
            ArrayList<Interactive> interactives = generateInteractives(seed, i);
            Card newCard = new Card(i,BigSwitches.switchIdForName(i), getStrengthByBonuses(), BigSwitches.switchNameForType(BigSwitches.switchIdForName(i)), bonuses, maluses, interactives );
            deck.add(newCard);
        }

        return deck;
    }

    private int getStrengthByBonuses(){
        int strength = 0;


        return strength;
    }

    private ArrayList<Bonus> generateBonuses(int seed, int thisCardID, boolean notBonusOrBonus){
        ArrayList<Bonus> bonuses;
        if(((seed >= 0) && (seed < 7)) || ((seed >= 15) && (seed <19))) {
            bonuses = new ArrayList<>();
            int howManyBonuses;
            Random randomGenerator = new Random();
            if (seed == 18) {
                howManyBonuses = 2;
            } else {
                howManyBonuses = 1;
            }
            for (int i = 0; i < howManyBonuses; i++) {
                int index = randomGenerator.nextInt(MALUSES_SIZE);
                int mustHave;
                int mustHaveID;
                int howMuch;
                boolean odd;
                switch (index) {
                    case 0:
                        // even if we want to generate two bonuses, this one must be alone
                        if(i == 0){
                            bonuses.add(new BonusOrBonus(generateBonuses(1, thisCardID, true).get(0), generateBonuses(1, thisCardID, true).get(0)));
                        }
                        break;
                    case 1: bonuses.add(new DeleteAllMaluses());
                        break;
                    case 2: bonuses.add(new DeleteAllMalusesOnType(generateTypes(1, null).get(0)));
                        break;
                    case 3: bonuses.add(new DeleteSelftypeFromAllMaluses(BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID))));
                        break;
                    case 4: bonuses.add(new DeleteTypeFromAllMalusesOnType(generateTypes(1, null).get(0), generateTypes(1, null).get(0)));
                        break;
                    case 5:
                        howMuch = randomGenerator.nextInt(20);
                        mustHave = randomGenerator.nextInt(5);
                        bonuses.add(new PlusForEachCard(howMuch, generateCardIDs(mustHave + 1,thisCardID, null)));
                        break;
                    case 6:
                        howMuch = randomGenerator.nextInt(5);
                        mustHave = randomGenerator.nextInt(2);
                        if(mustHave == 0){
                            odd = false;
                        }
                        else{
                            odd = true;
                        }
                        bonuses.add(new PlusForEachOdd(howMuch, odd, thisCardID));
                        break;
                    case 7:
                        howMuch = randomGenerator.nextInt(10);
                        bonuses.add(new PlusForEachSelftypeExceptThis(10 + howMuch,thisCardID, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID)) ));
                        break;
                    case 8:
                        howMuch = randomGenerator.nextInt(8);
                        mustHave = randomGenerator.nextInt(4);
                        bonuses.add(new PlusForEachType(generateTypes(mustHave + 1, null), 5+ howMuch));
                        break;
                    case 9:
                        howMuch = randomGenerator.nextInt(8);
                        mustHave = randomGenerator.nextInt(3);
                        mustHaveID = randomGenerator.nextInt(2);
                        ArrayList<Type> types = generateTypes(mustHave, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID)));
                        bonuses.add(new PlusForEachTypeAndForEachCard(6 + howMuch, types , generateCardIDs(mustHaveID + 1,thisCardID, complementToAllTypes(types))));
                        break;
                    case 10:
                        howMuch = randomGenerator.nextInt(8);
                        mustHave = randomGenerator.nextInt(3);
                        bonuses.add(new PlusForEachTypeIfYouHaveCard(18 + howMuch, generateTypes(mustHave, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID))), generateCardIDs(1,thisCardID,null).get(0)));
                        break;
                    case 11:
                        howMuch = randomGenerator.nextInt(4);
                        mustHave = randomGenerator.nextInt(3);
                        bonuses.add(new PlusForEachTypeOrSelfType(5+howMuch, thisCardID, generateTypes(mustHave+1, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID))) , BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID))));
                        break;
                    case 12:
                        bonuses.add(new PlusForSameColorCards());
                        break;
                    case 13:
                        bonuses.add(new PlusForStrengthsInRow());
                        break;
                    case 14:
                        howMuch = randomGenerator.nextInt(10);
                        mustHave = randomGenerator.nextInt(2);

                        if(mustHave == 0){
                            odd = false;
                        }
                        else{
                            odd = true;
                        }
                        bonuses.add(new PlusIfAllAreOdd(howMuch + 45, odd));
                        break;
                    case 15:
                        howMuch = randomGenerator.nextInt(10);
                        bonuses.add(new PlusIfTypesAreUnique(45 + howMuch));
                        break;
                    case 16:
                        howMuch = randomGenerator.nextInt(10);
                        bonuses.add(new PlusIfYouDontHaveType(8+howMuch, generateTypes(1, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID))).get(0)));
                        break;
                    case 17:
                        howMuch = randomGenerator.nextInt(10);
                        mustHave = randomGenerator.nextInt(2);
                        bonuses.add(new PlusIfYouHaveAll(25 + howMuch, generateCardIDs(mustHave, thisCardID, null)));
                        break;
                    case 18:
                        howMuch = randomGenerator.nextInt(40);
                        mustHave = randomGenerator.nextInt(3);
                        mustHaveID = randomGenerator.nextInt(2);
                        bonuses.add(new PlusIfYouHaveAllCardsAndAtLeastOneType(80 + howMuch, generateCardIDs(mustHave + 1, thisCardID, null), generateTypes(mustHaveID+1, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID)) )));
                        break;
                    case 19:
                        howMuch = randomGenerator.nextInt(4);
                        mustHave = randomGenerator.nextInt(3);
                        bonuses.add(new PlusIfYouHaveAtLeastOneCard(25+howMuch, generateCardIDs(mustHave + 1, thisCardID, null) ));
                        break;
                    case 20:
                        howMuch = randomGenerator.nextInt(4);
                        mustHave = randomGenerator.nextInt(3);
                        bonuses.add(new PlusIfYouHaveAtLeastOneType(18+howMuch, generateTypes(mustHave + 1, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID)))));
                        break;
                    case 21:
                        howMuch = randomGenerator.nextInt(8);
                        mustHave = randomGenerator.nextInt(3);
                        ArrayList<Integer> ids = generateCardIDs(mustHave+1, thisCardID, null);
                        bonuses.add(new PlusIfYouHaveCardAndAtLeastOneFrom( 25 + howMuch,ids, generateCardIDs(1,thisCardID,null).get(0)));
                        break;
                    case 22:
                        mustHave = randomGenerator.nextInt(3);
                        bonuses.add(new PlusStrengthOfAnyCardOfType(generateTypes(mustHave + 1, null)));
                        break;
                    case 23: bonuses.add(new PlusSumOfStrengthsType(generateTypes(1,  null).get(0)));
                        break;
                }
            }
        }
        else{
            bonuses = null;
        }
        return bonuses;
    }

    private ArrayList<Malus> generateMaluses(int seed, int thisCardID){
        ArrayList<Malus> maluses;
        int howManyMaluses;
        if(((seed >= 7) && (seed < 12)) || ((seed >= 15) && (seed < 18)) || (seed == 19)){
            maluses = new ArrayList<>();
            if(seed == 19){
                howManyMaluses = 2;
            } else {
                howManyMaluses = 1;
            }
            Random randomGenerator = new Random();
            for(int i = 0; i < howManyMaluses; i++){
                int index = randomGenerator.nextInt(MALUSES_SIZE);
                int mustHave;
                int mustHaveID;
                switch(index){
                    case 0:
                        mustHave = randomGenerator.nextInt(2);
                        maluses.add(new CardIsDeletedIfYouDontHaveAtLeastOneType(thisCardID, generateTypes(mustHave+1, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID)))));
                        break;
                    case 1:
                        mustHave = randomGenerator.nextInt(2);
                        maluses.add(new CardIsDeletedIfYouHaveAnyType(thisCardID, generateTypes(mustHave+1, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID)))));
                        break;
                    case 2:
                        mustHave = randomGenerator.nextInt(4);
                        mustHaveID = randomGenerator.nextInt(4);
                        maluses.add(new DeletesAllExceptTypeOrCard(generateTypes(mustHave + 1,null), generateCardIDs( mustHaveID + 1, thisCardID, null),thisCardID));
                        break;
                    case 3:
                        mustHave = randomGenerator.nextInt(4);
                        maluses.add(new DeletesAllType(thisCardID, generateTypes(mustHave + 1, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID)) )));
                        break;
                    case 4:
                        mustHave = randomGenerator.nextInt(3);
                        mustHaveID = randomGenerator.nextInt(2);
                        ArrayList<Type> types = generateTypes(mustHave + 1, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID)));
                        maluses.add(new DeletesAllTypeExceptCard(thisCardID, types , generateCardIDs(mustHaveID + 1, thisCardID, types)));
                        break;
                    case 5:
                        Type thisCardType = BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID));
                        mustHave = randomGenerator.nextInt(3);
                        maluses.add(new DeletesAllTypeOrOtherSelftype(generateTypes(mustHave + 1, thisCardType), thisCardType, thisCardID));
                        break;
                    case 6:
                        mustHave = randomGenerator.nextInt(3);
                        mustHaveID = randomGenerator.nextInt(10);
                        maluses.add(new MinusForEachOtherSelftypeOrType( - mustHaveID - 1 , generateTypes(mustHave, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID))) ,BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID)), thisCardID));
                        break;
                    case 7:
                        mustHave = randomGenerator.nextInt(3);
                        mustHaveID = randomGenerator.nextInt(10);
                        maluses.add(new MinusForEachType(- mustHaveID - 1, generateTypes(mustHave, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID)))));
                        break;
                    case 8:
                        mustHave = randomGenerator.nextInt(10);
                        maluses.add(new MinusIfYouDontHaveAtLeastOneType(mustHave - 50, generateTypes(1, BigSwitches.switchNameForType(BigSwitches.switchIdForName(thisCardID)))));
                        break;
                }
            }
        }
        else{
            maluses = null;
        }
        return maluses;
    }

    private ArrayList<Type> complementToAllTypes(ArrayList<Type> iDontWantThose){
        ArrayList<Type> all = new ArrayList<>(){{add(ARMY); add(ARTIFACT); add(CREATURE); add(EARTH); add(FIRE); add(FLOOD); add(LEADER); add(WEAPON); add(WEATHER); add(WIZARD); add(WILD);}};
        all.removeIf(type -> iDontWantThose.contains(type));
        return all;
    }

    private ArrayList<Interactive> generateInteractives(int seed, int thisCardID){
        ArrayList<Interactive> interactives;
        if(((seed >= 12) && (seed < 15))){
            interactives = new ArrayList<>();

            Random randomGenerator = new Random();
            int i = randomGenerator.nextInt(INTERACTIVES_SIZE);

            switch(i){
                case 0: interactives.add(new ChangeColor(thisCardID));
                    break;
                case 1:
                    ArrayList<Type> types = generateTypes(5, null);
                    interactives.add(new CopyNameAndType(thisCardID, types));
                    break;
                case 2: interactives.add(new CopyNameColorStrengthMalusFromHand(thisCardID));
                    break;
                case 3:
                    int howManyToGenerate = randomGenerator.nextInt(10) + 1;
                    interactives.add(new DeleteOneMalusOnType(thisCardID, generateTypes(howManyToGenerate, null)));
                    break;
                case 4:
                    int howMany = randomGenerator.nextInt(10) + 1;
                    interactives.add(new TakeCardOfTypeAtTheEnd(thisCardID, generateTypes(howMany, null)));
                    break;
            }
        }
        else{
            interactives = null;
        }
        return interactives;
    }

    private ArrayList<Type> generateTypes(int howMany, Type except){
        ArrayList<Type> types = new ArrayList<>();
        ArrayList<Type> all = new ArrayList<>(){{add(ARMY); add(ARTIFACT); add(CREATURE); add(EARTH); add(FIRE); add(FLOOD); add(LEADER); add(WEAPON); add(WEATHER); add(WIZARD); add(WILD);}};
        if(except != null){
            all.remove(except);
        }
        Random random = new Random();
        for(int i = 0; i < howMany; i++){
            int index = random.nextInt(all.size());
            types.add(all.get(index));
            all.remove(index);
        }
        return types;
    }

    private ArrayList<Integer> generateCardIDs(int howMany, Integer except, ArrayList<Type> forTypes){
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<Integer> all = new ArrayList<>();
        for(int i = 1; i < 55; i++){
            // add only those that are of given types
            if(forTypes != null){
                Type typeOfCard = BigSwitches.switchNameForType(BigSwitches.switchIdForName(i));
                if(forTypes.contains(typeOfCard)){
                    all.add(i);
                }
            } else{
                all.add(i);
            }
        }
        if(except != null){
            all.remove(except);
        }
        Random random = new Random();
        for(int i = 0; i < howMany; i++){
            int index = random.nextInt(all.size());
            ids.add(all.get(index));
            all.remove(index);
        }
        return ids;
    }
}

/*

switch(index){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    case 10:
                        break;
                    case 11:
                        break;
                    case 12:
                        break;
                    case 13:
                        break;
                    case 14:
                        break;
                    case 15:
                        break;
                    case 16:
                        break;
                    case 17:
                        break;
                    case 18:
                        break;
                    case 19:
                        break;
                    case 20:
                        break;
                    case 21:
                        break;
                    case 22:
                        break;
                    case 23:
                        break;
                    case 24:
                        break;
                    case 25:
                        break;
                    case 26:
                        break;
                    case 27:
                        break;
                    case 28:
                        break;
                    case 29:
                        break;
                    case 30:
                        break;
                    case 31:
                        break;


                }

 */
