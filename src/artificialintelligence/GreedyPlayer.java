package artificialintelligence;

import bonuses.Bonus;
import interactive.Interactive;
import maluses.Malus;
import server.Card;
import server.PlayerOrAI;
import server.Server;
import util.HandCloner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

public class GreedyPlayer extends PlayerOrAI implements ArtificialIntelligenceInterface{
    ArrayList<Card> hand;

    @Override
    public ArrayList<Card> getStoredHand() {
        return storedHand;
    }
    int insight = 1;
    ArrayList<Card> storedHand;
    Card bestCardToTake;
    Card bestCardToDrop;
    Server server;
    int bestPossibleScore;
    int gainThreshold;
    int rank;
    String name;
    private FutureTask<Integer> futureTask;
    private int numberOfRoundsPlayed;
    String beginningHandCards;
    int beginningHandScore;
    boolean playing = false;
    Map<List<Integer>,Integer> cache;
    CacheMap cacheMap;
    ArrayList<Integer> scoresInRounds = new ArrayList<>();

    public ArrayList<Card> getBestHandAfterTheEnd() {
        return bestHandAfterTheEnd;
    }

    public void setBestHandAfterTheEnd(ArrayList<Card> bestHandAfterTheEnd) {
        this.bestHandAfterTheEnd = bestHandAfterTheEnd;
    }

    public int getBestScoreAfterTheEnd() {
        return bestScoreAfterTheEnd;
    }

    public void setBestScoreAfterTheEnd(int bestScoreAfterTheEnd) {
        this.bestScoreAfterTheEnd = bestScoreAfterTheEnd;
    }

    ArrayList<Card> bestHandAfterTheEnd;
    int bestScoreAfterTheEnd;
    int numberOfChangedCardsInIdealHand;

    public int getNumberOfChangedCardsInIdealHand() {
        return numberOfChangedCardsInIdealHand;
    }

    public void setNumberOfChangedCardsInIdealHand(int numberOfChangedCardsInIdealHand) {
        this.numberOfChangedCardsInIdealHand = numberOfChangedCardsInIdealHand;
    }



    public GreedyPlayer(Server server) throws CloneNotSupportedException {
        hand = new ArrayList<>();
        this.server = server;
        bestPossibleScore = 0;
        bestCardToTake = null;
        bestCardToDrop = null;
        gainThreshold = 0;
        getInitCards();
    }

    public GreedyPlayer(Server server, int gainThreshold, String name) throws CloneNotSupportedException {
        this.name = name;
        //System.out.println("Name of GreedyPlayer " + this.name);
        hand = new ArrayList<>();
        this.server = server;
        bestPossibleScore = 0;
        bestCardToTake = null;
        bestCardToDrop = null;
        numberOfRoundsPlayed = 0;
        this.gainThreshold = gainThreshold;
        this.insight = gainThreshold;
        getInitCards();
        cache = new HashMap<>();
        bestHandAfterTheEnd = new ArrayList<>();
        bestScoreAfterTheEnd = -999;
        //System.out.println("Making Greedy player with gain threshold: " + gainThreshold);
    }

    public GreedyPlayer(Server server, int gainThreshold, String name, CacheMap cacheMap) throws CloneNotSupportedException {
        this.name = name;
        //System.out.println("Name of GreedyPlayer " + this.name);
        hand = new ArrayList<>();
        this.server = server;
        bestPossibleScore = 0;
        bestCardToTake = null;
        bestCardToDrop = null;
        numberOfRoundsPlayed = 0;
        this.gainThreshold = gainThreshold;
        this.insight = gainThreshold;
        getInitCards();
        cache = new HashMap<>();
        this.cacheMap = cacheMap;
        bestHandAfterTheEnd = new ArrayList<>();
        bestScoreAfterTheEnd = -999;
        //System.out.println("Making Greedy player with gain threshold: " + gainThreshold);
    }

    @Override
    public ArrayList<Card> getHand(){
        return hand;
    }

    @Override
    public String getName(){
        return this.name;
    }


    @Override
    public void setPlaying(boolean playing){this.playing = playing;}

    @Override
    public boolean getPlaying(){
        return this.playing;
    }

    @Override
    public int getNumberOfRoundsPlayed(){
        return numberOfRoundsPlayed;
    }

    @Override
    public String getBeginningHandCards(){
        return beginningHandCards;
    }

    @Override
    public int getBeginningHandScore(){
        return beginningHandScore;
    }

    @Override
    public int getRank(){return this.rank;}
    @Override
    public int getScore(){return this.score;}
    @Override
    public void setRank(int r){this.rank = r;}
    @Override
    public void setScore(int s){this.score = s;}

    @Override
    public void getInitCards() throws CloneNotSupportedException {
        hand.clear();
        numberOfRoundsPlayed = 0;
        //Random randomGenerator = new Random();
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < server.CARDS_ON_HAND; j++) {
            //System.out.println("In getInitCards in GreedyPlayer constructor, number of loop is " + j);
            //int index = randomGenerator.nextInt(server.deck.getDeck().size());
            Card newCard = server.deck.getDeck().get(0);
            hand.add(newCard);
            sb.append(newCard.getName()  + ";");
            server.deck.getDeck().remove(newCard);
        }
        beginningHandCards = sb.toString();
        HandCloner hc = new HandCloner();
        List<Card> newHandOldScore = hc.cloneHand(null, hand);

        // Count if there is gain from the table greater than the gainThreshold and keep the highest combination of them
        ScoreCounterForAI sc = new ScoreCounterForAI();
        sc.setServer(server);
        beginningHandScore = sc.countScore(newHandOldScore, new ArrayList<>(), true);
        this.score = beginningHandScore;
        bestHandAfterTheEnd = new ArrayList<>();
        bestScoreAfterTheEnd = -999;
        scoresInRounds = new ArrayList<>();
        scoresInRounds.add(this.score);
    }

    @Override
    public void learn() {
        // Greedy player doesn't learn
    }

    private void manageCache(ArrayList<Card> cardsOnTable, Card toBePutOnTable){
        ArrayList<List<Integer>> toRemove = new ArrayList<>();
        for(Map.Entry<List<Integer>, Integer> entry : cache.entrySet()){
            List<Integer> key = entry.getKey();
            for(Integer id : key){
                if(id == 31) {
                    if (!hand.stream().anyMatch(card -> id.equals(card.getId())) &&
                            !cardsOnTable.stream().anyMatch(card -> id.equals(card.getId())) && !id.equals(toBePutOnTable.getId())) {

                        toRemove.add(key);
                    /*
                    System.out.print("Removing from cache: "); key.forEach(num -> System.out.print(num + ", "));

                    System.out.print(" because " + id.toString() + " is not among ");
                    cardsOnTable.forEach(num -> System.out.print(num.getId() + ", "));
                    System.out.println();
                     */

                        break;
                    }
                    toRemove.remove(key);
                    break;
                }
            }
        }
        cache.keySet().removeAll(toRemove);
        //System.out.println("Cache size = " + cache.size());
    }

    public CacheMap getCacheMap() {
        return cacheMap;
    }

    @Override
    public ArrayList<Integer> getScoresInRound(){
        return scoresInRounds;
    }

    private int[] tableIndexInitializer(int size){
        int[] arr = new int[size];
        for(int i = 0; i < size; i++){
            arr[i] = i;
        }
        return arr;
    }

    public Card findMaxWithInsight(ArrayList<Card> cardsOnTable) {
        int[] handIndexes = {0,1,2,3,4,5,6};
        int[] tableIndexes = tableIndexInitializer(cardsOnTable.size());
        //System.out.print("Cards in hand: " + hand.size() + " ");
        ScoreCounterForAI sc = new ScoreCounterForAI();
            int maxScore = getScore();
            ArrayList<Card> bestHand = new ArrayList<>();
            ArrayList<Integer> worstTupleFromHand = new ArrayList<>();
            ArrayList<Integer> bestTupleFromTable = new ArrayList<>();
            HandCloner hc = new HandCloner();
            bestCardToDrop = null;
            bestCardToTake = null;
        //System.out.print("Cards in hand: " + hand.size() + " ");
        if(!cardsOnTable.isEmpty()){
            for(int i = 1; i < insight + 1; i++){
                //Switch this number of cards
                //System.out.print("Cards in hand: " + hand.size() + " ");
                StateMapCreator smc = new StateMapCreator(new int[1], 0,0);

                ArrayList<ArrayList<Integer>> tuplesInHand = smc.makeNTuples(handIndexes,i);
                ArrayList<ArrayList<Integer>> tuplesOnTable = smc.makeNTuples(tableIndexes,i);
                for(ArrayList<Integer> tupleInHand : tuplesInHand){
                    for(ArrayList<Integer> tupleOnTable : tuplesOnTable){
                        /*
                        System.out.println("Tuple in hand: ");
                        for(Integer indexInHand : tupleInHand){
                            System.out.print(indexInHand + ", ");
                        }
                        System.out.println();
                        System.out.println("Tuple on the table :");
                        for(Integer indexOnTable : tupleOnTable){
                            System.out.print(indexOnTable + ", ");
                        }
                        System.out.println();

                         */
                        try {
                            ArrayList<Card> newHand = hc.cloneHand(null, hand);
                            ArrayList<Card> newTable = hc.cloneHand(null, cardsOnTable);
                            ArrayList<Card> toRemoveFromHand = new ArrayList<>();
                            ArrayList<Card> toRemoveFromTable = new ArrayList<>();

                            for(Integer hand : tupleInHand){
                                toRemoveFromHand.add(newHand.get(hand));
                            }
                            for(Integer t : tupleOnTable){
                                toRemoveFromTable.add(newTable.get(t));
                            }
                            newHand.removeAll(toRemoveFromHand);
                            newTable.removeAll(toRemoveFromTable);

                            newHand.addAll(toRemoveFromTable);
                            newTable.addAll(toRemoveFromHand);
                            int currentScore = 0;

                            int score = getCacheMap().getValue(newHand);
                            if(score > -998) {
                                currentScore = score;

                            } else {
                                currentScore = sc.countScore(newHand, newTable, true);
                                cacheMap.putValue(newHand,currentScore);
                            }
                            if(currentScore > maxScore){
                                maxScore = currentScore;
                                bestTupleFromTable = tupleOnTable;
                                worstTupleFromHand = tupleInHand;
                                bestCardToTake = cardsOnTable.get(bestTupleFromTable.get(0));
                                bestCardToDrop = hand.get(worstTupleFromHand.get(0));
                            }


                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        //System.out.print("Cards in hand: " + hand.size() + " ");
            if(bestCardToTake != null) {
                //System.out.println("Ve chose a card from table: ");
               /*
                System.out.print("Cards in hand: ");
                printCards(hand);
                System.out.print("Cards ion table size: ");
                printCards(cardsOnTable);
                System.out.print("To drop: ");
                for(Integer index : worstTupleFromHand){
                    System.out.print(hand.get(index).getName() + ", ");
                }
                System.out.println();
                System.out.print("To take: ");
                for(Integer index : bestTupleFromTable){
                    System.out.print(cardsOnTable.get(index).getName() + ", ");
                }
                System.out.println();
                System.out.println("This change will result in score : " + maxScore);

                */
                bestCardToDrop = hand.get(worstTupleFromHand.get(0));

                int betterToDropScore = 999;
                for(Integer indexOfWorst : worstTupleFromHand){
                    for(Integer indexOfBest : bestTupleFromTable){
                        try {
                            ArrayList<Card> betterToDrop = hc.cloneHand(null, hand);
                            ArrayList<Card> betterToDropTable = hc.cloneHand(null, cardsOnTable);
                            Card fromHand = hand.get(indexOfWorst);
                            Card fromTable = cardsOnTable.get(indexOfBest);
                            betterToDrop.remove(fromHand);
                            betterToDrop.add(fromTable);
                            betterToDropTable.remove(fromTable);
                            betterToDropTable.add(fromHand);
                            int scoreWithOutThis = getCacheMap().getValue(betterToDrop);
                            if(betterToDropScore > scoreWithOutThis){
                                betterToDropScore = scoreWithOutThis;
                                bestCardToDrop = fromHand;
                                bestCardToTake = fromTable;
                            }
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }

                    }

                }

                bestCardToTake = cardsOnTable.get(bestTupleFromTable.get(0));
                cardsOnTable.remove(bestCardToTake);
                hand.add(bestCardToTake);
            } else{
                // Nothing on the table is good so we draw a card

                Card toDraw = server.drawCardFromDeck();
                //System.out.print("Cards in hand: " + hand.size() + " ");
                //System.out.println("Took card from DECK " + toDraw.getName());
                hand.add(toDraw);
                        // We are trying to drop i cards from hand and replace them with i-1 cards from the table
                //System.out.print("Cards in hand: " + hand.size() + " ");
                        for(int i = 1; i < insight; i++) {
                            //System.out.println("i = " + i);
                            //This time only look one less to the future since we already chose to draw a card
                            //Switch this number of cards
                            StateMapCreator smc = new StateMapCreator(new int[1], 0, 0);
                            int[] handIndexes2 = {0,1,2,3,4,5,6,7};
                            ArrayList<ArrayList<Integer>> tuplesInHand = smc.makeNTuples(handIndexes2,i);
                            ArrayList<ArrayList<Integer>> tuplesOnTable = smc.makeNTuples(tableIndexes,i-1);
                            //System.out.print("Cards in hand: " + hand.size() + " ");
                            if(tuplesOnTable != null) {
                                for (ArrayList<Integer> tupleInHand : tuplesInHand) {

                                    //System.out.println("Tuples on table != null ");
                                    for (ArrayList<Integer> tupleOnTable : tuplesOnTable) {

                                        try {
                                            ArrayList<Card> newHand = hc.cloneHand(null, hand);
                                            ArrayList<Card> newTable = hc.cloneHand(null, cardsOnTable);
                                            ArrayList<Card> toRemoveFromHand = new ArrayList<>();
                                            ArrayList<Card> toRemoveFromTable = new ArrayList<>();

                                            for (Integer hand : tupleInHand) {
                                                toRemoveFromHand.add(newHand.get(hand));
                                            }
                                            for (Integer t : tupleOnTable) {
                                                toRemoveFromTable.add(newTable.get(t));
                                            }
                                            newHand.removeAll(toRemoveFromHand);
                                            newTable.removeAll(toRemoveFromTable);

                                            newHand.addAll(toRemoveFromTable);
                                            newTable.addAll(toRemoveFromHand);
                                            int currentScore = 0;

                                            int score = getCacheMap().getValue(newHand);
                                            if (score > -998) {
                                                currentScore = score;

                                            } else {
                                                currentScore = sc.countScore(newHand, newTable, true);
                                                cacheMap.putValue(newHand, currentScore);
                                            }
                                            if (currentScore > maxScore) {
                                                maxScore = currentScore;
                                                bestCardToDrop = hand.get(worstTupleFromHand.get(0));
                                            }


                                        } catch (CloneNotSupportedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }else {
                                    // We are only trying out the best combination of 7 out of 8 cards in hand
                                    //System.out.println("Tuples on table === null ");

                                    for(Card toChangeInHand : hand){
                                        //System.out.print("Cards in hand: " + hand.size() + " ");
                                        if(toChangeInHand != toDraw){
                                            try {
                                                ArrayList<Card> newHand = hc.cloneHand(toChangeInHand, hand);
                                                //System.out.print("Cards in hand: " + hand.size() + " ");
                                                cardsOnTable.add(toChangeInHand);
                                                ArrayList<Card> newTable = hc.cloneHand(null, cardsOnTable);
                                                int currentScore = 0;

                                                int score = getCacheMap().getValue(newHand);
                                                if (score > -998) {
                                                    currentScore = score;

                                                } else {
                                                    currentScore = sc.countScore(newHand, newTable, true);
                                                    cacheMap.putValue(newHand, currentScore);
                                                }
                                                if (currentScore > maxScore) {
                                                    maxScore = currentScore;
                                                    bestCardToDrop = toChangeInHand;
                                                }
                                                //System.out.print("Cards in hand: " + hand.size() + " ");
                                                cardsOnTable.remove(toChangeInHand);
                                            }catch(CloneNotSupportedException ex){
                                                ex.printStackTrace();
                                            }
                                        }
                                    }

                                }

                            }
                if(bestCardToDrop == null){
                    bestCardToDrop = toDraw;
                }
                /*
                System.out.print("Cards in hand: ");
                printCards(hand);
                System.out.print("Cards on table size: ");
                printCards(cardsOnTable);
                System.out.print("To drop: ");
                for(Integer index : worstTupleFromHand){
                    System.out.print(hand.get(index).getName() + ", ");
                }
                System.out.println();
                System.out.print("To take: ");
                for(Integer index : bestTupleFromTable){
                    System.out.print(cardsOnTable.get(index).getName() + ", ");
                }
                System.out.println();


                 */
            }



        //System.out.print("Cards in hand: " + hand.size() + " ");
        //System.out.println("Best card to drop: " + bestCardToDrop.getName() + " ");
        hand.remove(bestCardToDrop);
            /*
        System.out.println("Cards in hand at the end of performMove: ");
        printCards(hand);

             */
        int countedScoreAfterTakingCard = getCacheMap().getValue(hand);
        if(countedScoreAfterTakingCard > -998) {

        } else {
            countedScoreAfterTakingCard = sc.countScore(hand, cardsOnTable, true);
            cacheMap.putValue(hand,countedScoreAfterTakingCard);
        }
        this.score = countedScoreAfterTakingCard;
        /*
        System.out.println(this.score);
        System.out.println();

         */
        scoresInRounds.add(this.score);
        return bestCardToDrop;
    }

    private void printCards(ArrayList<Card> cards){
        for(Card c : cards){
            System.out.print(c.getName() + ", ");
        }
        System.out.println();
    }

    public Card performMove(ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        if(insight > 1){
            // We must try more cards from the table
            return findMaxWithInsight(cardsOnTable);
        } else{


            long startTime = System.nanoTime();
            //System.out.println("Counting performMove");
            /*
            for(Card c : hand){
                System.out.print(c.getNameLoc("cs") + ", ");
            }
            System.out.println();
            System.out.print("Table: ");
            for(Card c : cardsOnTable){
                System.out.print(c.getNameLoc("cs") + ", ");
            }
            System.out.println();
             */
            //We cant continue playing when 10 cards are on table
            if(cardsOnTable.size() == server.END_GAME_NUMBER_OF_CARDS){
                return null;
            }
            numberOfRoundsPlayed++;
            // resetting choice
            bestCardToTake = null;
            bestCardToDrop = null;
            List<Card> newTable = new ArrayList<>();
            //System.out.println("After innitial in perfromMOve");

            // Copy the original hand to not get some maluses or interactives deleted while computing the original score
            HandCloner hc = new HandCloner();
            List<Card> newHandOldScore = hc.cloneHand(null, hand);

            //System.out.println("After copying the hand 1st");
            int currentScore;
            // Count if there is gain from the table greater than the gainThreshold and keep the highest combination of them
            ScoreCounterForAI sc = new ScoreCounterForAI();
            sc.setServer(server);
            int score = cacheMap.getValue(newHandOldScore);
            if(score > -998) {
                currentScore = score;
            } else {
                currentScore = sc.countScore(newHandOldScore, cardsOnTable, true);
                cacheMap.putValue(newHandOldScore, currentScore);
                //System.out.print("Players cards on hand before deciding what to do [score: " + currentMaxScore + "]: ");
            }
            int currentMaxScore = currentScore;
            /*
            for(Card c: hand){
                //System.out.print(c.name + ", ");
            }
            */
            //System.out.println("After counting the score of the hand 1st");
            //System.out.println();

            //System.out.println("------------------------------NOVE ZKOUSENI NEJLEPSI RUKY-----------------------------");

            if(!cardsOnTable.isEmpty()) {
                // Try to change all combination of cards in hand and place one card from table instead
                for (Card cardToChange : hand) {
                    //System.out.println("Changing card on hand: " + cardToChange.getNameLoc("cs"));
                    // Copy all cards except the one that should be changed
                    // List<Card> newHand = new ArrayList<>(hand.stream().filter(card -> !card.equals(cardToChange)).collect(Collectors.toList()));
                    List<Card> newHand = cloneHand(cardToChange);

                    for (Card cardOnTable : cardsOnTable) {
                        //System.out.println("Changing card on table: " + cardOnTable.getNameLoc("cs"));


                        Card cardToTakeFromTable = cloneCard(cardOnTable);
                        newHand.add(cardToTakeFromTable);
                        // Count the resulting score, add the cardToChange to table for purposes of Necromancer unique ability
                        // TODO : Count the currentScore of the newHand array === IMPLEMENT PROPERLY sc.COUNTSCORE()
                        newTable.clear();
                        newTable = cardsOnTable.stream().filter(card -> !card.equals(cardOnTable)).collect(Collectors.toList());
                        //System.out.println("Pocet karet na stole pred polozenim jedne karty z ruky: " + newTable.size() + " pocet karet na ruce " + hand.size() + " pocet karet na NOVE ruce: " + newHand.size());

                        Card cardToChangeFromHand = new Card(cardToChange.id, cardToChange.name, cardToChange.strength, cardToChange.type,
                                cardToChange.bonuses, cardToChange.maluses, cardToChange.interactives);
                        newTable.add(cardToChangeFromHand);
                        //System.out.println("Karty na novem stole po vybrani karty do ruky: ");
                        /*
                        for (Card cardOnTableToList : newTable) {
                            //System.out.print(cardOnTableToList.name + ", ");
                        }
                        //System.out.println("----------------------------------------------------------------------");
                        //System.out.print("Karty na nove ruce: ");
                        for (Card c : newHand) {
                            //System.out.print(c.name + ", ");
                        }

                         */
                        //System.out.println();
                        //System.out.println("Pocet karet na stole: " + newTable.size() + " pocet karet na ruce " + hand.size() + " pocet karet na NOVE ruce: " + newHand.size());

                        // Count the score with the new cards on hand and on table
                        score = cacheMap.getValue(newHand);
                        if(score > -998){
                            currentScore = score;
                        } else {
                            currentScore = sc.countScore(newHand, newTable, true);
                            //System.out.println("After counting score in performMove after selecting a card in foreachCardInHandLoop");
                            //System.out.print("DANG I have to compute this one, its " + currentScore + ": "); key.forEach(id -> System.out.print(id + ", "));System.out.println();
                            cacheMap.putValue(newHand,currentScore);
                        }
                        if (currentScore > currentMaxScore + gainThreshold) {
                            currentMaxScore = currentScore;
                            this.score = currentScore;
                            bestCardToTake = cardOnTable;
                            bestCardToDrop = cardToChange;
                        }
                        //Take the card back so another one can be tested
                        newTable.remove(cardToChangeFromHand);
                        newHand.remove(cardToTakeFromTable);

                    }
                }
            } else{
                //System.out.println("Table is empty");
            }

            // Finally performing actions: take card from table if good, otherwise draw card
            if(bestCardToTake != null){
                server.takeCardFromTable(bestCardToTake);
                hand.add(bestCardToTake);
            } else{
                //System.out.println("AI is going to draw a card from the deck");
                // The AI draws the card in the moment when no card on the table offers increase of the score
                Card cardFromDeck = server.drawCardFromDeck();
                //System.out.println("AI drew " + cardFromDeck.getNameLoc("cs"));
                //System.out.println("AI drew card from the deck: " + cardFromDeck.name);
                // Now the AI must decide whether drop the card immediately or it gives better score
                for(Card cardToChange : hand){
                    //System.out.println("AI drew and is now changing card in hand: " + cardToChange.getNameLoc("cs"));
                    ArrayList<Card> newHand = cloneHand(cardToChange);
                    Card copiedCardFromTable = cloneCard(cardFromDeck);
                    newHand.add(copiedCardFromTable);
                        // Count the resulting score, add the cardToChange to table for purposes of Necromancer unique ability
                        // TODO : Count the currentScore of the newHand array === IMPLEMENT PROPERLY sc.COUNTSCORE()
                        newTable = cardsOnTable;
                        newTable.add(cardToChange);
                        //System.out.println("Pocet karet na stole: " + newTable.size() + " pocet karet na ruce " + hand.size());
                       //System.out.println("AI drew and is now changing card in hand: " + cardToChange.getNameLoc("cs") + " and counting score with " + copiedCardFromTable.getNameLoc("cs"));

                        currentScore = sc.countScore(newHand, newTable, true);
                        //System.out.print("DANG I have to compute this one, its " + currentScore + ": "); key.forEach(id -> System.out.print(id + ", "));System.out.println();
                        //System.out.println("AI drew and is now changing card in hand: " + cardToChange.getNameLoc("cs") + " and is done counting score with " + copiedCardFromTable.getNameLoc("cs") + " " + currentScore);

                        cacheMap.putValue(newHand, currentScore);

                    newTable.remove(cardToChange);
                    if(currentScore > currentMaxScore + gainThreshold){
                        this.score = currentScore;
                        currentMaxScore = currentScore;
                        bestCardToDrop = cardToChange;
                    }
                }
                if(bestCardToDrop == null){
                    bestCardToDrop = cardFromDeck;
                }
                    hand.add(cardFromDeck);
                cacheMap.manageNecromancer();
            }

            // Drop a card = if there is a best card to drop, drop it. If not, first card in hand will be dropped.
            if(bestCardToDrop == null){
                bestCardToDrop = hand.get(0);
            }
            hand.remove(bestCardToDrop);
            /*
            System.out.print("Players cards on hand [score: " + currentMaxScore + "]: ");
            for(Card c: hand){
                System.out.print(c.name + ", ");
            }
            System.out.println();
            System.out.println(bestCardToDrop.getNameLoc("cs"));


             */

            long elapsedTime = (System.nanoTime() - startTime) / 1000000;
            //System.out.println("Total execution time spent in performMove in millis: " + elapsedTime/1000000);
            server.timeSpentInMakeMoveGrredyPlayer.append(elapsedTime);
            server.timeSpentInMakeMoveGrredyPlayer.append("\n");

            //manageCache(cardsOnTable, bestCardToDrop);
            //System.out.println(bestCardToDrop.getNameLoc("cs"));
            if(server.getNeedDelay()){
                System.out.println("Waiting till 5000ms");
                long elapsedTimeInMili = (System.nanoTime() - startTime)/1000000;
                while(elapsedTimeInMili < 5000){
                    elapsedTimeInMili = (System.nanoTime() - startTime)/1000000;
                }
            }
            scoresInRounds.add(currentMaxScore);
            return bestCardToDrop;
            //server.setNextPlayer();
        }
    }

    private Card cloneCard(Card cardOnTable) throws CloneNotSupportedException {
        ArrayList<Malus> malusesForTableCard = new ArrayList<>();
        ArrayList<Interactive> interactivesForTableCard = new ArrayList<>();
        if (cardOnTable.maluses != null) {
            //System.out.println("Klonuji kartu " + cardOnTable.name);
            for (Malus m : cardOnTable.maluses) {
                malusesForTableCard.add(m.clone());
            }
        }
        // The same rule apply to interactives = they can be deleted to signal it has been used
        if (cardOnTable.interactives != null) {
            for (Interactive inter : cardOnTable.interactives) {
                interactivesForTableCard.add(inter.clone());
            }
        }
        return new Card(cardOnTable.id, cardOnTable.name, cardOnTable.strength, cardOnTable.type,
                cardOnTable.bonuses, malusesForTableCard, interactivesForTableCard);
    }

    private ArrayList<Card> cloneHand(Card cardToChange) throws CloneNotSupportedException {
        ArrayList<Card> newHand = new ArrayList<>();
        for(Card cardOnHand : hand){
            if(cardOnHand != cardToChange){
                ArrayList<Bonus> bonuses = new ArrayList<>();
                ArrayList<Malus> maluses = new ArrayList<>();
                ArrayList<Interactive> interactives = new ArrayList<>();
                // bonuses are never deleted, they can be the same objects
                // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
                if(cardOnHand.maluses != null){
                    //System.out.println("Klonuji kartu " + cardOnHand.name);
                    for(Malus m : cardOnHand.maluses){
                        maluses.add(m.clone());
                    }
                }
                // The same rule apply to interactives = they can be deleted to signal it has been used
                if(cardOnHand.interactives != null){
                    for(Interactive inter : cardOnHand.interactives){
                        interactives.add(inter.clone());
                    }
                }
                newHand.add(new Card(cardOnHand.id,cardOnHand.name,cardOnHand.strength, cardOnHand.type, cardOnHand.bonuses, maluses, interactives));
            }
        }
        return newHand;
    }

    @Override
    public void endGame(){
        countScore();
       //System.out.println("After countScore in GreedyPlayer");
    }

    @Override
    public void sendNamesInOrder(String s) {
        //System.out.println(s);
        String[] message = s.split("#");
        // 0 place is NAMES, start from 1 - which is this players name
        if (message[0].startsWith("$&$START$&$")) {
            try {
                server.putCardOnTable(performMove(server.cardsOnTable));
            } catch(CloneNotSupportedException notCloneableEx){
                //System.out.println("Not cloneable");
            }
        }
    }

    @Override
    public void countScore() {
        // Create different hands by interactives that AI has
        ScoreCounterForAI sc = new ScoreCounterForAI();
        sc.setServer(server);
        HandCloner hc = new HandCloner();
        try {
            storedHand = hc.cloneHand(null, hand);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        score = sc.countScore(hand, server.cardsOnTable, false);
        hand = storedHand;
        while (score < -999) {
            // wait;
        }
        server.increaseCountedScoreNumber();
    }
}
