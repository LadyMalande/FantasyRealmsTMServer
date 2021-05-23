package artificialintelligence;

import server.Card;
import server.PlayerOrAI;
import server.Server;
import util.HandCloner;
import util.StateMapCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The GreedyPlayer represents the simplest player which takes to consideration only 1 card in advance and also
 * parametrized one that can take more cards into consideration.
 * WARNING: The greater the insight is, the longer it takes the player to count its move.
 * @author Tereza Miklóšová
 */
public class GreedyPlayer implements ArtificialIntelligenceInterface, PlayerOrAI{
    /**
     * The List of cards in agents hand.
     */
    ArrayList<Card> hand;

    /**
     * The insight of the greedy player. Default value is 1. So it sees only 1 card for exchange.
     */
    int insight;

    /**
     * Stores hand at the end of the game for computing better combination of cards given the table cards too.
     */
    ArrayList<Card> storedHand;

    /**
     * Represents the best card to take from table. If null at certain point, the player DRAWS from the deck.
     */
    Card bestCardToTake;

    /**
     * The card that the plaer has decided is not present in his best hand.
     */
    Card bestCardToDrop;

    /**
     * Server to communicate with.
     */
    Server server;

    /**
     * Represents the best possible score the agent can get at that moment.
     */
    int bestPossibleScore;

    /**
     * Represents the threshold at which the player takes the card if it gives +threshold to score if the
     * old score is taken into account.
     */
    int gainThreshold;

    /**
     * Final ranking of the agent.
     */
    int rank;

    /**
     * Name of the agent to differentiate between them when they interact.
     */
    String name;

    /**
     * Number of rounds played. Used for statistics.
     */
    private int numberOfRoundsPlayed;

    /**
     * String of names of beginning cards that agent got at the beginning of the game. Used for the statistics.
     */
    String beginningHandCards;

    /**
     * Score of the beginning hand. Used for statistics.
     */
    int beginningHandScore;

    /**
     * True if the agent is currently playing.
     */
    boolean playing = false;

    /**
     * Cache for storing already computed values of card combinations.
     */
    CacheMap cacheMap;

    StringBuilder scoreTable;

    /**
     * Scores in rounds from start to the end. Used for representative and statistic purposes.
     */
    ArrayList<Integer> scoresInRounds = new ArrayList<>();

    /**
     * Agent's score at the end of the game and in the flow of the game.
     */
    int score = 0;

    /**
     * The best hand the player could build from the hand and the cards on the table. Changing up to 3 cards
     * from hand to achieve it. Used for statistical purposes.
     */
    ArrayList<Card> bestHandAfterTheEnd;

    /**
     * The score of the best hand that the agent assembled after the end of the game. Used for statistical purposes.
     */
    int bestScoreAfterTheEnd;

    /**
     * The number of cards the agent had to change in order to achieve his best hand after the end of the game.
     * Used for statistical purposes.
     */
    int numberOfChangedCardsInIdealHand;

    /**
     * Get {@link GreedyPlayer#storedHand}
     * @return {@link GreedyPlayer#storedHand}
     */
    @Override
    public ArrayList<Card> getStoredHand() {
        return storedHand;
    }

    /**
     * Set {@link GreedyPlayer#bestHandAfterTheEnd}
     * @return {@link GreedyPlayer#bestHandAfterTheEnd}
     */
    public ArrayList<Card> getBestHandAfterTheEnd() {
        return bestHandAfterTheEnd;
    }

    /**
     * Set {@link GreedyPlayer#bestHandAfterTheEnd}
     * @param bestHandAfterTheEnd {@link GreedyPlayer#bestHandAfterTheEnd}
     */
    public void setBestHandAfterTheEnd(ArrayList<Card> bestHandAfterTheEnd) {
        this.bestHandAfterTheEnd = bestHandAfterTheEnd;
    }

    /**
     * Get {@link GreedyPlayer#bestScoreAfterTheEnd}
     * @return {@link GreedyPlayer#bestScoreAfterTheEnd}
     */
    public int getBestScoreAfterTheEnd() {
        return bestScoreAfterTheEnd;
    }

    /**
     * Set {@link GreedyPlayer#bestScoreAfterTheEnd}
     * @param bestScoreAfterTheEnd {@link GreedyPlayer#bestScoreAfterTheEnd}
     */
    public void setBestScoreAfterTheEnd(int bestScoreAfterTheEnd) {
        this.bestScoreAfterTheEnd = bestScoreAfterTheEnd;
    }

    /**
     * Get {@link GreedyPlayer#numberOfChangedCardsInIdealHand}
     * @return {@link GreedyPlayer#numberOfChangedCardsInIdealHand}
     */
    public int getNumberOfChangedCardsInIdealHand() {
        return numberOfChangedCardsInIdealHand;
    }

    /**
     * Set {@link GreedyPlayer#numberOfChangedCardsInIdealHand}
     * @param numberOfChangedCardsInIdealHand {@link GreedyPlayer#numberOfChangedCardsInIdealHand}
     */
    public void setNumberOfChangedCardsInIdealHand(int numberOfChangedCardsInIdealHand) {
        this.numberOfChangedCardsInIdealHand = numberOfChangedCardsInIdealHand;
    }

    /**
     * Get {@link GreedyPlayer#cacheMap}
     * @return {@link GreedyPlayer#cacheMap}
     */
    public CacheMap getCacheMap() {
        return cacheMap;
    }

    /**
     * Get {@link GreedyPlayer#scoresInRounds}
     * @return {@link GreedyPlayer#scoresInRounds}
     */
    @Override
    public ArrayList<Integer> getScoresInRound(){
        return scoresInRounds;
    }

    /**
     * Get {@link GreedyPlayer#hand}
     * @return {@link GreedyPlayer#hand}
     */
    @Override
    public ArrayList<Card> getHand(){
        return hand;
    }

    /**
     * Get {@link GreedyPlayer#name}
     * @return {@link GreedyPlayer#name}
     */
    @Override
    public String getName(){
        return this.name;
    }


    /**
     * Set Detailed score table.
     * @param sb Detailed score table.
     */
    @Override
    public void setScoreTable(StringBuilder sb) {}

    /**
     * Get detailed score table.
     * @return Detailed score table.
     */
    @Override
    public StringBuilder getScoreTable() {
        return scoreTable;
    }

    /**
     * Set {@link GreedyPlayer#playing}
     * @param playing {@link GreedyPlayer#playing}
     */
    @Override
    public void setPlaying(boolean playing){this.playing = playing;}

    /**
     * Get {@link GreedyPlayer#playing}
     * @return {@link GreedyPlayer#playing}
     */
    @Override
    public boolean getPlaying(){
        return this.playing;
    }

    /**
     * Get {@link GreedyPlayer#numberOfRoundsPlayed}
     * @return {@link GreedyPlayer#numberOfRoundsPlayed}
     */
    @Override
    public int getNumberOfRoundsPlayed(){
        return numberOfRoundsPlayed;
    }

    /**
     * Get {@link GreedyPlayer#rank}
     * @return {@link GreedyPlayer#rank}
     */
    @Override
    public int getRank(){return this.rank;}

    /**
     * Get {@link GreedyPlayer#score}
     * @return {@link GreedyPlayer#score}
     */
    @Override
    public int getScore(){return this.score;}

    /**
     * Set {@link GreedyPlayer#rank}
     * @param r {@link GreedyPlayer#rank}
     */
    @Override
    public void setRank(int r){this.rank = r;}

    /**
     * Set {@link GreedyPlayer#score}
     * @param s {@link GreedyPlayer#score}
     */
    @Override
    public void setScore(int s){this.score = s;}

    /**
     * Constructor for Greedy player.
     * @param server Server to communicate with.
     * @param gainThreshold The threshold for the insight and for taking cards.
     * @param name Name of the agent.
     * @throws CloneNotSupportedException Thrown if the cloning of the cards went wrong.
     */
    public GreedyPlayer(Server server, int gainThreshold, String name) throws CloneNotSupportedException {
        this.name = name;
        hand = new ArrayList<>();
        this.server = server;
        bestPossibleScore = 0;
        bestCardToTake = null;
        bestCardToDrop = null;
        numberOfRoundsPlayed = 0;
        this.gainThreshold = gainThreshold;
        this.insight = gainThreshold;
        getInitCards();
        this.cacheMap = new CacheMap();
        bestHandAfterTheEnd = new ArrayList<>();
        bestScoreAfterTheEnd = -999;
    }

    /**
     * Gets 7 initial cards from the server. Clears the values in case it has been in the experimental loop.
     * @throws CloneNotSupportedException Throws if the cards had difficulties with cloning properties.
     */
    @Override
    public void getInitCards() throws CloneNotSupportedException {
        hand.clear();
        numberOfRoundsPlayed = 0;
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < Server.CARDS_ON_HAND; j++) {
            Card newCard = server.getDeck().getDeck().get(0);
            hand.add(newCard);
            sb.append(newCard.getName()).append(";");
            server.getDeck().getDeck().remove(newCard);
        }
        beginningHandCards = sb.toString();
        HandCloner hc = new HandCloner();
        List<Card> newHandOldScore = hc.cloneHand(null, hand);

        // Count if there is gain from the table greater than the gainThreshold and keep the highest combination of them
        ScoreCounterForAI sc = new ScoreCounterForAI(server);
        sc.setServer(server);
        beginningHandScore = sc.countScore(newHandOldScore, new ArrayList<>(), true);
        this.score = beginningHandScore;
        bestHandAfterTheEnd = new ArrayList<>();
        bestScoreAfterTheEnd = -999;
        scoresInRounds = new ArrayList<>();
        scoreTable = new StringBuilder();
        this.cacheMap = new CacheMap();
        scoresInRounds.add(this.score);
    }

    /**
     * Do not learn. Greedy player doesn't need to learn anything.
     */
    @Override
    public void learn() {
        // Greedy player doesn't learn
    }

    /**
     * Agent doesn't have to send scores anywhere.
     * @param s String with score.
     */
    @Override
    public void sendScore(String s) {
        // The agent doesn't need to send scores anywhere.
    }


    /**
     * Makes an array of numbers depending on the highest number suggested in size.
     * @param size The maximum number of the array row.
     * @return Array with values from 0 to size - 1.
     */
    private int[] tableIndexInitializer(int size){
        int[] arr = new int[size];
        for(int i = 0; i < size; i++){
            arr[i] = i;
        }
        return arr;
    }

    /**
     * Computes the best actions given the maximum number of cards it can switch in hand.
     * @param cardsOnTable Cards on table it can use.
     * @return Card to drop on the table.
     */
    public Card findMaxWithInsight(ArrayList<Card> cardsOnTable) {
        int[] handIndexes = {0,1,2,3,4,5,6};
        int[] tableIndexes = tableIndexInitializer(cardsOnTable.size());
        ScoreCounterForAI sc = new ScoreCounterForAI(server);
            int maxScore = getScore();
        ArrayList<Integer> worstTupleFromHand = new ArrayList<>();
            ArrayList<Integer> bestTupleFromTable = new ArrayList<>();
            HandCloner hc = new HandCloner();
            bestCardToDrop = null;
            bestCardToTake = null;
        if(!cardsOnTable.isEmpty()){
            for(int i = 1; i < insight + 1; i++){
                //Switch this number of cards
                StateMapCreator smc = new StateMapCreator(new int[1], 0,0);

                ArrayList<ArrayList<Integer>> tuplesInHand = smc.makeNTuples(handIndexes,i);
                ArrayList<ArrayList<Integer>> tuplesOnTable = smc.makeNTuples(tableIndexes,i);
                for(ArrayList<Integer> tupleInHand : tuplesInHand){
                    for(ArrayList<Integer> tupleOnTable : tuplesOnTable){
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
                            int currentScore;

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
            if(bestCardToTake != null) {
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
                hand.add(toDraw);
                        // We are trying to drop i cards from hand and replace them with i-1 cards from the table
                        for(int i = 1; i < insight; i++) {
                            //This time only look one less to the future since we already chose to draw a card
                            //Switch this number of cards
                            StateMapCreator smc = new StateMapCreator(new int[1], 0, 0);
                            int[] handIndexes2 = {0,1,2,3,4,5,6,7};
                            ArrayList<ArrayList<Integer>> tuplesInHand = smc.makeNTuples(handIndexes2,i);
                            ArrayList<ArrayList<Integer>> tuplesOnTable = smc.makeNTuples(tableIndexes,i-1);
                            if(tuplesOnTable != null) {
                                for (ArrayList<Integer> tupleInHand : tuplesInHand) {
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
                                            int currentScore;

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
                                    for(Card toChangeInHand : hand){
                                        if(toChangeInHand != toDraw){
                                            try {
                                                ArrayList<Card> newHand = hc.cloneHand(toChangeInHand, hand);
                                                cardsOnTable.add(toChangeInHand);
                                                ArrayList<Card> newTable = hc.cloneHand(null, cardsOnTable);
                                                int currentScore;

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
            }
        hand.remove(bestCardToDrop);

        int countedScoreAfterTakingCard = getCacheMap().getValue(hand);
        if(countedScoreAfterTakingCard < -998) {
            countedScoreAfterTakingCard = sc.countScore(hand, cardsOnTable, true);
            cacheMap.putValue(hand,countedScoreAfterTakingCard);
        }
        this.score = countedScoreAfterTakingCard;
        scoresInRounds.add(this.score);
        return bestCardToDrop;
    }

    /**
     * Computes how to play the first action and the second action in round.
     * @param cardsOnTable Cards on the table used for calculating of this round.
     * @return Card to put down to table.
     * @throws CloneNotSupportedException Thrown if there were some difficulties with cloning properties on cards.
     */
    public Card performMove(ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        if(insight > 1){
            // We must try more cards from the table
            return findMaxWithInsight(cardsOnTable);
        } else{
            long startTime = System.nanoTime();

            //We cant continue playing when 10 cards are on table
            if(cardsOnTable.size() == Server.END_GAME_NUMBER_OF_CARDS){
                return null;
            }
            numberOfRoundsPlayed++;
            // resetting choice
            bestCardToTake = null;
            bestCardToDrop = null;
            List<Card> newTable = new ArrayList<>();
            // Copy the original hand to not get some maluses or interactives deleted while computing the original score

            int currentScore;
            // Count if there is gain from the table greater than the gainThreshold and keep the highest combination of them
            ScoreCounterForAI sc = new ScoreCounterForAI(server);
            sc.setServer(server);

            currentScore = this.score;
            int currentMaxScore = currentScore;

            HandCloner hc = new HandCloner();

            if(!cardsOnTable.isEmpty()) {
                // Try to change all combination of cards in hand and place one card from table instead
                for (Card cardToChange : hand) {
                    // Copy all cards except the one that should be changed
                    List<Card> newHand = hc.cloneHand(cardToChange, hand);

                    for (Card cardOnTable : cardsOnTable) {
                        Card cardToTakeFromTable = hc.cloneCard(cardOnTable);
                        newHand.add(cardToTakeFromTable);
                        // Count the resulting score, add the cardToChange to table for purposes of Necromancer unique ability

                        newTable.clear();
                        newTable = cardsOnTable.stream().filter(card -> !card.equals(cardOnTable)).collect(Collectors.toList());

                        Card cardToChangeFromHand = new Card(cardToChange.getId(), cardToChange.getName(),
                                cardToChange.getStrength(), cardToChange.getType(),
                                cardToChange.getBonuses(), cardToChange.getMaluses(), cardToChange.getInteractives());
                        newTable.add(cardToChangeFromHand);


                        // OPTIMIZED WITH CACHE
                        // Count the score with the new cards on hand and on table
                        score = cacheMap.getValue(newHand);
                        if(score > -998){
                            currentScore = score;
                        } else {
                            currentScore = sc.countScore(newHand, newTable, true);
                            cacheMap.putValue(newHand,currentScore);
                        }

                        /*
                        //NOT OPTIMIZED - without cache
                        currentScore = sc.countScore(newHand, newTable, true);
                        */
                        if (currentScore > currentMaxScore + gainThreshold) {
                            currentMaxScore = currentScore;
                            bestCardToTake = cardOnTable;
                            bestCardToDrop = cardToChange;
                        }

                        //Take the card back so another one can be tested
                        newTable.remove(cardToChangeFromHand);
                        newHand.remove(cardToTakeFromTable);

                    }
                }
            }

            // Finally performing actions: take card from table if good, otherwise draw card
            if(bestCardToTake != null){
                if(server.getNeedDelay()){
                    System.out.println("Waiting till 5000ms");
                    long elapsedTimeInMili = (System.nanoTime() - startTime)/1000000;
                    while(elapsedTimeInMili < 5000){
                        elapsedTimeInMili = (System.nanoTime() - startTime)/1000000;
                    }
                }
                server.takeCardFromTable(bestCardToTake);
                hand.add(bestCardToTake);
            } else{
                // The AI draws the card in the moment when no card on the table offers increase of the score
                Card cardFromDeck = server.drawCardFromDeck();
                // Now the AI must decide whether drop the card immediately or it gives better score
                for(Card cardToChange : hand){
                    ArrayList<Card> newHand = hc.cloneHand(cardToChange, hand);
                    Card copiedCardFromTable = hc.cloneCard(cardFromDeck);
                    newHand.add(copiedCardFromTable);
                        // Count the resulting score, add the cardToChange to table for purposes of Necromancer unique ability
                        newTable = cardsOnTable;
                        newTable.add(cardToChange);

                        // WE must count the score because we drew a NEW card
                        currentScore = sc.countScore(newHand, newTable, true);

                        cacheMap.putValue(newHand, currentScore);
                    newTable.remove(cardToChange);
                    if(currentScore > currentMaxScore + gainThreshold){
                        currentMaxScore = currentScore;
                        bestCardToDrop = cardToChange;
                    }
                }
                if(bestCardToDrop == null){
                    bestCardToDrop = cardFromDeck;
                }
                    hand.add(cardFromDeck);
            }
            // Drop a card = if there is a best card to drop, drop it. If not, first card in hand will be dropped.
            if(bestCardToDrop == null){
                bestCardToDrop = hand.get(0);
            }
            hand.remove(bestCardToDrop);

            long elapsedTime = (System.nanoTime() - startTime) / 1000000;
            server.getBufferForResult().appendToTimeSpentInMakeMoveGrredyPlayer(String.valueOf(elapsedTime));

            cacheMap.manageNecromancer();
            if(server.getNeedDelay()){
                System.out.println("Waiting till 5000ms");
                long elapsedTimeInMili = (System.nanoTime() - startTime)/1000000;
                if(bestCardToTake != null){
                    while(elapsedTimeInMili < 5000){
                        elapsedTimeInMili = (System.nanoTime() - startTime)/1000000;
                    }
                } else{
                    while(elapsedTimeInMili < 8000){
                        elapsedTimeInMili = (System.nanoTime() - startTime)/1000000;
                    }
                }

            }
            scoresInRounds.add(currentMaxScore);
            this.score = currentMaxScore;
            return bestCardToDrop;
        }
    }

    /**
     * This method is called after the server has declared the game has ended.
     * This agent just counts his final score and saves the value.
     */
    @Override
    public void endGame(){
        countScore();
    }

    /**
     * Doesn't do anything as the Greedy player doesn't send the names to anyone.
     * @param s string with names of other players.
     */
    @Override
    public void sendNamesInOrder(String s) {
    }

    /**
     * Counts the score of the agent's hand.
     */
    @Override
    public void countScore() {
        // Create different hands by interactives that AI has
        ScoreCounterForAI sc = new ScoreCounterForAI(server);
        sc.setServer(server);
        sc.setAgent(this);




        HandCloner hc = new HandCloner();
        try {
            storedHand = hc.cloneHand(null, hand);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        score = sc.countScore(hand, server.cardsOnTable, false);
        hand = storedHand;
        while (score < -999) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        server.increaseCountedScoreNumber();
        System.out.println("count score in Greedy Player");
    }

    /**
     * Nothing special implemented as the agent can use his reference to server to work with cards.
     * @param c Card to put on the table.
     */
    @Override
    public void putCardOnTable(Card c){
    }

    /**
     * Nothing special implemented as the agent can use his reference to server to work with cards.
     * @param c Card to erase from the table.
     */
    @Override
    public void eraseCardFromTable(Card c) {
    }
}
