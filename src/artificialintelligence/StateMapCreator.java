package artificialintelligence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StateMapCreator {
    Map<Integer, int[]> stateMap;
    int id;
    int[] poolOfIDs;
    ArrayList<Integer> listPoolOfIDs;
    int hand;
    int table;
    public StateMapCreator(int[] arrayOfIDs, int handCapacity, int tableCapacity){
        stateMap = new HashMap<>();
        this.poolOfIDs = arrayOfIDs;
        this.listPoolOfIDs = makeList(poolOfIDs);
        this.hand = handCapacity;
        this.table = tableCapacity;
        int id = 0;
    }

    public Map<Integer, int[]> makeStateMap(){
        int[] tempHand = new int[hand];

        //get array of indexes for hand
        combineHand(tempHand, 0, poolOfIDs.length-1,0,hand);

        return stateMap;
    }

    private void combineHand(int[] tempHand,int startIndexOfPool, int endIndexOfPool, int startIndex, int numberOfItems){
        if(startIndex == numberOfItems){
            int[] tempTable = new int[table];
            ArrayList<Integer> listHand = makeList(tempHand);
            listPoolOfIDs.removeAll(listHand);
            int[] restOfCardsPool = listPoolOfIDs.stream().mapToInt(i -> i).toArray();
            for(int j = 0; j <= table; j++){
                combineTable(tempHand, tempTable,restOfCardsPool,0, restOfCardsPool.length, 0, j);
            }
            listPoolOfIDs.addAll(listHand);
            return;
        }

        for(int i = startIndexOfPool; i < poolOfIDs.length; i++){
            tempHand[startIndex] = poolOfIDs[i];
            combineHand(tempHand,i+1,endIndexOfPool,startIndex+1,numberOfItems);
        }
    }

    private void combineTable(int[] tempHand, int[] tempTable, int[] pool ,int startIndexOfPool, int endIndexOfPool, int startIndex, int numberOfItems){
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
            combineTable(tempHand,tempTable,pool, i+1,endIndexOfPool,startIndex+1,numberOfItems);
        }

    }

    private ArrayList<Integer> makeList(int[] arr){
        ArrayList<Integer> list = new ArrayList<>(arr.length);
        for(int i : arr){
            list.add(i);
        }
        return list;
    }
}
