package server;

import artificialintelligence.GreedyPlayer;
import artificialintelligence.ScoreCounterForAI;
import util.StateMapCreator;
import util.HandCloner;

import java.util.ArrayList;

/**
 * Class that counts the best possible score after the end of the game by combining cards from hand and changes them
 * for at most 3 cards from the table. Running as a separate thread.
 * @author Tereza Miklóšová
 */
public class CountBestFromTableAndHandThread  extends Thread  {
    /**
     * Which player is this class trying to evaluate.
     */
    PlayerOrAI player;
    /**
     * Cards on table.
     */
    ArrayList<Card> table;
    /**
     * Card indexes from the hand to take into consideration while trading.
     */
    int[] handIndexes = {0,1,2,3,4,5,6};
    /**
     * Card indexes from the table to take into consideration while trading.
     */
    int[] tableIndexes = {0,1,2,3,4,5,6,7,8};

    Server server;
    /**
     * Constructor of the thread.
     * @param player Which player is this class trying to evaluate.
     * @param cardsOnTable Cards on table.
     */
    public CountBestFromTableAndHandThread(PlayerOrAI player, ArrayList<Card> cardsOnTable, Server server){
        this.player = player;
        this.table = cardsOnTable;
        this.server = server;
    }

    public void run(){
        int maxScore = player.getScore();
        ArrayList<Card> bestHand = new ArrayList<>();
        int numberOfChangedCards = 0;
        HandCloner hc = new HandCloner();

        for(int i = 1; i < 4; i++){
            //Switch this number of cards
            StateMapCreator smc = new StateMapCreator(new int[1], 0,0);
            ArrayList<ArrayList<Integer>> tuplesInHand = smc.makeNTuples(handIndexes,i);
            ArrayList<ArrayList<Integer>> tuplesOnTable = smc.makeNTuples(tableIndexes,i);
            for(ArrayList<Integer> tupleInHand : tuplesInHand){
                for(ArrayList<Integer> tupleOnTable : tuplesOnTable){

                    try {
                        ArrayList<Card> newHand = hc.cloneHand(null, player.getStoredHand());
                        ArrayList<Card> newTable = hc.cloneHand(null, table);
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
                        ScoreCounterForAI sc = new ScoreCounterForAI(server);
                        int score = ((GreedyPlayer)player).getCacheMap().getValue(newHand);
                        if(score > -998) {
                            currentScore = score;

                        } else {
                            currentScore = sc.countScore(newHand, newTable, true);
                        }
                        if(currentScore > maxScore){
                            maxScore = currentScore;
                            bestHand = new ArrayList<>(newHand);
                            numberOfChangedCards = i;
                        }


                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }


        }

        ((GreedyPlayer)player).setBestScoreAfterTheEnd(maxScore);
        ((GreedyPlayer)player).setNumberOfChangedCardsInIdealHand(numberOfChangedCards);

        if(bestHand.isEmpty()){
            ((GreedyPlayer)player).setBestHandAfterTheEnd(player.getHand());
        } else{
            ((GreedyPlayer)player).setBestHandAfterTheEnd(bestHand);
        }
    }


}
