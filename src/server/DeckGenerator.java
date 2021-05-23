package server;

import bonuses.*;
import interactive.*;
import maluses.*;
import util.BigSwitches;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import static server.Type.*;

/**
 * Class for creating cards with randomized properties. The id, names and types of the cards remain the same.
 * Only bonuses, maluses, interactives and strength of the card are generated.
 * @author Tereza Miklóšová
 */
public class DeckGenerator {

    /**
     * Number of Attributes categories.
     */
    private final int SEED_SIZE = 20;
    /**
     * The final border of one bonus pool.
     */
    private final int SEED_BONUS_END = 6;
    /**
     * The final border of one malus pool.
     */
    private final int SEED_MALUS_END = 11;
    /**
     * The final border of one interactive field.
     */
    private final int SEED_INTERACTIVE_END = 14;
    /**
     * The final border of one bonus and one malus field.
     */
    private final int SEED_BONUS_MALUS_END = 17;
    /**
     * The final border of 2 bonuses field.
     */
    private final int SEED_2BONUS_END = 18;

    /**
     * Generates deck of cards with randomized properties. The id, names and types of the cards remain the same.
     * @return List of generated cards.
     */
    public ArrayList<Card> generateDeck(){
        Locale locale = new Locale("en");
        ArrayList<Card> deck = new ArrayList<>();
        for(int i = 1; i < 55;i++){
            Random randomGenerator = new Random();
                int seed = randomGenerator.nextInt(SEED_SIZE);
            ArrayList<Bonus> bonuses = generateBonuses(seed, i, false);
            ArrayList<Malus> maluses = generateMaluses(seed, i);
            ArrayList<Interactive> interactives = generateInteractives(seed, i);
            Card newCard = new Card(i, BigSwitches.switchIdForName(i,locale.getLanguage()), getStrengthByBonuses(seed), BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(i, locale.getLanguage())), bonuses, maluses, interactives );
            deck.add(newCard);
        }

        return deck;
    }

    /**
     * generates strength based by the bonuses/maluses/interactives mixed on the card.
     * @param seed What category of attributes is going to be created for the card.
     * @return Strength of the card.
     */
    private int getStrengthByBonuses(int seed){
        int strength;
        Random random = new Random();
        if(seed <= SEED_BONUS_END && seed >= 0){
            strength = random.nextInt(10);
        } else if(seed <= SEED_MALUS_END){
            strength = random.nextInt(10) + 15;
        } else if(seed <= SEED_INTERACTIVE_END){
            strength = random.nextInt(5);
        } else if(seed <= SEED_BONUS_MALUS_END){
            strength = random.nextInt(5) + 5;
        } else if(seed <= SEED_2BONUS_END){
            strength = random.nextInt(5);
        } else{
            strength = random.nextInt(10) + 35;

        }

        return strength;
    }

    /**
     * Randomly generates bonuses by given seed on the card of given id. if the given seed does not correspond
     * to any bonuses, no bonuses will be generated.
     * @param seed Seed of the randomized choice.
     * @param thisCardID The card for which the bonuses are being created.
     * @param notBonusOrBonus True if the bonus is not bonus or bonus one. False if the bonus is the bonus or bonus one.
     * @return List of generated bonuses.
     */
    private ArrayList<Bonus> generateBonuses(int seed, int thisCardID, boolean notBonusOrBonus){
        ArrayList<Bonus> bonuses;
        if(((seed >= 0) && (seed <= SEED_BONUS_END)) || ((seed > SEED_INTERACTIVE_END) && (seed <= SEED_2BONUS_END))) {
            bonuses = new ArrayList<>();
            int howManyBonuses;
            Random randomGenerator = new Random();
            if (seed == SEED_2BONUS_END) {
                howManyBonuses = 2;
            } else {
                howManyBonuses = 1;
            }
            for (int i = 0; i < howManyBonuses; i++) {

                int BONUSES_SIZE = 24;
                int index = randomGenerator.nextInt(BONUSES_SIZE);
                if(notBonusOrBonus || SEED_BONUS_MALUS_END == seed || SEED_2BONUS_END == seed) {
                    index = (index % 23) +1;
                    while(index < 7){
                        // We dont want deletes to BonusOrBOnus and we don't want long bonuses on bonus/Malus or 2 Bonus
                        int plus = randomGenerator.nextInt(9) + 1;
                        index += plus;
                    }
                }
                int mustHave;
                int mustHaveID;
                int howMuch;
                boolean odd;
                switch (index) {
                    case 0:
                        // even if we want to generate two bonuses, this one must be alone
                        bonuses.add(new BonusOrBonus(generateBonuses(1, thisCardID, true).get(0), generateBonuses(1, thisCardID, true).get(0)));
                        break;
                    case 1: bonuses.add(new DeleteAllMaluses());
                        break;
                    case 2: bonuses.add(new DeleteAllMalusesOnType(generateTypes(1, null).get(0)));
                        break;
                    case 3: bonuses.add(new DeleteSelftypeFromAllMaluses(BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID))));
                        break;
                    case 4: bonuses.add(new DeleteTypeFromAllMalusesOnType(generateTypes(1, null).get(0), generateTypes(1, null).get(0)));
                        break;
                    case 5:
                        bonuses.add(new PlusForStrengthsInRow());

                        break;
                    case 6:
                        bonuses.add(new PlusForSameColorCards());

                        break;
                    case 7:
                        howMuch = randomGenerator.nextInt(10);
                        bonuses.add(new PlusForEachSelftypeExceptThis(10 + howMuch,thisCardID, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID)) ));
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
                        ArrayList<Type> types = generateTypes(mustHave + 1, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID)));
                        bonuses.add(new PlusForEachTypeAndForEachCard(6 + howMuch, types , generateCardIDs(mustHaveID + 1,thisCardID, complementToAllTypes(types))));
                        break;
                    case 10:
                        howMuch = randomGenerator.nextInt(8);
                        mustHave = randomGenerator.nextInt(3);
                        bonuses.add(new PlusForEachTypeIfYouHaveCard(18 + howMuch, generateTypes(mustHave + 1, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID))), generateCardIDs(1,thisCardID,null).get(0)));
                        break;
                    case 11:
                        howMuch = randomGenerator.nextInt(4);
                        mustHave = randomGenerator.nextInt(3);
                        bonuses.add(new PlusForEachTypeOrSelfType(5+howMuch, thisCardID, generateTypes(mustHave+1, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID))) , BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID))));
                        break;
                    case 12:
                        howMuch = randomGenerator.nextInt(5);
                        mustHave = randomGenerator.nextInt(2);
                        odd = mustHave != 0;
                        bonuses.add(new PlusForEachOdd(howMuch + 1, odd, thisCardID));
                        break;
                    case 13:
                        howMuch = randomGenerator.nextInt(20);
                        mustHave = randomGenerator.nextInt(5);
                        bonuses.add(new PlusForEachCard(howMuch + 1, generateCardIDs(mustHave + 1,thisCardID, null)));
                        break;
                    case 14:
                        howMuch = randomGenerator.nextInt(10);
                        mustHave = randomGenerator.nextInt(2);

                        odd = mustHave != 0;
                        bonuses.add(new PlusIfAllAreOdd(howMuch + 45, odd));
                        break;
                    case 15:
                        howMuch = randomGenerator.nextInt(10);
                        bonuses.add(new PlusIfTypesAreUnique(45 + howMuch));
                        break;
                    case 16:
                        howMuch = randomGenerator.nextInt(10);
                        bonuses.add(new PlusIfYouDontHaveType(8 + howMuch, generateTypes(1, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID))).get(0)));
                        break;
                    case 17:
                        howMuch = randomGenerator.nextInt(10);
                        mustHave = randomGenerator.nextInt(2);
                        bonuses.add(new PlusIfYouHaveAll(25 + howMuch, generateCardIDs(mustHave + 1, thisCardID, null)));
                        break;
                    case 18:
                        howMuch = randomGenerator.nextInt(40);
                        mustHave = randomGenerator.nextInt(3);
                        mustHaveID = randomGenerator.nextInt(2);
                        bonuses.add(new PlusIfYouHaveAllCardsAndAtLeastOneType(80 + howMuch, generateCardIDs(mustHave + 1, thisCardID, null), generateTypes(mustHaveID+1, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID)) )));
                        break;
                    case 19:
                        howMuch = randomGenerator.nextInt(4);
                        mustHave = randomGenerator.nextInt(3);
                        bonuses.add(new PlusIfYouHaveAtLeastOneCard(25+howMuch, generateCardIDs(mustHave + 1, thisCardID, null) ));
                        break;
                    case 20:
                        howMuch = randomGenerator.nextInt(4);
                        mustHave = randomGenerator.nextInt(3);
                        bonuses.add(new PlusIfYouHaveAtLeastOneType(18+howMuch, generateTypes(mustHave + 1, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID)))));
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

    /**
     * Randomly generates maluses by given seed on the card of given id. if the given seed does not correspond
     * to any malus scheme, no maluses will be generated.
     * @param seed Seed of the randomized choice.
     * @param thisCardID The card for which the maluses are being created.
     * @return List of generated maluses.
     */
    private ArrayList<Malus> generateMaluses(int seed, int thisCardID){
        ArrayList<Malus> maluses;
        int howManyMaluses;
        int SEED_2MALUS_END = SEED_SIZE - 1;
        if(((seed > SEED_BONUS_END) && (seed <= SEED_MALUS_END)) || ((seed > SEED_INTERACTIVE_END) && (seed <= SEED_BONUS_MALUS_END)) || (seed == SEED_2MALUS_END)){
            maluses = new ArrayList<>();
            if(seed == SEED_2MALUS_END){
                howManyMaluses = 2;
            } else {
                howManyMaluses = 1;
            }
            Random randomGenerator = new Random();
            for(int i = 0; i < howManyMaluses; i++){
                int MALUSES_SIZE = 9;
                int index = randomGenerator.nextInt(MALUSES_SIZE);
                int mustHave;
                int mustHaveID;
                switch(index){
                    case 0:
                        mustHave = randomGenerator.nextInt(2);
                        maluses.add(new CardIsDeletedIfYouDontHaveAtLeastOneType(thisCardID, generateTypes(mustHave + 1, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID)))));
                        break;
                    case 1:
                        mustHave = randomGenerator.nextInt(2);
                        maluses.add(new CardIsDeletedIfYouHaveAnyType(thisCardID, generateTypes(mustHave + 1, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID)))));
                        break;
                    case 2:
                        mustHave = randomGenerator.nextInt(4);
                        mustHaveID = randomGenerator.nextInt(4);
                        maluses.add(new DeletesAllExceptTypeOrCard(generateTypes(mustHave + 1,null), generateCardIDs( mustHaveID + 1, thisCardID, null),thisCardID));
                        break;
                    case 3:
                        mustHave = randomGenerator.nextInt(4);
                        maluses.add(new DeletesAllType(thisCardID, generateTypes(mustHave + 1, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID)) )));
                        break;
                    case 4:
                        mustHave = randomGenerator.nextInt(3);
                        mustHaveID = randomGenerator.nextInt(2);
                        ArrayList<Type> types = generateTypes(mustHave + 1, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID)));
                        maluses.add(new DeletesAllTypeExceptCard(thisCardID, types , generateCardIDs(mustHaveID + 1, thisCardID, types)));
                        break;
                    case 5:
                        Type thisCardType = BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID));
                        mustHave = randomGenerator.nextInt(3);
                        maluses.add(new DeletesAllTypeOrOtherSelftype(generateTypes(mustHave + 1, thisCardType), thisCardType, thisCardID));
                        break;
                    case 6:
                        mustHave = randomGenerator.nextInt(3);
                        mustHaveID = randomGenerator.nextInt(10);
                        maluses.add(new MinusForEachOtherSelftypeOrType( - mustHaveID - 1 , generateTypes(mustHave + 1, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID))) ,BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID, "en")), thisCardID));
                        break;
                    case 7:
                        mustHave = randomGenerator.nextInt(3);
                        mustHaveID = randomGenerator.nextInt(10);
                        maluses.add(new MinusForEachType(- mustHaveID - 1, generateTypes(mustHave + 1, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID)))));
                        break;
                    case 8:
                        mustHave = randomGenerator.nextInt(10);
                        maluses.add(new MinusIfYouDontHaveAtLeastOneType(mustHave - 50, generateTypes(1, BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(thisCardID)))));
                        break;
                }
            }
        }
        else{
            maluses = null;
        }
        return maluses;
    }

    /**
     * Creates a list of types to complement the ones given as parameter to create all 11 colors together.
     * @param iDontWantThose Types to create complement to.
     * @return List of complement types to the ones given in parameter.
     */
    private ArrayList<Type> complementToAllTypes(ArrayList<Type> iDontWantThose){
        ArrayList<Type> all = new ArrayList<>(){{add(ARMY); add(ARTIFACT); add(BEAST); add(LAND); add(FLAME); add(FLOOD); add(LEADER); add(WEAPON); add(WEATHER); add(WIZARD); add(WILD);}};
        all.removeIf(iDontWantThose::contains);
        return all;
    }

    /**
     * Randomly generates interactives by given seed on the card of given id. if the given seed does not correspond
     * to any interactives, no interactives will be generated.
     * @param seed Seed of the randomized choice.
     * @param thisCardID The card for which the interactives are being created.
     * @return List of generated interactives.
     */
    private ArrayList<Interactive> generateInteractives(int seed, int thisCardID){
        ArrayList<Interactive> interactives;
        if(((seed > SEED_MALUS_END) && (seed <= SEED_INTERACTIVE_END))){
            interactives = new ArrayList<>();

            Random randomGenerator = new Random();
            int INTERACTIVES_SIZE = 5;
            int i = randomGenerator.nextInt(INTERACTIVES_SIZE);

            switch(i){
                case 0: interactives.add(new ChangeColor(thisCardID));
                    break;
                case 1:
                    ArrayList<Type> types = generateTypes(5, null);
                    interactives.add(new CopyNameAndType(thisCardID, types));
                    break;
                case 2: interactives.add(new CopyCardFromHand(thisCardID));
                    break;
                case 3:
                    int howManyToGenerate = randomGenerator.nextInt(10) + 1;
                    interactives.add(new DeleteOneMalusOnType(thisCardID, generateTypes(howManyToGenerate, null)));
                    break;
                case 4:
                    int howMany = randomGenerator.nextInt(6) + 1;
                    interactives.add(new TakeCardOfTypeAtTheEnd(thisCardID, generateTypes(howMany, null)));
                    break;
            }
        }
        else{
            interactives = null;
        }
        return interactives;
    }

    /**
     * Generates given number of random types.
     * @param howMany How many types to generate.
     * @param except Those types are not wanted in the result.
     * @return Generated list of types.
     */
    private ArrayList<Type> generateTypes(int howMany, Type except){
        ArrayList<Type> types = new ArrayList<>();
        ArrayList<Type> all = new ArrayList<>(){{add(ARMY); add(ARTIFACT); add(BEAST); add(LAND); add(FLAME); add(FLOOD); add(LEADER); add(WEAPON); add(WEATHER); add(WIZARD); add(WILD);}};
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

    /**
     * Generates list of random ids of cards.
     * @param howMany How many ids to generate.
     * @param except Those ids will not be in the resulting list.
     * @param forTypes Select ids from these types.
     * @return List of card ids.
     */
    private ArrayList<Integer> generateCardIDs(int howMany, Integer except, ArrayList<Type> forTypes){
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<Integer> all = new ArrayList<>();
        for(int i = 1; i < 55; i++){
            // add only those that are of given types
            if(forTypes != null){
                //System.out.println("forTypes in generateCardIDs: " + forTypes.toString());
                Type typeOfCard = BigSwitches.switchCardNameForType(BigSwitches.switchIdForName(i));
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
