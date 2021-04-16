package artificialintelligence;

import javafx.util.Pair;
import server.Card;
import server.Deck;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TuplesMapCreator {

    Map<Pair<Integer, Integer>,Coefficients> stateMap;
    int[] poolOfIDs;
    private final int TUPLE_SIZE = 2;
    public TuplesMapCreator (int[] arrayOfIDs){
        stateMap = new HashMap<>();
        this.poolOfIDs = arrayOfIDs;
    }

    public Map<Pair<Integer, Integer>,Coefficients> makeStateMap(){
        int[] tuple = new int[2];

        //get array of indexes for hand
        combineHand(tuple, 0, poolOfIDs.length-1,0,TUPLE_SIZE);
        writeToOutputFile(createOutputFile("pair_values"));
        return stateMap;
    }

    private void combineHand(int[] tempHand,int startIndexOfPool, int endIndexOfPool, int startIndex, int numberOfItems){
        if(startIndex == numberOfItems){
            Pair<Integer, Integer> pair = new Pair<>(tempHand[0],tempHand[1]);
            Deck deck = new Deck();
            deck.initializeOriginal();
            deck.setDeck(false);
            List<Card> hand = deck.getDeck().stream().filter(card -> (card.getId() == pair.getKey()) || (card.getId() == pair.getValue())).collect(Collectors.toList());
            ScoreCounterForAI sc = new ScoreCounterForAI();
            int actual = sc.countScore(hand,new ArrayList<>());
            while(actual < -999){

            }
            stateMap.put(pair, new Coefficients(actual));
            System.out.println( pair.getKey() + " : " + pair.getValue() + " = " + actual);

            return;
        }

        for(int i = startIndexOfPool; i < poolOfIDs.length; i++){
            tempHand[startIndex] = poolOfIDs[i];
            combineHand(tempHand,i+1,endIndexOfPool,startIndex+1,numberOfItems);
        }
    }

    ArrayList<Integer> makeList(int[] arr){
        ArrayList<Integer> list = new ArrayList<>(arr.length);
        for(int i : arr){
            list.add(i);
        }
        return list;
    }

    public ArrayList<Pair<Integer,Integer>> makeListOfPairs(int[] ids){
        int[] tuple = new int[2];
        ArrayList<Pair<Integer,Integer>> list = new ArrayList<>();
        combineIDs(tuple, 0, ids.length-1,0,TUPLE_SIZE, list);
        return list;
    }

    private void combineIDs(int[] temp,int startIndexOfPool, int endIndexOfPool, int startIndex, int numberOfItems, ArrayList<Pair<Integer,Integer>> list){
        if(startIndex == numberOfItems){
            Pair<Integer, Integer> pair = new Pair<>(temp[0],temp[1]);
            list.add(pair);
            return;
        }

        for(int i = startIndexOfPool; i < poolOfIDs.length; i++){
            temp[startIndex] = poolOfIDs[i];
            combineIDs(temp,i+1,endIndexOfPool,startIndex+1,numberOfItems, list);
        }
    }

    private File createOutputFile(String filename){
        try {
            File myFile = new File(filename + ".txt");
            //System.out.println("Creating file named: " + myFile.getName());
            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            } else {
                //System.out.println("File already exists.");
            }
            return myFile;
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }

    private void writeToOutputFile(File file){
        try {
            FileWriter writer = new FileWriter(file, true);
            for(Map.Entry<Pair<Integer, Integer>, Coefficients> entry : stateMap.entrySet()) {
                writer.write(entry.getKey().getKey().toString());
                writer.write(";");
                writer.write(entry.getKey().getValue().toString());
                writer.write(";");
                writer.write(String.valueOf(entry.getValue().getActualValue()));
                writer.write("\n");
            }
            // Writes the content to the file
            writer.flush();
            writer.close();
        } catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Unable to send experiment data to file.");
        }
    }

}
