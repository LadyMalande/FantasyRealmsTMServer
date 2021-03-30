package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ExperimentOutputCreator {
    Vector<PlayerOrAI> players;

    public ExperimentOutputCreator(Vector<PlayerOrAI> list){
        this.players = list;
    }


    private File createOutputFile(String filename){
        try {
            File myFile = new File(filename + ".txt");
            System.out.println("Creating file named: " + myFile.getName());
            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            } else {
                System.out.println("File already exists.");
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
            double totalNumberOfRounds = 0;

            FileWriter writer = new FileWriter(file, true);
            for(PlayerOrAI player : players) {
                totalNumberOfRounds += player.getNumberOfRoundsPlayed();
            }
            double averageNumberOfRounds = totalNumberOfRounds / players.size();
            writer.write(totalNumberOfRounds + ";");
            for(PlayerOrAI player : players){

                List<String> namesOld = Arrays.asList(player.getBeginningHandCards().split(";"));
                StringBuilder sameCards = new StringBuilder();
                for(Card c : player.getHand()){
                    if(namesOld.contains(c.getName())){
                        sameCards.append(c.getName());
                    }
                    sameCards.append(";");
                }
                writer.write(player.getName() + ";" + player.getNumberOfRoundsPlayed() + ";" + player.getBeginningHandScore() + ";" + player.getBeginningHandCards() + player.score + ";" );
                for(Card c: player.getHand()){
                    writer.write(c.name + ";");
                }
                if(player.getHand().size() < 8){
                    writer.write("-;");
                }
                int differenceInScores = player.score - player.getBeginningHandScore();
                writer.write(differenceInScores + ";");
                writer.write(sameCards.toString());
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
