package artificialintelligence;

import server.Card;
import server.PlayerOrAI;
import server.Server;
import util.ExperimentOutputCreator;
import util.Pair;
import util.TuplesMapCreator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LearningPlayer implements ArtificialIntelligenceInterface, PlayerOrAI{
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
        //Map<Pair<Integer,Integer>,Coefficients> coefficientsMap;
        Map<Pair<Integer,Integer>,Double> coefficientsMap;
        Map<Integer, Double> cardsCoefficientsMap;
        double currentActualValue;
        Map<int[], Double> handValuesMap;
        Map<Integer, int[]> historyHandRound;
        ArrayList<Integer> scoresInRound;
        int round = 0;
        int score = 0;


    public LearningPlayer(Server server, String name, double alpha, double epsilon){
            this.name = name;
            //System.out.println("Name of Learning " + this.name);
            hand = new ArrayList<>();
            this.server = server;
            bestPossibleScore = 0;
            bestCardToTake = null;
            numberOfRoundsPlayed = 0;
            handValuesMap = new HashMap<>();
            historyHandRound = new HashMap<>();
            stateRecognized = false;
            TuplesMapCreator tmc = new TuplesMapCreator(arrayCreatorForRow(54), server);
            coefficientsMap = loadPairCoefficientsFromFile("map_coefficients");
            cardsCoefficientsMap = loadCardCoefficientsFromFile("card_coefficients");
            if(coefficientsMap == null){
                coefficientsMap = tmc.makePairCoefficientsMap();
            }
            //System.out.println("Size of pairMap: " + coefficientsMap.size());
            if(cardsCoefficientsMap == null){
                cardsCoefficientsMap = tmc.makeCardCoefficientsMap();
            }
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
    public void getInitCards(){
        round = 0;
        handValuesMap.clear();
        historyHandRound.clear();
        scoresInRound = new ArrayList<>();
        hand.clear();
        numberOfRoundsPlayed = 0;
        for (int j = 0; j < server.CARDS_ON_HAND; j++) {
            Card newCard = server.getDeck().getDeck().get(0);
            hand.add(newCard);
            server.getDeck().getDeck().remove(newCard);
        }
        /*
        for(Map.Entry<Pair<Integer,Integer>,Double> entry :coefficientsMap.entrySet()){
            System.out.println("EntryInMap: " + entry.getKey().getKey() + "," + entry.getKey().getValue() + "=" + entry.getValue());
        }

         */
            currentActualValue = countActualValue(collectIDs(hand));
            historyHandRound.put(round, collectIDs(hand));
            ScoreCounterForAI sc = new ScoreCounterForAI();
            scoresInRound.add(sc.countScore(hand, server.cardsOnTable, true));
        }

    @Override
    public ArrayList<Card> getStoredHand() {
        return null;
    }

    @Override
    public ArrayList<Integer> getScoresInRound() {
        return scoresInRound;
    }

    int[] arrayCreatorForRow(int numberOfElements){
        int[] array = new int[numberOfElements];
        for(int i = 1; i <= numberOfElements; i++){
            array[i-1] = i;
        }
        return array;
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
    @Override
    public Card performMove(ArrayList<Card> cardsOnTable){
        if (performRandomMove()) {
            Random randomMove = new Random();
            int move = randomMove.nextInt(server.cardsOnTable.size() + 1);
            if(move < server.cardsOnTable.size()){
                hand.add(server.cardsOnTable.get(move));
                server.cardsOnTable.remove(move);
            }else{
                hand.add(server.drawCardFromDeck());
            }
            move = randomMove.nextInt(hand.size());
            Card toDrop = hand.get(move);
            hand.remove(toDrop);
            ScoreCounterForAI sc = new ScoreCounterForAI();
            scoresInRound.add(sc.countScore(hand, server.cardsOnTable, true));
            return toDrop;
        } else{
            double handActualValue = currentActualValue;

            double maxFromTable = maxForPossibleActualValuesFromTable();

            double avgFromDeck = checkForPossibleActualValuesFromDeck();
            if(handActualValue < maxFromTable || handActualValue < avgFromDeck){
                if(maxFromTable < avgFromDeck){
                    hand.add(server.drawCardFromDeck());
                    checkForPossibleValuesInHand();
                } else{
                    hand.add(bestCardToTake);
                    server.cardsOnTable.remove(bestCardToTake);
                }
            }

            hand.remove(bestCardToDrop);

            round++;
            historyHandRound.put(round, collectIDs(hand));
        }
        ScoreCounterForAI sc = new ScoreCounterForAI();
        scoresInRound.add(sc.countScore(hand, server.cardsOnTable, true));
        numberOfRoundsPlayed++;
        return bestCardToDrop;
    }

    private double maxForPossibleActualValuesFromTable(){
        double cummulativeValue;
        double maxValue = currentActualValue;
        int[] ids = collectIDs(hand);

        TuplesMapCreator tmc = new TuplesMapCreator(ids, server);
        ArrayList<Integer> idsInHand = tmc.makeList(ids);

        for(Card cardInHand:hand){
            idsInHand.removeIf(value -> value == cardInHand.getId());
            for(Card cardOnTable : server.cardsOnTable){
                idsInHand.add(cardOnTable.getId());
                ids = collectIDs(hand);
                cummulativeValue = countActualValue(ids);
                idsInHand.removeIf(value -> value == cardOnTable.getId());

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

    private double checkForPossibleActualValuesFromDeck(){
        // We want to get the average value we get if we take card from deck = average of possibilities
        double cummulativeValue = 0;
        int numberOfPossibilities = 0;
        double maxValue;
        ArrayList<Card> canBeInDeck = server.getMightBeInDeck();
        canBeInDeck.removeAll(hand);
        ArrayList<Card> listForSearch = new ArrayList<>(hand);
        for(Card deck : canBeInDeck){
            hand.add(deck);
            maxValue = 0;
            for(Card c : listForSearch){
                hand.remove(c);
                int[] idsInHand = collectIDs(hand);
                double valueInHand = countActualValue(idsInHand);
                if(valueInHand > maxValue){
                    maxValue = valueInHand;
                    bestCardToDrop = c;
                }

                hand.add(c);

            }
            cummulativeValue += maxValue;
            numberOfPossibilities++;
            hand.remove(deck);
        }
        double averageCoefficientSumWithDeck = cummulativeValue / numberOfPossibilities;
        //System.out.println("Average coefficients sum from deck is " + averageCoefficientSumWithDeck);
        return averageCoefficientSumWithDeck;
    }

    private void checkForPossibleValuesInHand(){
        // 8 cards in hand, one must go out
        double maxValue = 0;
        ArrayList<Card> toBeSearched = new ArrayList<>(hand);
        for(Card c : toBeSearched){
            hand.remove(c);
            int[] idsInHand = collectIDs(hand);
            double valueInHand = countActualValue(idsInHand);
            if(valueInHand > maxValue){
                maxValue = valueInHand;
                bestCardToDrop = c;
            }
            hand.add(c);
        }
    }

    private double countActualValue(int[] ids){
        if(handValuesMap.containsKey(ids)){
            return handValuesMap.get(ids);
        }
        double cummulativeValue = 0;
        TuplesMapCreator tmc = new TuplesMapCreator(ids, server);
        /*
        System.out.println("Ids in hand count Actual value: ");
        for(int i : ids){
            System.out.print(i+ ",");
        }
        System.out.println();
        System.out.println("CountCummulativeValue " + ids);

         */
        ArrayList<Pair<Integer,Integer>> pairs = tmc.makeListOfPairs();
        for(Pair<Integer,Integer> pair : pairs){
            //System.out.println("Pair in countActualValue: " + pair.getKey() + "," + pair.getValue());
            if(coefficientsMap == null){
                System.out.println("COEFFICIENTS MAP IS NULL");
            }
            Object valueFromMap = coefficientsMap.get(pair);
            if(valueFromMap == null){
                System.out.println("VALUE FROM MAP IS NULL");
            }
            cummulativeValue += coefficientsMap.get(pair);
        }
        for(Card c : hand){
            cummulativeValue += cardsCoefficientsMap.get(c.getId());
        }
        //System.out.println("cummulative value of hand " + ids + " = " + cummulativeValue);
        handValuesMap.put(ids, cummulativeValue);
        return cummulativeValue;
    }

    private int getScoreBoundaries(){
        if(score <= 0){
            return 0;
        }
        return score / 10;
    }

    @Override
    public void learn() {
        ArrayList<Pair<Integer,Integer>> alreadyEvaluatedInThisRound = new ArrayList<>();
        ArrayList<Integer> alreadyEvaluatedCardsThisRound = new ArrayList<>();

        int reward = 0;
        if(rank == 1){
            reward = 1;
        }
        reward += getScoreBoundaries() * 0.001;
        //System.out.println("Reward for this round is " + reward);

        for(int i = round; i > round-5 && i > -1; i--){
            //System.out.println("historyHandRound size " + historyHandRound.size() + " cards in historyHand: ");
            int[] historyHand = historyHandRound.get(i);
            /*
            for(int id : historyHand){
                System.out.print(id + ",");
            }
            System.out.println();

             */
            TuplesMapCreator tmc = new TuplesMapCreator(historyHand, server);
            // learn hands in history too
            ArrayList<Pair<Integer,Integer>> pairs = tmc.makeListOfPairs();

            for(Pair<Integer,Integer> pair : pairs){
                if(!alreadyEvaluatedInThisRound.contains(pair)) {
                    Double pairCoefficient = coefficientsMap.get(pair);
                    double oldValuePair = pairCoefficient;
                    double newValuePair = oldValuePair + (alpha * (reward - oldValuePair))*(1-(round-i)*0.02);
                    //System.out.println("New value for pair: " + pair.getKey() + "," + pair.getValue() + " = " + newValuePair);
                    coefficientsMap.put(pair, newValuePair);
                    alreadyEvaluatedInThisRound.add(pair);
                }
            }
            for(int id : historyHand){
                if(!alreadyEvaluatedCardsThisRound.contains(id)){
                    Double cardCoefficient = cardsCoefficientsMap.get(id);
                    double oldValue = cardCoefficient;
                    double newValue = oldValue + (alpha * (reward - oldValue))*(1-(round-i)*0.02);
                    //System.out.println("New value for pair: " + id + " = " + newValue);

                    cardsCoefficientsMap.put(id, newValue);
                    alreadyEvaluatedCardsThisRound.add(id);
                }
            }
        }
        if(server.ithRound % 100 == 0){
            writeValuesToFile("map_coefficients");
            writeValuesToFile("card_coefficients");
        }
    }

    private void writeValuesToFile(String fileName){
        ExperimentOutputCreator ex = new ExperimentOutputCreator(server.getPlayers());
        if(fileName.equals("map_coefficients")){
            ex.writePairCoefficients(ex.createOutputFile(fileName),coefficientsMap);
        }
        if(fileName.equals("card_coefficients")){
            ex.writeCardsCoefficients(ex.createOutputFile(fileName),cardsCoefficientsMap);
        }
    }

    private Map<Integer, Double> loadCardCoefficientsFromFile(String fileName){
        Map<Integer, Double> map = new HashMap<>();
        try{
            FileReader r = new FileReader(fileName + ".txt");
            BufferedReader fr = new BufferedReader(r);

            String line;
            // Read objects
            while((line = fr.readLine()) != null){
                String[] separated = line.split(";");
                map.put( Integer.parseInt(separated[0]),
                        Double.parseDouble(separated[1]));
            }

            fr.close();
        } catch (IOException e) {
            System.out.println("Error initializing stream while loading");
            return null;
        }

        return map;
    }

    private Map<Pair<Integer,Integer>, Double> loadPairCoefficientsFromFile(String fileName){
        Map<Pair<Integer,Integer>, Double> map = new HashMap<>();
        try{
            FileReader r = new FileReader(fileName + ".txt");
            BufferedReader fr = new BufferedReader(r);

            String line;
            // Read objects
            while((line = fr.readLine()) != null){
                String[] separated = line.split(";");
                map.put(new Pair<>(Integer.parseInt(separated[0]), Integer.parseInt(separated[1])),
                        Double.parseDouble(separated[2]));
            }

            fr.close();
        } catch (IOException e) {
            System.out.println("Error initializing stream while loading");
            return null;
        }

        return map;
    }

    private Map<Pair<Integer,Integer>, Coefficients> loadCoefficientsFromFile(String fileName){
        Map<Pair<Integer,Integer>, Coefficients> map = new HashMap<>();
        try{
            FileReader r = new FileReader(fileName + ".txt");
            BufferedReader fr = new BufferedReader(r);

            String line;
            // Read objects
            while((line = fr.readLine()) != null){
                String[] separated = line.split(";");
                map.put(new Pair<>(Integer.parseInt(separated[0]), Integer.parseInt(separated[1])),
                        new Coefficients(Integer.parseInt(separated[2]), Double.parseDouble((separated[3]))));
            }

            fr.close();
        } catch (IOException e) {
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
        /*
        System.out.println("Result is " + result + " so epsilon is " + String.valueOf(epsilon) + " We did "  +
                (result <= epsilon ? "random" : "normal") + " move.");

         */
        return result <= epsilon;
    }

    @Override
    public void countScore() {
        // Create different hands by interactives that AI has
        ScoreCounterForAI sc = new ScoreCounterForAI(server);

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

    @Override
    public void eraseCardFromTable(Card c) {

    }
}
