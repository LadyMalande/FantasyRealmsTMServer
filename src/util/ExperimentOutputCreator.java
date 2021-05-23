package util;

import artificialintelligence.Coefficients;
import artificialintelligence.StateStopPlanning;
import server.PlayerOrAI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

/**
 * Support class for writing texts to output files.
 * @author Tereza Miklóšová
 */
public class ExperimentOutputCreator {
    /**
     * Players playing the game.
     */
    Vector<PlayerOrAI> players;

    /**
     * Constructor of the class.
     * @param list List of player playing the game.
     */
    public ExperimentOutputCreator(Vector<PlayerOrAI> list){
        this.players = list;
    }

    /**
     * Creates the output file by given name.
     * @param filename Name of the created file.
     * @return File with given name.
     */
    public File createOutputFile(String filename){
        try {
            File myFile = new File(filename + ".txt");
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

    /**
     * Writes coefficient values to the file.
     * @param file File to write the coefficients to.
     * @param map Map of coefficients and their values.
     */
    public void writeCoefficients(File file, Map<Pair<Integer, Integer>, Coefficients> map){
        try {
            FileWriter writer = new FileWriter(file);

            // Write objects to file
            for(Map.Entry<Pair<Integer,Integer>, Coefficients> entry : map.entrySet()){
                writer.write(entry.getKey().getKey().toString() + ";");
                writer.write(entry.getKey().getValue().toString() + ";");
                writer.write((entry.getValue().getActualValue()) + ";");
                writer.write(String.valueOf(entry.getValue().getActalValueCoefficient()));
                writer.write("\n");
                System.out.println("ID: " + entry.getKey() + " Val: " + (entry.getValue()).getActalValueCoefficient());

            }

            // Writes the content to the file

            writer.flush();
            writer.close();
        } catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Unable to send experiment data to file.");
        }
    }

    public void writePairCoefficients(File file, Map<Pair<Integer, Integer>, Double> map){
        try {
            FileWriter writer = new FileWriter(file);

            // Write objects to file
            for(Map.Entry<Pair<Integer,Integer>, Double> entry : map.entrySet()){
                writer.write(entry.getKey().getKey().toString() + ";");
                writer.write(entry.getKey().getValue().toString() + ";");
                writer.write(String.valueOf(entry.getValue()));
                writer.write("\n");
                //System.out.println("IDs: " + entry.getKey() + " Val: " + (entry.getValue()));
            }

            // Writes the content to the file

            writer.flush();
            writer.close();
        } catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Unable to send experiment data to file.");
        }
    }

    public void writeCardsCoefficients(File file, Map<Integer, Double> map){
        try {
            FileWriter writer = new FileWriter(file);

            // Write objects to file
            for(Map.Entry<Integer, Double> entry : map.entrySet()){
                writer.write(entry.getKey().toString() + ";");
                writer.write(String.valueOf(entry.getValue()));
                writer.write("\n");
                //System.out.println("ID: " + entry.getKey() + " Val: " + (entry.getValue()));
            }
            // Writes the content to the file
            writer.flush();
            writer.close();
        } catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Unable to send experiment data to file.");
        }
    }

    /**
     * Writes coefficient values to the file.
     * @param file File to write the coefficients to.
     * @param map Map of coefficients and their values.
     */
    public void writeCoefficientsStateStopPlanning(File file, ArrayList<StateStopPlanning> map){
        try {
            FileWriter writer = new FileWriter(file, false);

            // Write objects to file
            for(StateStopPlanning entry : map){
                writer.write(entry.getNumberOfCardsOnTable() + ";");
                writer.write(entry.getNumberOfPlayers() + ";");
                writer.write(entry.getCardsTakenLastRound() + ";");
                writer.write(entry.getPlayerScoreApprox() + ";");
                writer.write(String.valueOf(entry.getWinrate()));
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

    /**
     * Creates file writer for friting to file.
     * @param experimentName Name of the experiment. This namem will be given to connected file.
     * @return FileWriter for writing to connected file.
     */
    public FileWriter createFileWriter(String experimentName){
        File file = createOutputFile(experimentName);
        try{
            return new FileWriter(file, true);
        } catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Unable to send experiment data to file.");
            return null;
        }

    }

    /**
     * Push the text to the file.
     * @param writer Writer to connect the text with file.
     */
    public void flushFileWriter(FileWriter writer){
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes given text to given FileWriter.
     * @param writer Writer to use for writing to the file.
     * @param sb Text we want to push to file.
     */
    public void writeToFileWriter(FileWriter writer, StringBuilder sb){
        try {
            writer.write(sb.toString());
            flushFileWriter(writer);
        } catch(IOException ex){
            ex.printStackTrace();
            System.out.println("Unable to send experiment data to file.");
        }
    }

}
