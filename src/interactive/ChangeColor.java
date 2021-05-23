package interactive;

import artificialintelligence.ScoreCounterForAI;
import artificialintelligence.State;
import bonuses.*;
import maluses.*;
import server.Card;
import server.ClientHandler;
import server.Server;
import server.Type;
import util.HandCloner;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The interactive bonus that implements the possibility of changing color of one card
 * other than the one containing this bonus.
 * @author Tereza Miklóšová
 */
public class ChangeColor extends Interactive {
    /**
     * This bonus is computed after CopyNameAndType.
     */
    public int priority = 4;

    /**
     * This card's color can't be changed by this bonus.
     */
    public int thiscardid;

    /**
     * Deleted cards in hand.
     */
    ArrayList<Card> deletedCards = new ArrayList<>();

    /**
     * Constructor for interactive bonus.
     * @param id Id of the card that can;t be affected by this bonus.
     */
    public ChangeColor(int id) {
        this.thiscardid = id;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("interactive.CardInteractives",loc);
        sb.append(rb.getString("changeColor"));
        return sb.toString();
    }

    // All dialogs made with the help of example on https://code.makery.ch/blog/javafx-dialogs-official/

    @Override
    public void askPlayer(ClientHandler client) {
        client.sendMessage("ChangeColor#" + thiscardid);

    }
/*
// OPTIMIZED WITH THREADS, TOO MUCH THREADING
    @Override
    public void changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        long startTime = System.nanoTime();
        //System.out.println("Counting ChangeColor");

        Type bestTypeToChangeInto = null;
        Card bestCardToChange = null;

        // Remove this interactive from card to not get stuck in loop
        for (Card original : originalHand) {
            if (thiscardid == original.id) {
                original.interactives.remove(this);
            }
        }

        //Copy hand for computing the original score on Hand
        List<Card> newHandOldScore = new ArrayList<>();
        for (Card copy : originalHand) {
            ArrayList<Malus> maluses = new ArrayList<>();
            ArrayList<Interactive> interactives = new ArrayList<>();
            // bonuses are never deleted, they can be the same objects
            // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
            if (copy.maluses != null) {
                //System.out.println("Klonuji kartu " + copy.name);
                for (Malus m : copy.maluses) {
                    maluses.add(m.clone());
                }
            }
            // The same rule apply to interactives = they can be deleted to signal it has been used
            if (copy.interactives != null) {
                for (Interactive inter : copy.interactives) {
                    interactives.add(inter.clone());
                }
            }
            newHandOldScore.add(new Card(copy.id, copy.name, copy.strength, copy.type, copy.bonuses, maluses, interactives));
        }

        //Count the score of the hand without this interactive to get the base best score
        ScoreCounterForAI sc = new ScoreCounterForAI();
        int bestScore = sc.countScore(newHandOldScore, cardsOnTable);

       // ArrayList<Type> typesToTry = new ArrayList<Type>(Arrays.asList(FLOOD, FLAME, LAND, WEATHER, ARMY, WEAPON, ARTIFACT, WIZARD, LEADER, BEAST, WILD));

        ArrayList<ScoreCounterChangeColorThread> threads = new ArrayList<>();
        // Try out different changes and find out the actual best possible score
        for(Card card : originalHand) {
            if(card.getId() != thiscardid){
                ScoreCounterChangeColorThread thread = new ScoreCounterChangeColorThread(originalHand,cardsOnTable,card);
                thread.start();
                threads.add(thread);
            }
        }

        for(ScoreCounterChangeColorThread thread : threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(ScoreCounterChangeColorThread thread : threads){
            int score = thread.getBestScore();
            if(score > bestScore){
                bestScore = score;
                bestTypeToChangeInto = thread.getBestColor();
                bestCardToChange = thread.getToChange();
            }
        }

        if(bestTypeToChangeInto != null){
            bestCardToChange.type = bestTypeToChangeInto;
            //System.out.println("Changed the Color of " + bestCardToChange.name + " to be [" + bestCardToChange.type + "]");
        }
        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("Total execution time spent in ChangeColor in millis: " + elapsedTime/1000000);

    }


 */

/*
    //NOT OPTIMIZED
    @Override
    public int changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        long startTime = System.nanoTime();
        //System.out.println("Counting ChangeColor");

        Type bestTypeToChangeInto = null;
        Card bestCardToChange = null;

        // Remove this interactive from card to not get stuck in loop
        for (Card original : originalHand) {
            if (thiscardid == original.id) {
                original.interactives.remove(this);
            }
        }

        //Copy hand for computing the original score on Hand
        List<Card> newHandOldScore = new ArrayList<>();
        for (Card copy : originalHand) {
            ArrayList<Malus> maluses = new ArrayList<>();
            ArrayList<Interactive> interactives = new ArrayList<>();
            // bonuses are never deleted, they can be the same objects
            // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
            if (copy.maluses != null) {
                //System.out.println("Klonuji kartu " + copy.name);
                for (Malus m : copy.maluses) {
                    maluses.add(m.clone());
                }
            }
            // The same rule apply to interactives = they can be deleted to signal it has been used
            if (copy.interactives != null) {
                for (Interactive inter : copy.interactives) {
                    interactives.add(inter.clone());
                }
            }
            newHandOldScore.add(new Card(copy.id, copy.name, copy.strength, copy.type, copy.bonuses, maluses, interactives));
        }

        //Count the score of the hand without this interactive to get the base best score
        ScoreCounterForAI sc = new ScoreCounterForAI();
        int bestScore = sc.countScore(newHandOldScore, cardsOnTable, true);

        ArrayList<Type> typesToTry = new ArrayList<Type>(Arrays.asList(Type.FLOOD, Type.FLAME, Type.LAND, Type.WEATHER,
                Type.ARMY, Type.WEAPON, Type.ARTIFACT, Type.WIZARD, Type.LEADER, Type.BEAST, Type.WILD));

        // Try out different changes and find out the actual best possible score
        for(Card card : originalHand) {
            if(card.getId() != thiscardid){
                for (Type type : typesToTry) {
                    List<Card> newHand = new ArrayList<>();
                    for (Card copy : originalHand) {
                        ArrayList<Malus> maluses = new ArrayList<>();
                        ArrayList<Interactive> interactives = new ArrayList<>();
                        // bonuses are never deleted, they can be the same objects
                        // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
                        if (copy.maluses != null) {
                            //System.out.println("Klonuji kartu " + copy.name);
                            for (Malus m : copy.maluses) {
                                maluses.add(m.clone());
                            }
                        }
                        // The same rule apply to interactives = they can be deleted to signal it has been used
                        if (copy.interactives != null) {
                            for (Interactive inter : copy.interactives) {
                                interactives.add(inter.clone());
                            }
                        }
                        // Change the color of card if it is its turn to change it, otherwise just copy the card for further score counting
                        if (copy.equals(card)) {
                            newHand.add(new Card(copy.id, copy.name, copy.strength, type, copy.bonuses, maluses, interactives));
                        } else {
                            newHand.add(new Card(copy.id, copy.name, copy.strength, copy.type, copy.bonuses, maluses, interactives));
                        }

                    }

                    int currentScore = sc.countScore(newHand, cardsOnTable, true);
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestTypeToChangeInto = type;
                        bestCardToChange = card;
                    }
                    //System.out.println("After changing color of " + card.name + " to [" + card.type + "] the score IS " + currentScore);
                }
            }
        }

        if(bestTypeToChangeInto != null){
            bestCardToChange.type = bestTypeToChangeInto;
            //System.out.println("Changed the Color of " + bestCardToChange.name + " to be [" + bestCardToChange.type + "]");
        }
        long elapsedTime = (System.nanoTime() - startTime) / 1000000;
        //System.out.println("Total execution time spent in performMove in millis: " + elapsedTime/1000000);

        return bestScore;
    }
    */

    // OPTIMIZED SELECTION
    @Override
    public int changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable, Server server,
                                         Integer idOfCardToChange, Card cardToChangeInto)
            throws CloneNotSupportedException {
        int maxScore;
        Card suitableCard = null;
        // Remove this interactive from card to not get stuck in loop
        for (Card original : originalHand) {
            if (thiscardid == original.getId()) {
                original.getInteractives().remove(this);
            }
        }
        HandCloner hc = new HandCloner();
        ScoreCounterForAI sc = new ScoreCounterForAI(server);
                ArrayList<Card> copiedHand = hc.cloneHand(null,originalHand);
        maxScore = sc.countScore(copiedHand, cardsOnTable, true);
        ArrayList<Card> handWithoutThisCard = new ArrayList<>(copiedHand);
        handWithoutThisCard.removeIf(card -> card.getId() == thiscardid);
        ArrayList<Type> deletedTypes = getDeletedTypes(copiedHand, handWithoutThisCard);
        ArrayList<Type> colors =
                new ArrayList<>(Arrays.asList(Type.ARMY, Type.ARTIFACT, Type.BEAST, Type.FLAME, Type.FLOOD,
                        Type.LAND, Type.LEADER, Type.WEAPON, Type.WEATHER, Type.WIZARD));
        colors.removeAll(deletedTypes);
        Type minType;
        Type toChangeInto = null;
        Card worstColorCard = null;
        List<Card> worstColorCards;
        Map<Type, Integer> typeValues = getTypeValues(copiedHand, handWithoutThisCard, deletedTypes);
        if(!typeValues.isEmpty()){
            toChangeInto = getMax(typeValues);

            minType = getMin(typeValues);
            //        for(Map.Entry<Type, Integer> entry : typeValues.entrySet()){
            //            System.out.println(BigSwitches.switchTypeForName(entry.getKey()) + " = " + entry.getValue());
            //        }
            //        System.out.println();
            //        System.out.println("MaxEntryType : " + toChangeInto);
            //                System.out.println("handwithoutThisCard:");
            //                for(Card c : handWithoutThisCard){
            //                    System.out.print(c.getName() + ", ");
            //                }
            //                System.out.println();
            //
            //                System.out.println("Max i min entries jsou stejne, to je divne.");
            //


            if(deletedCards.isEmpty() &&
                typeValues.get(toChangeInto) == 0
                && typeValues.get(minType) == 0){
                return maxScore;
        } else {
            // First try to change those that substract points
            ArrayList<Card> doNotChange = ratherNotChange(copiedHand, handWithoutThisCard);
            minType = getMin(typeValues);
            worstColorCards = getMinTypeCard(handWithoutThisCard, minType);

            // The card we want to change is essential, we cant change it
            if(!worstColorCards.isEmpty()) {
                worstColorCard = worstColorCards.get(0);
            }

            while (doNotChange.contains(worstColorCard) || worstColorCard == null) {
                // We are trying to find a low valued color card in our hand that we could change for the better
                typeValues.keySet().remove(minType);
                if (!typeValues.isEmpty() && typeValues.size() > 1) {
                    minType = getMin(typeValues);

                    if(minType != toChangeInto){
                        worstColorCards = getMinTypeCard(handWithoutThisCard, minType);
                        if(!worstColorCards.isEmpty()) {
                            worstColorCard = worstColorCards.get(0);
                        }
                    }
                } else {
                    // TypeValues are empty or there is only one value left = the value to change into
                    break;
                }
            }

            if (worstColorCard != null) {
                // Try the combination of the worst Color cards to avoid lesser score (for example Princess, Queen and King,
                // toChangeInto is ARMY and better benefit is if PRINCESS is turned into army because she benefits from all)
                for(Card c : worstColorCards){
                    int currentScore;
                    Type storeType = worstColorCard.getType();
                    worstColorCard.setType(toChangeInto);
                    currentScore = sc.countScore(copiedHand, cardsOnTable, true);
                    worstColorCard.setType(storeType);
                    if(currentScore > maxScore){
                        worstColorCard = c;
                        maxScore = currentScore;
                    }
                }
                    Type storeType = worstColorCard.getType();
                    worstColorCard.setType(toChangeInto);
                    maxScore = sc.countScore(copiedHand, cardsOnTable, true);
                    worstColorCard.setType(storeType);
                    //System.out.println(worstColorCard.getName() + " --> " + BigSwitches.switchTypeForName(toChangeInto) + " = " + maxScore);
            }  // There is no worst card that we could change

            }
        }
        // Treatment of Warlord
        if(originalHand.stream().anyMatch(card -> card.getId() == 18)){
            int tempScore;

            for(Card c : handWithoutThisCard){
                Type storeType = c.getType();
                c.setType(Type.ARMY);
                tempScore = sc.countScore(copiedHand, cardsOnTable, true);
                if(tempScore > maxScore){
                    maxScore = tempScore;
                    suitableCard = c;
                }
                c.setType(storeType);
            }
        }
        // Treatment of Fountain of Life
        if(originalHand.stream().anyMatch(card -> card.getId() == 32)){
            Card suitableCardToGet = null;
            int tempScore = handleFountain(copiedHand, cardsOnTable, typeValues, suitableCardToGet, sc);
            if(tempScore > maxScore){
                maxScore = tempScore;
                suitableCard = suitableCardToGet;
            }
        }
        // Treatment of deleted cards
        if(!deletedCards.isEmpty()){
            // try to change deleted cards
            if (toChangeInto == null) {
                toChangeInto = colors.get(0);
            }
            int tempScore;
                for(Card c : deletedCards){
                    Type storeType = c.getType();
                    c.setType(toChangeInto);
                    tempScore = sc.countScore(copiedHand, cardsOnTable, true);
                        if(tempScore > maxScore){
                            maxScore = tempScore;
                            suitableCard = c;
                        }
                    c.setType(storeType);
                }
        }
        // Treatment of World Tree card
        if(copiedHand.stream().anyMatch(card -> card.getId() == 30)){
            List<Card> tree = copiedHand.stream().filter(card -> card.getId() == 30).collect(Collectors.toList());
            Card treeCard = tree.get(0);
            Bonus b = treeCard.getBonuses().get(0);
            Card toChangeCard = ((PlusIfTypesAreUnique)b).whichCardNeedsChange(copiedHand);
            if(toChangeCard != null) {
                int tempScore;
                Type storeType = toChangeCard.getType();
                toChangeCard.setType(toChangeInto);
                tempScore = sc.countScore(copiedHand, cardsOnTable, true);
                if (tempScore > maxScore) {
                    maxScore = tempScore;
                    suitableCard = toChangeCard;
                }
                toChangeCard.setType(storeType);
            }
        }
        if(suitableCard != null && toChangeInto != null){
            suitableCard.setType(toChangeInto);
            idOfCardToChange = suitableCard.getId();
            ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
            cardToChangeInto = HandCloner.cloneCard(suitableCard);
            cardToChangeInto.setType(toChangeInto);
        }

        return maxScore;
    }


    /**
     * Returns cards of given type in hand.
     * @param handWithoutThisCard The cards in hand without the holder of this bonus.
     * @param minType The type of cards to be extracted.
     * @return List of cards in hand of given type.
     */
    private ArrayList<Card> getMinTypeCard(ArrayList<Card> handWithoutThisCard, Type minType){
        ArrayList<Card> cards = new ArrayList<>();
        for(Card c : handWithoutThisCard){
            if(c.getType() == minType){
                cards.add(c);
            }
        }
        return cards;
    }

    /**
     * Gets type with minimal gain.
     * @param map Map of types and their awarded points for getting +1 of that type.
     * @return Type with minimal points gain.
     */
    private Type getMin(Map<Type, Integer> map){
        Type min = null;
        int minsco = 999;
        for(Map.Entry<Type, Integer> entry : map.entrySet()){
            if(entry.getValue() < minsco){
                min = entry.getKey();
                minsco = entry.getValue();
            }
        }
        return min;
    }

    /**
     * Gets type of maximal gain from the map.
     * @param map Map of types and their awarded points for getting +1 of that type.
     * @return Type of maximal gain from the map.
     */
    private Type getMax(Map<Type, Integer> map){
        Type max = null;
        int maxsco = -999;
        for(Map.Entry<Type, Integer> entry : map.entrySet()){
            if(entry.getValue() > maxsco){
                max = entry.getKey();
                maxsco = entry.getValue();
            }
        }
        return max;
    }

    /**
     * Computes how to use the bonus if fountain is to be used most effectively.
     * @param hand Cards in hand.
     * @param table Cards on table.
     * @param typeValues Map of types and their awarded gain if the type is added +1 to hand.
     * @param suitableCardToGet Which card is good for recoloring
     * @return Maximal score using the fountain feature.
     */
    private int handleFountain(ArrayList<Card> hand, ArrayList<Card> table,
                               Map<Type, Integer> typeValues, Card suitableCardToGet, ScoreCounterForAI sc){
        int maxScore = -999;

        Type winningType = getWinningTypeForFountain( typeValues);
        int tempScore;
        for(Card c : hand){
            Type storeType = c.getType();
            c.setType(winningType);
            tempScore = sc.countScore(hand, table, true);
            if(tempScore > maxScore){
                maxScore = tempScore;
                suitableCardToGet = c;
            }
            c.setType(storeType);
        }

        return maxScore;
    }

    /**
     * Gets the most suitable type to recolor into with fountain regarding the gains from types.
     * @param typeValues Map of types and their awarded gain if the type is added +1 to hand.
     * @return Type to change chosen card into.
     */
    private Type getWinningTypeForFountain( Map<Type, Integer> typeValues){
        Type bestType = null;
        Map<Type, Integer> typeValuesCopy = new HashMap<>(typeValues);
        ArrayList<Type> fountainColors =
                new ArrayList<>(Arrays.asList(Type.FLAME, Type.FLOOD,
                        Type.LAND, Type.WEAPON, Type.WEATHER));
        while(bestType == null){
            if(typeValuesCopy.isEmpty()){
                return Type.FLOOD;
            }
            Map.Entry<Type, Integer> maxEntry = Collections.max(typeValuesCopy.entrySet(),
                    Map.Entry.comparingByValue());

            if(fountainColors.contains(maxEntry.getKey()) && maxEntry.getValue() > 0){
                bestType = maxEntry.getKey();
            } else{
                typeValuesCopy.entrySet().remove(maxEntry);
            }
        }
        return bestType;
    }

    /**
     * Which cards should not be changed if there is some other better option
     * @param originalHand Cards in hand.
     * @param handWithoutThisCard Cards in hand without card containing this bonus.
     * @return List of cards that should preferably not be changed unless no other option.
     */
    private ArrayList<Card> ratherNotChange(ArrayList<Card> originalHand,ArrayList<Card> handWithoutThisCard){
        ArrayList<Card> doNotChange = new ArrayList<>();
        for(Card c : handWithoutThisCard){
            if(c.getBonuses() != null){
                for(Bonus b : c.getBonuses()){
                    if(b instanceof BonusOrBonus || b instanceof PlusIfYouHaveAllCardsAndAtLeastOneType ||
                            b instanceof PlusIfYouHaveAtLeastOneType){
                        Card satisfiesCondition = b.satisfiesCondition(originalHand);
                        if (!doNotChange.contains(satisfiesCondition)) {
                            doNotChange.add(satisfiesCondition);

                        }

                    }
                }
            }
            if(c.getMaluses() != null){
                for(Malus m : c.getMaluses()){
                    if(m instanceof CardIsDeletedIfYouDontHaveAtLeastOneType || m instanceof MinusIfYouDontHaveAtLeastOneType ){
                        Card satisfiesCondition = m.satisfiesCondition(originalHand);
                        if (!doNotChange.contains(satisfiesCondition)) {
                            doNotChange.add(satisfiesCondition);
                        }
                    }

                }
            }
        }
        return doNotChange;
    }

    /**
     * Makes map of types and gain awarded for getting +1 card of that type to hand.
     * @param hand Cards in hand.
     * @param handWithoutThis Cards in hand without card containing this bonus.
     * @param deletedTypes Cards that are deleted because of some penalties.
     * @return Map of types and their points gain if there is one more card of that type in hand.
     */
    private Map<Type, Integer> getTypeValues(ArrayList<Card> hand, ArrayList<Card> handWithoutThis, ArrayList<Type> deletedTypes){
        Map<Type, Integer> map = new HashMap<>();
        map.put(Type.ARMY, 0);
        map.put(Type.ARTIFACT, 0);
        map.put(Type.BEAST, 0);
        map.put(Type.FLOOD, 0);
        map.put(Type.FLAME, 0);
        map.put(Type.LAND, 0);
        map.put(Type.LEADER, 0);
        map.put(Type.WEATHER, 0);
        map.put(Type.WIZARD, 0);
        map.put(Type.WEAPON, 0);
        map.put(Type.WILD, 0);
    // If the type isnt in the deleted types, add it to the map or get sum of bonuses you can get from it
        for(Card c : handWithoutThis){
            if(c.getBonuses() != null){
                for(Bonus b : c.getBonuses()){
                    if(b instanceof PlusForSameColorCards){
                        Map<Type, Integer> temp = ((PlusForSameColorCards)b).howMuchCanTypeGive(hand);
                        for(Map.Entry<Type,Integer> entry : temp.entrySet()){
                            int oldValue = map.get(entry.getKey());
                            map.put(entry.getKey(), entry.getValue() + oldValue);
                        }
                    } else{
                        ArrayList<Type> typesAvailable = b.getTypesAvailable(hand);
                        if(typesAvailable != null) {
                            for (Type t : typesAvailable) {
                                int oldValue = map.get(t);
                                map.put(t, b.getHowMuch(hand) + oldValue);
                            }
                        }
                    }
                }
            }
            if(c.getMaluses() != null){
                ArrayList<Malus> copiedMaluses = new ArrayList<>(c.getMaluses());
                for(Malus m : copiedMaluses){
                    ArrayList<Type> typesAvailable = m.getTypesAvailable(hand);
                    if(typesAvailable != null){
                        for(Type t: typesAvailable){
                            if (!map.containsKey(t)) {
                                map.put(t, 0);
                            } else{
                                int oldValue = map.get(t);
                                map.put(t, m.getHowMuch(hand) + oldValue);
                            }

                        }
                    }
                }
            }
        }
        // Remove all deleted types as they do not count at all
        map.keySet().removeAll(deletedTypes);
        return map;
    }

    /**
     * Makes list of deleted types.
     * @param hand Cards in hand.
     * @param handWithoutThis Cards in hand without the card containing this bonus.
     * @return List of deleted types.
     */
    private ArrayList<Type> getDeletedTypes(ArrayList<Card> hand, ArrayList<Card> handWithoutThis){
        ArrayList<Type> types = new ArrayList<>();
        for(Card c : handWithoutThis){
            if(c.getBonuses() != null){
                for(Bonus b : c.getBonuses()){
                    if(b instanceof DeleteAllMaluses ||
                            b instanceof DeleteAllMalusesOnType || b instanceof DeleteSelftypeFromAllMaluses ||
                            b instanceof DeleteTypeFromAllMalusesOnType){
                        b.count(handWithoutThis);
                    }
                }
            }
        }
        List<Card> deleted1 = new ArrayList<>();
        List<Card> deleted2 = new ArrayList<>();
        List<Card> deleted3 = new ArrayList<>();
        List<Card> deleted4 = new ArrayList<>();
        List<Card> malusCards = new ArrayList<>();
        for(Card c : handWithoutThis){
            if(c.getMaluses() != null){
                // These ones are to solely store deleted types without exceptions
                for(Malus m : c.getMaluses()){
                    if(m instanceof DeletesAllType ){
                        types.addAll(m.getTypes());
                        deleted1 = hand.stream().filter(card -> m.getTypes().contains(card.getType())).collect(Collectors.toList());
                        malusCards.add(c);
                    }
                    if(m instanceof DeletesAllTypeOrOtherSelftype){
                        types.addAll(m.getTypes());
                        deleted2 = hand.stream().filter(card -> m.getTypes().contains(card.getType()) && card.getId() != c.getId()).collect(Collectors.toList());
                        malusCards.add(c);
                    }
                    if(m instanceof DeletesAllExceptTypeOrCard){
                        types.addAll(m.getTypes());
                        deleted3 = hand.stream().filter(card -> m.getTypes().contains(card.getType()) && !m.getCards().contains(card.getId())).collect(Collectors.toList());
                        malusCards.add(c);
                    }
                    if(m instanceof DeletesAllTypeExceptCard){
                        types.addAll(m.getTypes());
                        deleted4 = hand.stream().filter(card -> m.getTypes().contains(card.getType()) && !m.getCards().contains(card.getId())).collect(Collectors.toList());
                        malusCards.add(c);
                    }
                }
            }
        }
        Set<Card> set = new LinkedHashSet<>(deleted1);
        set.addAll(deleted2);
        set.addAll(deleted3);
        set.addAll(deleted4);
        deletedCards = new ArrayList<>(set);
        List<Card> finalMalusCards = malusCards;
        if(deletedCards.stream().anyMatch(finalMalusCards::contains)){
            boolean somethingChnaged = true;
            while(somethingChnaged){
                somethingChnaged = false;
                finalMalusCards = new ArrayList<>(malusCards);
                deleted1 = new ArrayList<>();
                deleted2 = new ArrayList<>();
                deleted3 = new ArrayList<>();
                deleted4 = new ArrayList<>();
                malusCards = new ArrayList<>();
                types.clear();
                for(Card malusCard : finalMalusCards){
                    if(!deletedCards.contains(malusCard)) {
                        for (Malus m : malusCard.getMaluses()) {
                            if(m instanceof DeletesAllType ){
                                types.addAll(m.getTypes());
                                deleted1 = hand.stream().filter(card -> m.getTypes().contains(card.getType())).collect(Collectors.toList());
                                malusCards.add(malusCard);
                            }
                            if(m instanceof DeletesAllTypeOrOtherSelftype){
                                types.addAll(m.getTypes());
                                deleted2 = hand.stream().filter(card -> m.getTypes().contains(card.getType()) && card.getId() != malusCard.getId()).collect(Collectors.toList());
                                malusCards.add(malusCard);
                            }
                            if(m instanceof DeletesAllExceptTypeOrCard){
                                types.addAll(m.getTypes());
                                deleted3 = hand.stream().filter(card -> m.getTypes().contains(card.getType()) && !m.getCards().contains(card.getId())).collect(Collectors.toList());
                                malusCards.add(malusCard);
                            }
                            if(m instanceof DeletesAllTypeExceptCard){
                                types.addAll(m.getTypes());
                                deleted4 = hand.stream().filter(card -> m.getTypes().contains(card.getType()) && !m.getCards().contains(card.getId())).collect(Collectors.toList());
                                malusCards.add(malusCard);
                            }
                        }
                    }
                }
                set = new LinkedHashSet<>(deleted1);
                set.addAll(deleted2);
                set.addAll(deleted3);
                set.addAll(deleted4);
                deletedCards = new ArrayList<>(set);
            }
        }

        return types;
    }

    @Override
    public Interactive clone() throws CloneNotSupportedException{
        ChangeColor newi = (ChangeColor)super.clone();
        newi.priority = this.priority;
        newi.thiscardid = this.thiscardid;
        return newi;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }
}
