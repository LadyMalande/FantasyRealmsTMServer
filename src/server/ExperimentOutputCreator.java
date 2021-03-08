package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
            FileWriter writer = new FileWriter(file, true);

            for(PlayerOrAI player : players){
                writer.write(player.name + ";" + player.score + ";" );
                for(Card c: player.getHand()){
                    writer.write(c.name + ";");
                }
                if(player.getHand().size() < 8){
                    writer.write("-;");
                }
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
