package server;

import artificialintelligence.GreedyPlayer;
import artificialintelligence.ScoreCounterForAI;
import artificialintelligence.StateMapCreator;
import util.HandCloner;

import java.util.ArrayList;

public class CountBestFromTableAndHandThread  extends Thread  {
    PlayerOrAI player;
    ArrayList<Card> table;
    int HAND_SIZE = 7;
    int TABLE_SIZE = 9;
    int[] handIndexes = {0,1,2,3,4,5,6};
    int[] tableIndexes = {0,1,2,3,4,5,6,7,8};
    public CountBestFromTableAndHandThread(PlayerOrAI player, ArrayList<Card> cardsOnTable){
        this.player = player;
        this.table = cardsOnTable;
    }

    public void run(){
        int maxScore = player.getScore();
        ArrayList<Card> bestHand = new ArrayList<>();
        int numberOfChangedCards = 0;
        HandCloner hc = new HandCloner();

        for(int i = 1; i < 4; i++){
            //Switch this number of cards
            StateMapCreator smc = new StateMapCreator(new int[1], 0,0);
            /*
            System.out.print("Toto je N ruka: ");
            for(Card c : player.getHand()){
                System.out.print(c.getName() + ", ");
            }
            System.out.println();


             */
            ArrayList<ArrayList<Integer>> tuplesInHand = smc.makeNTuples(handIndexes,i);
            ArrayList<ArrayList<Integer>> tuplesOnTable = smc.makeNTuples(tableIndexes,i);
            for(ArrayList<Integer> tupleInHand : tuplesInHand){
                for(ArrayList<Integer> tupleOnTable : tuplesOnTable){

                    try {
                        ArrayList<Card> newHand = hc.cloneHand(null, player.getStoredHand());
                        ArrayList<Card> newTable = hc.cloneHand(null, table);
                        ArrayList<Card> toRemoveFromHand = new ArrayList<>();
                        ArrayList<Card> toRemoveFromTable = new ArrayList<>();
/*
                        System.out.print("zkopirovana ruka: ");
                        for(Card c : newHand){
                            System.out.print(c.getName() + ", ");
                        }
                        System.out.println();


 */
                       // System.out.println("Hand ");
                        for(Integer hand : tupleInHand){
                            toRemoveFromHand.add(newHand.get(hand));
                            //System.out.print(hand.toString() + ",");
                        }
                        //System.out.println("Table ");
                        for(Integer t : tupleOnTable){
                            toRemoveFromTable.add(newTable.get(t));
                            //System.out.print(t.toString() + ",");
                        }
/*
                        System.out.print("Ma byt odebrano: ");
                        for(Card toBeRemoved: toRemoveFromHand){
                            System.out.print(toBeRemoved.getName() + ", ");
                        }
                        System.out.println();

 */
                        newHand.removeAll(toRemoveFromHand);
                        newTable.removeAll(toRemoveFromTable);

                        /*System.out.print("Po odebrani ruka: ");
                        for(Card c : newHand){
                            System.out.print(c.getName() + ", ");
                        }
                        System.out.println();

                         */
                        newHand.addAll(toRemoveFromTable);
                        newTable.addAll(toRemoveFromHand);
                        int currentScore = 0;
                        ScoreCounterForAI sc = new ScoreCounterForAI();
                        int score = ((GreedyPlayer)player).getCacheMap().getValue(newHand);
                        if(score > -998) {
                            currentScore = score;

                        } else {
                            currentScore = sc.countScore(newHand, newTable, true);
                            //System.out.print("Players cards on hand before deciding what to do [score: " + currentMaxScore + "]: ");
                            if(newHand.size() == 8){
                                System.out.println("Necromancer took a card and its visible in CountBestTableAndHand");
                            }
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
        /*
        System.out.print("Toto je N ruka: ");
        for(Card c : player.getHand()){
            System.out.print(c.getName() + ", ");
        }
        System.out.println();
        System.out.print("Toto je I ruka: ");
        for(Card c : bestHand){
            System.out.print(c.getName() + ", ");
        }
        System.out.println();

         */
        if(bestHand.isEmpty()){
            //System.out.println("best hand JE NULL");
            ((GreedyPlayer)player).setBestHandAfterTheEnd(player.getHand());
        } else{
            //System.out.println("best hand neni null, zapisuji idealni ruku");
            ((GreedyPlayer)player).setBestHandAfterTheEnd(bestHand);
        }
    }


}
