package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Support class for making a map of states and their values.
 * @author Tereza Miklóšová
 */
public class StateMapCreator {
    /**
     * The map of states and their names.
     */
    Map<Integer, int[]> stateMap;
    /**
     * Pool of IDs of cards to include to the states.
     */
    int[] poolOfIDs;
    /**
     * List version of the poolOfIDs.
     */
    ArrayList<Integer> listPoolOfIDs;
    /**
     * List of all pairs made from teh pool of ids.
     */
    ArrayList<ArrayList<Integer>> listOfTuples;

    /**
     * Number of cards in hand.
      */
    int hand;
    /**
     * Number of cards on the table.
     */
    int table;

    /**
     * Constructor.
     * @param arrayOfIDs Pool of IDs of cards to include to the states.
     * @param handCapacity Number of cards in hand.
     * @param tableCapacity Number of cards on the table.
     */
    public StateMapCreator(int[] arrayOfIDs, int handCapacity, int tableCapacity){
        stateMap = new HashMap<>();
        this.poolOfIDs = arrayOfIDs;
        this.listPoolOfIDs = makeList(poolOfIDs);
        this.hand = handCapacity;
        this.table = tableCapacity;
    }

    /*
    public Map<Integer, int[]> makeStateMap(){
        int[] tempHand = new int[hand];
        //get array of indexes for hand
        combineHand(tempHand, 0, poolOfIDs.length-1,0,hand);
        return stateMap;
    }

     */

    /**
     * Makes tuples of given length out of the array of ids.
     * @param arrayOfIDs Pool of ids to take from.
     * @param lengthOfTuple How large the tuples should be.
     * @return ArrayList of all the possible tuples of given size and from the ids supplied in array.
     */
    public ArrayList<ArrayList<Integer>> makeNTuples(int[] arrayOfIDs, int lengthOfTuple){
        int[] tempTuple = new int[lengthOfTuple];
        listOfTuples = new ArrayList<>();
        poolOfIDs = arrayOfIDs;
        if(lengthOfTuple != 0){
            combineNNumbersFromPool(tempTuple,0, 0,lengthOfTuple);
            return listOfTuples;
        }
        return null;
    }

    /**
     * Combines the ids from the pool to the temporary array.
     * @param tempHand Temporary array for storing tidally growing array of values.
     * @param startIndexOfPool At which starting point of the pool of IDs should the algorithm start.
     * @param startIndex At which index to start.
     * @param numberOfItems Number of items in the tuple.
     */
    private void combineNNumbersFromPool(int[] tempHand, int startIndexOfPool, int startIndex, int numberOfItems){
        if(startIndex == numberOfItems){
            ArrayList<Integer> listHand = makeList(tempHand);
            listOfTuples.add(listHand);
            return;
        }

        for(int i = startIndexOfPool; i < poolOfIDs.length; i++){
            tempHand[startIndex] = poolOfIDs[i];
            combineNNumbersFromPool(tempHand,i+1, startIndex+1,numberOfItems);
        }
    }
/*
    private void combineHand(int[] tempHand,int startIndexOfPool, int endIndexOfPool, int startIndex, int numberOfItems){
        if(startIndex == numberOfItems){
            int[] tempTable = new int[table];
            ArrayList<Integer> listHand = makeList(tempHand);
            listPoolOfIDs.removeAll(listHand);
            int[] restOfCardsPool = listPoolOfIDs.stream().mapToInt(i -> i).toArray();
            for(int j = 0; j <= table; j++){
                combineTable(tempHand, tempTable,restOfCardsPool,0, 0, j);
            }
            listPoolOfIDs.addAll(listHand);
            return;
        }

        for(int i = startIndexOfPool; i < poolOfIDs.length; i++){
            tempHand[startIndex] = poolOfIDs[i];
            combineHand(tempHand,i+1,endIndexOfPool,startIndex+1,numberOfItems);
        }
    }

 */

/*
    private void combineTable(int[] tempHand, int[] tempTable, int[] pool, int startIndexOfPool, int startIndex, int numberOfItems){
        if(startIndex == numberOfItems){
            int[] result = new int[hand+numberOfItems];
            System.arraycopy(tempHand,0,result,0,hand);
            System.arraycopy(tempTable,0,result,hand,numberOfItems);
            stateMap.put(id, result);
            System.out.println(id + " : " + Arrays.toString(result));
            id++;
            return;
        }
        for(int i = startIndexOfPool; i < pool.length; i++){
            tempTable[startIndex] = pool[i];
            combineTable(tempHand,tempTable,pool, i+1, startIndex+1,numberOfItems);
        }

    }

 */

    /**
     * Creates ArrayList out of simple array.
     * @param arr Simple array.
     * @return ArrayList version of the array.
     */
    private ArrayList<Integer> makeList(int[] arr){
        ArrayList<Integer> list = new ArrayList<>(arr.length);
        for(int i : arr){
            list.add(i);
        }
        return list;
    }
}
