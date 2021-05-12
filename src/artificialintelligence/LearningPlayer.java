package artificialintelligence;

import javafx.util.Pair;
import server.Card;
import server.ExperimentOutputCreator;
import server.PlayerOrAI;
import server.Server;

import java.io.*;
import java.util.*;
import java.util.concurrent.FutureTask;

import static java.util.stream.Collectors.toList;

public class LearningPlayer extends PlayerOrAI implements ArtificialIntelligenceInterface{
        ArrayList<Card> hand;
        Card bestCardToTake;
        Card bestCardToDrop;
        Server server;
        int bestPossibleScore;
        int rank;
        String name;
        private FutureTask<Integer> futureTask;
        private int numberOfRoundsPlayed;
        String beginningHandCards;
        int beginningHandScore;
        boolean playing = false;
        int stateID;
        boolean stateRecognized;
        double alpha; // learning coefficient
        double epsilon; // choose random action with this probability
        Map<Pair<Integer,Integer>,Coefficients> coefficientsMap;
        int currentActualValue;
        Map<int[], Integer> handValuesMap;
        Map<Integer, int[]> historyHandRound;
        int round = 0;


    public LearningPlayer(Server server, String name, double alpha, double epsilon) throws CloneNotSupportedException {
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
            //1, 2, 3, 4, 5, 6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,  ,52,53,54
            TuplesMapCreator tmc = new TuplesMapCreator(new int[]{25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51});
            coefficientsMap = loadCoefficientsFromFile();
            if(coefficientsMap == null){
                coefficientsMap = tmc.makeStateMap();
            }
            this.alpha = alpha;
            this.epsilon = epsilon;
            currentActualValue = 0;
            getInitCards();
            rank = 0;

            for(Map.Entry<Pair<Integer,Integer>, Coefficients> entry : coefficientsMap.entrySet()){
                System.out.println(entry.getKey().toString() + " value: " + entry.getValue().getActualValue() + " coefficient: " + entry.getValue().getActalValueCoefficient());
            }
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
        public void sendScore(String s){
            learn();
        }

    @Override
    public void getInitCards() throws CloneNotSupportedException {
        round = 0;
        handValuesMap.clear();
        historyHandRound.clear();
        hand.clear();
        numberOfRoundsPlayed = 0;
        //Random randomGenerator = new Random();
        for (int j = 0; j < server.CARDS_ON_HAND; j++) {
            //System.out.println("In getInitCards in GreedyPlayer constructor, number of loop is " + j);
            //int index = randomGenerator.nextInt(server.deck.getDeck().size());
            Card newCard = server.deck.getDeck().get(0);
            hand.add(newCard);
            server.deck.getDeck().remove(newCard);
        }

            currentActualValue = countActualValue(collectIDs(hand));
        historyHandRound.put(round, collectIDs(hand));
        //System.out.println("Pocet karet v ruce: " + hand.size());
        }

        private void recognizeState(ArrayList<Card> cardsOnTable){
            int handSize = hand.size();
            int tableSize = cardsOnTable.size();
            int[] currentState = new int[handSize + tableSize];
            List<Integer> listHand = hand.stream().map(card -> card.getId()).collect(toList());
            List<Integer> listTable = cardsOnTable.stream().map(card -> card.getId()).collect(toList());
            Collections.sort(listHand);
            Collections.sort(listTable);
            int[] handIDs = listHand.stream().mapToInt(i -> i).toArray();
            int[] tableIDs = listTable.stream().mapToInt(i -> i).toArray();
            System.arraycopy(handIDs,0,currentState,0,handSize);
            System.arraycopy(tableIDs,0,currentState,handSize,tableSize);
           // stateID = stateMap.entrySet().stream().filter(entry -> Arrays.equals(entry.getValue(),currentState)).map(Map.Entry::getKey).findFirst().get();
            stateRecognized = true;
            //System.out.println("Player is in state id :" + stateID);
        }

    @Override
    public Card performMove(ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        if (performRandomMove()) {
            //System.out.println("random move Pocet karet v ruce: " + hand.size());
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
            //server.putCardOnTable(toDrop);
            hand.remove(toDrop);
            return toDrop;
        } else{

            int handActualValue = currentActualValue;
            //System.out.println("zac perform move Pocet karet v ruce: " + hand.size());

            int maxFromTable = maxForPossibleActualValuesFromTable();

            int avgFromDeck = checkForPossibleActualValuesFromDeck();
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
           // server.putCardOnTable(bestCardToDrop);
        }
        numberOfRoundsPlayed++;
        //System.out.println("konec perform move Pocet karet v ruce: " + hand.size());
        return bestCardToDrop;
    }

    private int maxForPossibleActualValuesFromTable(){
        int cummulativeValue = 0;
        int maxValue = currentActualValue;
        int[] ids = collectIDs(hand);

        TuplesMapCreator tmc = new TuplesMapCreator(ids);
        ArrayList<Integer> idsInHand = tmc.makeList(ids);

        for(Card cardInHand:hand){
            idsInHand.remove(new Integer(cardInHand.getId()));
            for(Card cardOnTable : server.cardsOnTable){
                idsInHand.add(cardOnTable.getId());
                ids = collectIDs(hand);
                cummulativeValue = countActualValue(ids);
                idsInHand.remove(new Integer(cardOnTable.getId()));

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
        int maxValue = 0;
        ArrayList<Card> canBeInDeck = server.getMightBeInDeck();
        canBeInDeck.removeAll(hand);
        ArrayList<Card> listForSearch = new ArrayList<>(hand);
        for(Card table : canBeInDeck){
            hand.add(table);
            //System.out.println("Pridavam do ruky z balicku : " + table.getId());
            maxValue = 0;
            for(Card c : listForSearch){
                hand.remove(c);
                for(Card c1: hand){
                    //System.out.println("V ruce: " + c1.getId());
                }
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
        TuplesMapCreator tmc = new TuplesMapCreator(ids);
        for(int id : ids){
            //System.out.println("ids pro vytvoreni dvojic: "  + id);
        }
        ArrayList<Pair<Integer,Integer>> pairs = tmc.makeListOfPairs(ids);
        for(Pair<Integer,Integer> pair : pairs){
            //System.out.println(pair);
            Coefficients co = coefficientsMap.get(pair);
            //cummulativeValue += co.getActualValue() * co.getActalValueCoefficient();
            cummulativeValue += co.getActalValueCoefficient();
        }
        handValuesMap.put(ids, cummulativeValue);
        return cummulativeValue;
    }

    @Override
    public void learn() {
        ArrayList<Pair<Integer,Integer>> alreadyEvaluatedInThisRound = new ArrayList<>();
        int[] idsInHand = collectIDs(hand);
        TuplesMapCreator tmc = new TuplesMapCreator(idsInHand);
        //ArrayList<Pair<Integer,Integer>> pairs = tmc.makeListOfPairs(idsInHand);
        int reward = 0;
        if(rank == 1){
            reward = 1;
        }

        for(int i = round; i > round-5 || i > -1; i--){
            // learn hands in history too
            ArrayList<Pair<Integer,Integer>> pairs = tmc.makeListOfPairs(idsInHand);
            for(Pair<Integer,Integer> pair : pairs){
                if(!alreadyEvaluatedInThisRound.contains(pair)) {
                    Coefficients c = coefficientsMap.get(pair);
                    double oldValue = c.getActalValueCoefficient();
                    double newValue = oldValue + (alpha * (reward - oldValue))*(1-(round-i)*0.02);
                    c.setActalValueCoefficient(newValue);
                    alreadyEvaluatedInThisRound.add(pair);
                    //System.out.println(pair + " value: " + c.actualValue + " oldc: " + oldValue + " newc: "+ newValue);
                }
            }
        }
        if(server.ithRound % 100 == 0){
            writeValuesToFile();
        }
    }

    private void writeValuesToFile(){
        ExperimentOutputCreator ex = new ExperimentOutputCreator(server.getPlayers());
        ex.writeCoefficients(ex.createOutputFile("map_coefficients"),coefficientsMap);
    }

    private Map<Pair<Integer,Integer>, Coefficients> loadCoefficientsFromFile(){
        Map<Pair<Integer,Integer>, Coefficients> map = new HashMap<>();
        try{
            FileReader r = new FileReader("map_coefficients.txt");
            BufferedReader fr = new BufferedReader(r);

            String line;
            // Read objects
            while((line = fr.readLine()) != null){
                String[] separated = line.split(";");
                map.put(new Pair<Integer, Integer>(Integer.parseInt(separated[0]), Integer.parseInt(separated[1])),
                        new Coefficients(Integer.parseInt(separated[2]), Double.parseDouble((separated[3]))));
            }
            for(Map.Entry entry: map.entrySet()){
                System.out.println(entry.getKey() + " value: " + ((Coefficients)entry.getValue()).getActualValue() + " newc: "+ ((Coefficients)entry.getValue()).getActalValueCoefficient());
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
        double result = rand /100;
        if(result <= epsilon ){
            return true;
        } else{
            return false;
        }
    }

    @Override
    public void countScore() {
        // Create different hands by interactives that AI has
        ScoreCounterForAI sc = new ScoreCounterForAI();
        score = sc.countScore(hand, server.cardsOnTable, false);

        while (score < -999) {
            // wait;
        }
        server.increaseCountedScoreNumber();
    }
}
