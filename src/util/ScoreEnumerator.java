package util;

import artificialintelligence.Coefficients;
import artificialintelligence.ScoreCounterForAI;
import javafx.util.Pair;
import server.Card;
import server.Deck;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreEnumerator {
    Map<String, Integer> map;
    ScoreCounterForAI sc;
    int hand;
    Deck deck;
    public ScoreEnumerator(int hand){
        this.hand = hand;
        map = new HashMap<>();
        this.deck = new Deck();
        deck.initializeOriginal();
        deck.setDeck(false);
        sc = new ScoreCounterForAI();
    }

    public void saveAllPossibilities(){

        saveMapToFile("allPossibilities");
    }

    private void saveMapToFile(String file){

        int[] tempCards = new int[hand];

        //get array of indexes for hand
        combineCards(tempCards, 0, deck.getDeck().size()-1,0,hand);

        writeToOutputFile(createOutputFile(file));
    }

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
            int score = sc.countScore(hand, deck.getDeck());
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

    public void storeMapToFile(Map<Pair<Integer,Integer>, Coefficients> map){
        try {
            FileOutputStream f = new FileOutputStream(new File("DefaultGameDeckCardsObjects.txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            for(Map.Entry entry: map.entrySet()){
                o.writeObject(entry);
                //System.out.println("ID: " + c.id + " Name: " + c.name);
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
