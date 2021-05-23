package util;

import artificialintelligence.Coefficients;
import artificialintelligence.ScoreCounterForAI;
import server.Card;
import server.Deck;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Support class for counting scores of generated combinations of cards.
 * @author Tereza Miklóšová
 */
public class ScoreEnumerator {
    /**
     * Map of cards combined and their value.
     */
    Map<String, Integer> map;
    /**
     * Score counter to count all the scores.
     */
    ScoreCounterForAI sc;
    /**
     * Cards in hand.
     */
    int hand;
    /**
     * Cards in deck.
     */
    Deck deck;

    /**
     * Constructor for this class. Creates n-element hands.
     * @param hand Size of hands to create.
     */
    public ScoreEnumerator(int hand){
        this.hand = hand;
        map = new HashMap<>();
        this.deck = new Deck();
        deck.initializeOriginal();
        deck.setDeck(false);
        sc = new ScoreCounterForAI();
    }



    /**
     * Save all counted scores to file.
     */
    public void saveAllPossibilities(){

        saveMapToFile("allPossibilities");
    }

    /**
     * Write the results to given file.
     * @param file Name of the output file.
     */
    private void saveMapToFile(String file){

        int[] tempCards = new int[hand];

        //get array of indexes for hand
        combineCards(tempCards, 0, deck.getDeck().size()-1,0,hand);

        writeToOutputFile(createOutputFile(file));
    }

    /**
     * Combine cards of different ids to create all possible hands.
     * @param tempCards Variable for storing the made combination so far.
     * @param startIndexOfPool At which index of the supplied pool to start.
     * @param endIndexOfPool At which index of the supplied pool to end.
     * @param startIndex Start index for iterating.
     * @param numberOfItems How big n-elements array to create.
     */
    private void combineCards(int[] tempCards,int startIndexOfPool, int endIndexOfPool, int startIndex, int numberOfItems){
        if(startIndex == numberOfItems){
            deck.initializeOriginal();
            deck.setDeck(false);
            List<Card> hand = new ArrayList<>();

            StringBuilder cards = new StringBuilder();
            for(int s:tempCards){
                cards.append(s);
                cards.append(";");
                //System.out.println(s);
                hand.add(deck.getDeck().stream().filter(card -> card.getId() == s).findAny().get());
            }
            deck.getDeck().removeAll(hand);
            int score = sc.countScore(hand, deck.getDeck(),true);
            while(score < -999){

            }
            map.put(cards.toString(), score);
            return;
        }

        for(int i = startIndexOfPool; i < deck.getDeck().size(); i++){
            tempCards[startIndex] = deck.getDeck().get(i).getId();
            combineCards(tempCards,i+1,endIndexOfPool,startIndex+1,numberOfItems);
        }
    }

    /**
     * Creates output file with given name.
     * @param filename Name for the output file.
     * @return File by the given name.
     */
    private File createOutputFile(String filename){
        try {
            File myFile = new File(filename + ".txt");
            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            } else {
            }
            return myFile;
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Write the values from the map to a file.
     * @param map Map of combinations and its values.
     */
    public void storeMapToFile(Map<Pair<Integer,Integer>, Coefficients> map){
        try {
            FileOutputStream f = new FileOutputStream(new File("DefaultGameDeckCardsObjects.txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            for(Map.Entry entry: map.entrySet()){
                o.writeObject(entry);
            }
            o.close();
            f.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error initializing stream");
        }
    }

    /**
     * Writes all the counted combinations and their values to given file.
     * @param file File to write to.
     */
    private void writeToOutputFile(File file){
        try {
            FileWriter writer = new FileWriter(file, true);
            for(Map.Entry<String,Integer> entry : map.entrySet()) {
                writer.write(entry.getKey());
                writer.write(entry.getValue().toString());
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
