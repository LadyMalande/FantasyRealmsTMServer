package artificialintelligence;

import interactive.Interactive;
import javafx.scene.control.skin.SeparatorSkin;
import javafx.util.Pair;
import maluses.Malus;
import server.Card;
import server.PlayerOrAI;
import server.Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public LearningPlayer(Server server, String name, double alpha, double epsilon) throws CloneNotSupportedException {
            this.name = name;
            System.out.println("Name of Learning " + this.name);
            hand = new ArrayList<>();
            this.server = server;
            bestPossibleScore = 0;
            bestCardToTake = null;
            bestCardToDrop = null;
            numberOfRoundsPlayed = 0;
            getInitCards();
            stateRecognized = false;
            TuplesMapCreator tmc = new TuplesMapCreator(new int[]{1, 2, 3, 4, 5, 6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54};
            coefficientsMap = loadCoefficientsFromFile();
            if(coefficientsMap == null){
                tmc.makeStateMap();
            }
            rank = 0;
            this.alpha = alpha;
            this.epsilon = epsilon;
            currentActualValue = 0;
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
            System.out.println("Player is in state id :" + stateID);
        }

    @Override
    public Card performMove(ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        int handActualValue = currentActualValue;

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
            server.putCardOnTable(toDrop);
            hand.remove(toDrop);
        } else{

            hand.remove(bestCardToDrop);
            server.putCardOnTable(bestCardToDrop);
        }


        return null;
    }

    private int maxForPossibleActualValuesFromTable(){
        int cummulativeValue = 0;
        int maxValue = currentActualValue;
        int[] ids = collectIDs(hand);

        TuplesMapCreator tmc = new TuplesMapCreator(ids);
        ArrayList<Integer> listIDs = tmc.makeList(ids);
        List<Integer> idsInHand = new ArrayList<Integer>();

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

    }

    private void checkForPossibleValuesInHand(){

    }

    private int countActualValue(int[] ids){
        
    }

    @Override
    public void learn() {
        int[] idsInHand = collectIDs(hand);
        TuplesMapCreator tmc = new TuplesMapCreator(idsInHand);
        ArrayList<Pair<Integer,Integer>> pairs = tmc.makeListOfPairs(idsInHand);
        int reward = 0;
        if(rank == 1){
            reward = 1;
        }

        for(Pair<Integer,Integer> pair : pairs){
            Coefficients c = coefficientsMap.get(pair);
            double oldValue = c.getActalValueCoefficient();
            double newValue = oldValue + alpha*(reward-oldValue);
            c.setActalValueCoefficient(newValue);
        }
    }

    private Map<Pair<Integer,Integer>, Coefficients> loadCoefficientsFromFile(){
        Map<Pair<Integer,Integer>, Coefficients> map = new HashMap<>();

        try{
            FileInputStream fi = new FileInputStream(new File("pair_values.txt"));
            ObjectInputStream oi = new ObjectInputStream(fi);

            // Read objects
            for(int i = 0; i < 1432; i++){
                map.put(((Map.Entry<Pair<Integer,Integer>,Coefficients>) oi.readObject()).getKey(),
                        ((Map.Entry<Pair<Integer,Integer>,Coefficients>) oi.readObject()).getValue());
            }

            oi.close();
            fi.close();
        } catch (IOException e) {
            System.out.println("Error initializing stream while loading");
            return null;
        }
        catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
}
