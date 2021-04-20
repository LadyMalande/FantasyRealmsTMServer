package server;

import artificialintelligence.Coefficients;
import javafx.util.Pair;

import java.io.*;
import java.util.Map;
import java.util.Vector;

public class ExperimentOutputCreator {
    Vector<PlayerOrAI> players;
    StringBuilder startingDeck;

    public ExperimentOutputCreator(Vector<PlayerOrAI> list){
        this.players = list;
    }
    public ExperimentOutputCreator(Vector<PlayerOrAI> list, StringBuilder deck){
        this.players = list;
        this.startingDeck = deck;
    }


    public File createOutputFile(String filename){
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

    public void writeCoefficients(File file, Map<Pair<Integer, Integer>, Coefficients> map){
        try {
            FileWriter writer = new FileWriter(file, true);
            FileOutputStream f = new FileOutputStream(createOutputFile("map_coefficients"));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            for(Map.Entry entry : map.entrySet()){
                o.writeObject(entry.getKey());
                o.writeObject(entry.getValue());
                //System.out.println("ID: " + c.id + " Name: " + c.name);
            }

            o.close();
            f.close();

            // Writes the content to the file
            writer.write("\n");
            writer.flush();
            writer.close();
        } catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Unable to send experiment data to file.");
        }
    }

    private void writeToOutputFile(File file){
        try {
            double totalNumberOfRounds = 0;



            FileWriter writer = new FileWriter(file, true);
            /*
            if(startingDeck != null){

                writer.write(startingDeck.toString());
            }

             */
            for(PlayerOrAI player : players) {
                totalNumberOfRounds += player.getNumberOfRoundsPlayed();
            }
            double averageNumberOfRounds = totalNumberOfRounds / players.size();
            writer.write(totalNumberOfRounds + ";");

            for(PlayerOrAI player : players){

                /*
                List<String> namesOld = Arrays.asList(player.getBeginningHandCards().split(";"));

                StringBuilder sameCards = new StringBuilder();
                for(Card c : player.getHand()){
                    if(namesOld.contains(c.getName())){
                        sameCards.append(c.getName());
                    }
                    sameCards.append(";");
                }

                 */
               // writer.write(player.getName() + ";" + player.getNumberOfRoundsPlayed() + ";" + player.getBeginningHandScore() + ";" + player.getBeginningHandCards() + player.score + ";" );
                writer.write(player.getName() + ";" + player.getNumberOfRoundsPlayed() + ";"+ player.score + ";" );

                for(Card c: player.getHand()){
                    writer.write(c.name + ";");
                }
                if(player.getHand().size() < 8){
                    for(int i = player.getHand().size(); i < 8; i++){
                        writer.write("-;");
                    }
                }
                //writer.write(differenceInScores + ";");
                //writer.write(sameCards.toString());
            }
            // Writes the content to the file
            writer.write("\n");
            writer.flush();
            writer.close();
        } catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Unable to send experiment data to file.");
        }
    }

    public void createOutput(String experimentName){
        writeToOutputFile(createOutputFile(experimentName));
    }
}
