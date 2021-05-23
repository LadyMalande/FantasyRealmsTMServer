package util;

import artificialintelligence.Coefficients;
import server.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Support class for making a tuple map.
 * @author Tereza Miklóšová
 */
public class TuplesMapCreator {

    /**
     * State map of pairs of ids forming a state and its coefficient.
     */
    Map<Pair<Integer, Integer>, Coefficients> stateMap;

    Map<Pair<Integer, Integer>, Double> pairCoefficientsMap;

    Map<Integer, Double> cardCoefficientsMap;
    /**
     * Pool of ids that have to be used for state creating.
     */
    int[] poolOfIDs;
    /**
     * Size of the tuple.
     */
    private final int TUPLE_SIZE = 2;

    Server server;

    /**
     * Constructor for this class.
     * @param arrayOfIDs The ids of cards that will be used in the game.
     */
    public TuplesMapCreator (int[] arrayOfIDs, Server server){
        stateMap = new HashMap<>();
        this.poolOfIDs = arrayOfIDs;
        this.server = server;
    }

    /**
     * Get the created state map.
     * @return Map of pairs of ids of cards and their coefficients.
     */
    public Map<Pair<Integer, Integer>,Coefficients> makeStateMap(){
        int[] tuple = new int[2];

        //get array of indexes for hand
        combineHand(tuple, 0, 0,TUPLE_SIZE);
        writeToOutputFile(createOutputFile());
        return stateMap;
    }

    public Map<Pair<Integer, Integer>, Double> makePairCoefficientsMap(){
        int[] tuple = new int[2];
        pairCoefficientsMap = Collections.synchronizedMap(new HashMap<>());

        //get array of indexes for hand
        combineHand(tuple, 0, 0,TUPLE_SIZE);
        return pairCoefficientsMap;
    }

    public Map<Integer, Double> makeCardCoefficientsMap(){
        //get array of indexes for hand
        cardCoefficientsMap =  Collections.synchronizedMap(new HashMap<>());
        for(int i = 1; i <= 54;i++){
            cardCoefficientsMap.put(i, 0.5);
        }
        return cardCoefficientsMap;
    }

    /**
     * Combine ids in given pool to tuples of maximal length of numberOfItems.
     * @param tempHand Here are stored the partially made tuples.
     * @param startIndexOfPool  Starting index of the pool of ids.
     * @param startIndex Starting index of the tuple.
     * @param numberOfItems Number of items in the tuple.
     */
    private void combineHand(int[] tempHand, int startIndexOfPool, int startIndex, int numberOfItems){
        if(startIndex == numberOfItems){
            Pair<Integer, Integer> pair = new Pair<>(tempHand[0],tempHand[1]);
            //Deck deck = new Deck();
            //deck.initializeOriginal();
            //deck.setDeck(false);
            //List<Card> hand = deck.getDeck().stream().filter(card -> (card.getId() == pair.getKey()) ||
            //        (card.getId() == pair.getValue())).collect(Collectors.toList());
            //ScoreCounterForAI sc = new ScoreCounterForAI(server);
            //int actual = sc.countScore(hand,new ArrayList<>(), true);
            pairCoefficientsMap.put(pair, new Double(0.5));
            //System.out.println("<" + pair.getKey() + "," + pair.getValue() + "> " + 0.5 + " size "+ pairCoefficientsMap.size());
            return;
        }

        for(int i = startIndexOfPool; i < poolOfIDs.length; i++){
            tempHand[startIndex] = poolOfIDs[i];
            combineHand(tempHand,i+1, startIndex+1,numberOfItems);
        }
    }

    /**
     * Make a list out of simple array.
     * @param arr Simple array to transform.
     * @return ArrayList with elements from the simple array.
     */
    public ArrayList<Integer> makeList(int[] arr){
        ArrayList<Integer> list = new ArrayList<>(arr.length);
        for(int i : arr){
            list.add(i);
        }
        return list;
    }

    /**
     * Create list of Pairs of ids.
     * @return ArrayList full of pairs of ids.
     */
    public ArrayList<Pair<Integer,Integer>> makeListOfPairs(){
        int[] tuple = new int[2];
        ArrayList<Pair<Integer,Integer>> list = new ArrayList<>();
        combineIDs(tuple, 0, 0,TUPLE_SIZE, list);
        return list;
    }

    /**
     * Combine ids in given pool to tuples of maximal length of TUPLE_SIZE.
     * @param temp Here are stored the partially made tuples.
     * @param startIndexOfPool  Starting index of the pool of ids.
     * @param startIndex Starting index of the tuple.
     * @param numberOfItems Number of items in the tuple.
     * @param list For storing created tuples.
     */
    private void combineIDs(int[] temp, int startIndexOfPool, int startIndex, int numberOfItems, ArrayList<Pair<Integer, Integer>> list){
        if(startIndex == numberOfItems){
            Pair<Integer, Integer> pair = new Pair<>(temp[0],temp[1]);
            list.add(pair);
            return;
        }

        for(int i = startIndexOfPool; i < poolOfIDs.length; i++){
            temp[startIndex] = poolOfIDs[i];
            combineIDs(temp,i+1, startIndex+1,numberOfItems, list);
        }
    }

    /**
     * Create output file for the tuples with values.
     * @return Created File.
     */
    private File createOutputFile(){
        try {
            File myFile = new File("pair_values" + ".txt");
            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            }
            return myFile;
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Write the values to given file.
     * @param file File to write to.
     */
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
