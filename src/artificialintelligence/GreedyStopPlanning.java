package artificialintelligence;

import server.Card;
import server.PlayerOrAI;
import server.Server;
import util.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GreedyStopPlanning implements ArtificialIntelligenceInterface, PlayerOrAI{
        ArrayList<Card> hand;
        Card bestCardToTake;
        Card bestCardToDrop;
        Server server;
        int bestPossibleScore;
        int rank;
        String name;
        private int numberOfRoundsPlayed;
        boolean playing = false;
        boolean stateRecognized;
        double alpha; // learning coefficient
        double epsilon; // choose random action with this probability
        Map<Pair<Integer,Integer>,Coefficients> coefficientsMap;
        int currentActualValue;
        Map<int[], Integer> handValuesMap;
        Map<Integer, int[]> historyHandRound;
        CacheMap cacheMap;
        int insight;
        int round = 0;
        int score = 0;
        int storeInsight;
        StateStopPlanning stateChosenForThisRound;
        ArrayList<StateStopPlanning> stateMap;
        int takenSinceLastRound;
        /**
     * Scores in rounds from start to the end. Used for representative and statistic purposes.
     */
    ArrayList<Integer> scoresInRounds = new ArrayList<>();


        public GreedyStopPlanning(Server server, String name, int insight, double alpha, double epsilon){
            this.name = name;
            //System.out.println("Name of Learning " + this.name);
            hand = new ArrayList<>();
            this.server = server;
            this.insight = insight;
            bestPossibleScore = 0;
            bestCardToTake = null;
            storeInsight = insight;
            numberOfRoundsPlayed = 0;
            handValuesMap = new HashMap<>();
            historyHandRound = new HashMap<>();
            stateRecognized = false;
            //this.stateMap = makeStateMap();
            this.alpha = alpha;
            this.epsilon = epsilon;
            currentActualValue = 0;
            getInitCards();
            rank = 0;

        }

        @Override
        public ArrayList<Card> getHand(){
            return hand;
        }

        @Override
        public void sendNamesInOrder(String s) {

        }

        @Override
        public String getName(){
            return this.name;
        }

        @Override
        public void setScoreTable(StringBuilder sb) {

        }

        @Override
        public StringBuilder getScoreTable() {
            return null;
        }

        @Override
        public void endGame(){
            countScore();
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
        public int getRank(){return this.rank;}
        @Override
        public int getScore(){return this.score;}
        @Override
        public void setRank(int r){this.rank = r;}
        @Override
        public void setScore(int s){this.score = s;}

        @Override
        public void sendScore(String s){
            learn();
        }

        @Override
        public void getInitCards() {
            round = 0;
            this.stateMap = loadCoefficientsFromFile();
            handValuesMap.clear();
            historyHandRound.clear();
            hand.clear();
            numberOfRoundsPlayed = 0;
            for (int j = 0; j < server.CARDS_ON_HAND; j++) {
                Card newCard = server.getDeck().getDeck().get(0);
                hand.add(newCard);
                server.getDeck().getDeck().remove(newCard);
            }

            ScoreCounterForAI sc = new ScoreCounterForAI();
            sc.setServer(server);
            HandCloner hc = new HandCloner();
            List<Card> newHandOldScore = new ArrayList<>();
            try {
                newHandOldScore = hc.cloneHand(null, hand);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            int beginningHandScore = sc.countScore(newHandOldScore, new ArrayList<>(), true);
            this.score = beginningHandScore;
            scoresInRounds = new ArrayList<>();

            this.cacheMap = new CacheMap();
            this.insight = storeInsight;
            scoresInRounds.add(this.score);

            stateChosenForThisRound = null;
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

    private ArrayList<StateStopPlanning> makeStateMap(){
        ArrayList<StateStopPlanning> map = new ArrayList<>();
        for(int table = 0; table < 11; table++){
            for(int size = 2; size < 7; size++){
                for(int taken = 0; taken < size; taken++){
                    for(int scoreapprox = 0; scoreapprox < 40; scoreapprox++){
                        map.add(new StateStopPlanning(table, size, taken, scoreapprox, 0.5));
                    }
                }
            }
        }
        return map;
    }

    /**
     * Computes the best actions given the maximum number of cards it can switch in hand.
     * @param cardsOnTable Cards on table it can use.
     * @return Card to drop on the table.
     */
    public Card findMaxWithInsight(ArrayList<Card> cardsOnTable) {
        int[] handIndexes = {0,1,2,3,4,5,6};
        int[] tableIndexes = tableIndexInitializer(cardsOnTable.size());
        ScoreCounterForAI sc = new ScoreCounterForAI();
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

                            int score = cacheMap.getValue(newHand);
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
                        int scoreWithOutThis = cacheMap.getValue(betterToDrop);
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

                                int score = cacheMap.getValue(newHand);
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

                                int score = cacheMap.getValue(newHand);
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

        int countedScoreAfterTakingCard = cacheMap.getValue(hand);
        if(countedScoreAfterTakingCard < -998) {
            countedScoreAfterTakingCard = sc.countScore(hand, cardsOnTable, true);
            cacheMap.putValue(hand,countedScoreAfterTakingCard);
        }
        this.score = countedScoreAfterTakingCard;
        scoresInRounds.add(this.score);
        return bestCardToDrop;
    }

        @Override
        public ArrayList<Card> getStoredHand() {
            return null;
        }

        @Override
        public ArrayList<Integer> getScoresInRound() {
            return scoresInRounds;
        }

        /*
        private void recognizeState(ArrayList<Card> cardsOnTable){
                int handSize = hand.size();
                int tableSize = cardsOnTable.size();
                int[] currentState = new int[handSize + tableSize];
                List<Integer> listHand = hand.stream().map(Card::getId).collect(toList());
                List<Integer> listTable = cardsOnTable.stream().map(Card::getId).collect(toList());
                Collections.sort(listHand);
                Collections.sort(listTable);
                int[] handIDs = listHand.stream().mapToInt(i -> i).toArray();
                int[] tableIDs = listTable.stream().mapToInt(i -> i).toArray();
                System.arraycopy(handIDs,0,currentState,0,handSize);
                System.arraycopy(tableIDs,0,currentState,handSize,tableSize);
                stateRecognized = true;
            }


         */
        private StateStopPlanning getStateStopPlanning(int size, int table, int taken, int score ){
            try {
                List<StateStopPlanning> map = stateMap.stream().filter(state -> state.getNumberOfPlayers() == size &&
                        state.getNumberOfCardsOnTable() == table &&
                        state.getCardsTakenLastRound() == taken &&
                        state.getPlayerScoreApprox() == score).collect(Collectors.toList());
                if (map != null) {
                    return map.get(0);
                } else return null;
            } catch(IndexOutOfBoundsException ex){
                System.out.println("Table: " + table + " Size: " +  size + " taken: " + taken + " score: " + score);
            }
            return null;
        }

        @Override
        public Card performMove(ArrayList<Card> cardsOnTable){
            numberOfRoundsPlayed++;
            if(insight > 1) {
                if ((Math.random() <= 0.5)) {
                    insight = 1;
                    stateChosenForThisRound = getStateStopPlanning(cardsOnTable.size(), server.getPlayers().size(),
                            takenSinceLastRound, getScoreBoundaries());
                }
            }
            takenSinceLastRound = 0;
            return findMaxWithInsight(cardsOnTable);
        }

    /**
     * Tells the client to erase a card from the table.
     * @param c The card to put on the client's table.
     */
    @Override
    public void eraseCardFromTable(Card c){
        takenSinceLastRound++;
    }

        private int maxForPossibleActualValuesFromTable(){
            int cummulativeValue;
            int maxValue = currentActualValue;
            int[] ids = collectIDs(hand);

            TuplesMapCreator tmc = new TuplesMapCreator(ids, server);
            ArrayList<Integer> idsInHand = tmc.makeList(ids);

            for(Card cardInHand:hand){
                idsInHand.remove(cardInHand.getId());
                for(Card cardOnTable : server.cardsOnTable){
                    idsInHand.add(cardOnTable.getId());
                    ids = collectIDs(hand);
                    cummulativeValue = countActualValue(ids);
                    idsInHand.remove(cardOnTable.getId());

                    if(cummulativeValue > maxValue){
                        maxValue = cummulativeValue;
                        bestCardToDrop = cardInHand;
                        bestCardToTake = cardOnTable;
                    }
                }
                idsInHand.add(cardInHand.getId());

            }
            return maxValue;
        }

        private int checkForPossibleActualValuesFromDeck(){
            // We want to get the average value we get if we take card from deck = average of possibilities
            int cummulativeValue = 0;
            int numberOfPossibilities = 0;
            int maxValue;
            ArrayList<Card> canBeInDeck = server.getMightBeInDeck();
            canBeInDeck.removeAll(hand);
            ArrayList<Card> listForSearch = new ArrayList<>(hand);
            for(Card table : canBeInDeck){
                hand.add(table);
                maxValue = 0;
                for(Card c : listForSearch){
                    hand.remove(c);
                    int[] idsInHand = collectIDs(hand);
                    int valueInHand = countActualValue(idsInHand);
                    if(valueInHand > maxValue){
                        maxValue = valueInHand;
                        bestCardToDrop = c;
                    }

                    hand.add(c);

                }
                cummulativeValue += maxValue;
                numberOfPossibilities++;
                hand.remove(table);
            }
            return cummulativeValue / numberOfPossibilities;
        }

        private void checkForPossibleValuesInHand(){
            // 8 cards in hand, one must go out
            int maxValue = 0;
            ArrayList<Card> toBeSearched = new ArrayList<>(hand);
            for(Card c : toBeSearched){
                hand.remove(c);
                int[] idsInHand = collectIDs(hand);
                int valueInHand = countActualValue(idsInHand);
                if(valueInHand > maxValue){
                    maxValue = valueInHand;
                    bestCardToDrop = c;
                }
                hand.add(c);
            }
        }

        private int countActualValue(int[] ids){
            if(handValuesMap.containsKey(ids)){
                return handValuesMap.get(ids);
            }
            int cummulativeValue = 0;
            TuplesMapCreator tmc = new TuplesMapCreator(ids, server);

            ArrayList<Pair<Integer,Integer>> pairs = tmc.makeListOfPairs();
            for(Pair<Integer,Integer> pair : pairs){
                Coefficients co = coefficientsMap.get(pair);
                cummulativeValue += co.getActalValueCoefficient();
            }
            handValuesMap.put(ids, cummulativeValue);
            return cummulativeValue;
        }

        private double getPenaltyForLocalMinimum(){
            int lastRecord = scoresInRounds.size() - 1;
            int lastScore = scoresInRounds.get(lastRecord);
            int secondLast = scoresInRounds.get((lastRecord-1));
            if(lastScore < secondLast){
                return -0.2;
            } else{
                return 0.0;
            }
        }

        private int getScoreBoundaries(){
            if(score <= 0){
                return 0;
            }
            return score / 10;
        }

        @Override
        public void learn() {

        if(stateChosenForThisRound == null){
            stateChosenForThisRound = getStateStopPlanning(server.getPlayers().size(), 10,  takenSinceLastRound, getScoreBoundaries());

        }
            double reward = 0.0;
            if(rank == 1){
                reward = 1.0;
            }
            reward += getScoreBoundaries() * 0.001;
            reward += getPenaltyForLocalMinimum();

            double oldValue = stateChosenForThisRound.getWinrate();
            double newValue = oldValue + (alpha * (reward - oldValue));
            System.out.println("State old value: " + oldValue + " new value: " + newValue);
            stateChosenForThisRound.setWinrate(newValue);
            System.out.println(stateChosenForThisRound.getWinrate());
            if(server.ithRound % 100 == 0){
                writeValuesToFile();
            }
            writeValuesToFile();
        }

        private void writeValuesToFile(){
            ExperimentOutputCreator ex = new ExperimentOutputCreator(server.getPlayers());
            ex.writeCoefficientsStateStopPlanning(ex.createOutputFile("map_statesStopPlanning"),stateMap);
        }

        private ArrayList<StateStopPlanning> loadCoefficientsFromFile(){
            ArrayList<StateStopPlanning> map = new ArrayList<>();
            int number = this.server.getPlayers().size();
            //System.out.println("1");
            try{
                //System.out.println("2");

                FileReader r = new FileReader("map_statesStopPlanning.txt");
                //System.out.println("3");

                BufferedReader fr = new BufferedReader(r);
                //System.out.println("4");

                String line;
                // Read objects
                //System.out.println("5");

                while((line = fr.readLine()) != null){
                    String[] separated = line.split(";");
                    map.add(new StateStopPlanning(Integer.parseInt(separated[0]),Integer.parseInt(separated[1]),
                            Integer.parseInt(separated[2]),Integer.parseInt(separated[3]),Double.parseDouble(separated[4])));
                }
                //System.out.println("6");


                fr.close();
            } catch (IOException e) {
                System.out.println(e.getStackTrace());
                System.out.println("Error initializing stream while loading");
                return null;
            }

            return map;
        }

        private int[] collectIDs(ArrayList<Card> list){
            int[] output = new int[list.size()];

            for(int i = 0; i< list.size(); i++){
                output[i] = list.get(i).getId();
            }
            Arrays.sort(output);
            return output;
        }

        private boolean performRandomMove(){
            Random randomNum = new Random();
            int rand = 1 + randomNum.nextInt(100);
            double result = rand /100.0;
            return result <= epsilon;
        }

        @Override
        public void countScore() {
            // Create different hands by interactives that AI has
            ScoreCounterForAI sc = new ScoreCounterForAI();
            score = sc.countScore(hand, server.cardsOnTable, false);

            while (score < -999) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            server.increaseCountedScoreNumber();
        }

        @Override
        public void putCardOnTable(Card c){

        }


}
