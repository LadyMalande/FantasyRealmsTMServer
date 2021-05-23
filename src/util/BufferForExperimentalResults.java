package util;

import artificialintelligence.GreedyPlayer;
import interactive.Interactive;
import interactive.TakeCardOfTypeAtTheEnd;
import server.Card;
import server.PlayerOrAI;
import server.Server;

import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for writing and storing values gathered from experiment iterations of the game.
 * @author Tereza Miklóšová
 */
public class BufferForExperimentalResults {
    Server server;
    /**
     * File writer for the time in the round buffer.
     */
    private FileWriter timeInRoundWriter;
    /**
     * File writer for the time in the Copy name and type buffer.
     */
    private FileWriter timeInCopyNameWriter;
    /**
     * File writer for the time in the Change color buffer.
     */
    private FileWriter timeInChangeColorWriter;
    /**
     * File writer for the time in the make move of greedy player buffer.
     */
    private FileWriter timeInMakeMoveGreedyWriter;
    /**
     * File writer for the scores in the round buffer.
     */
    private FileWriter scoreInRoundWriter;
    /**
     * File writer for the general buffer.
     */
    private FileWriter writer;
    /**
     * Another helping class to write results to file.
     */
    private ExperimentOutputCreator eoc;
    /**
     * Selected mode of output and gathering information.
     */
    private int variantOfBufferingResults;

    /**
     * Players playing this game.
     */
    Vector<PlayerOrAI> players;
    /**
     * Generated name for the file with experimental results.
     */
    private String experimentOutputName;
    /**
     * Buffer for storing results from the games.
     */
    private StringBuilder bufferForResults;
    /**
     * Buffer for storing results from the measuring of time in Change color.
     */
    private StringBuilder timeSpentInChangeColor = new StringBuilder();
    /**
     * Buffer for storing results from the measuring of time in Change color.
     */
    private StringBuilder timeSpentInMakeMoveGrredyPlayer = new StringBuilder();
    /**
     * Buffer for storing results from the measuring of time in Change color.
     */
    private StringBuilder timeSpentInCopyNameAndType = new StringBuilder();
    /**
     * Buffer for storing results from the measuring of time in Change color.
     */
    private StringBuilder timeSpentRound = new StringBuilder();
    /**
     * Buffer for storing results from the measuring of time in Change color.
     */
    private StringBuilder scoreInRound = new StringBuilder();

    /**
     * Constructor for this support class.
     * @param variant Which data should be written to files.
     * @param players Players of the game.
     * @param varietyOfPlayers Abbreviations of the players in the game.
     * @param randomOrNot True if the play is random or with original deck.
     */
    public BufferForExperimentalResults(int variant, Vector<PlayerOrAI> players, StringBuilder varietyOfPlayers,
                                        String randomOrNot, Server server){
        this.variantOfBufferingResults = variant;
        eoc = new ExperimentOutputCreator(players);
        experimentOutputName = players.size() + varietyOfPlayers.toString() + randomOrNot;
        this.players = players;
        this.server = server;
        initializeBuffers();
    }

    /**
     * General initializing method deciding which value to initialize.
     */
    private void initializeBuffers(){
        switch(variantOfBufferingResults){
            case 0: initializeScoreMeasuringBuffers();
                break;
            case 1: initializeTimeMeasuringBuffers();
                break;
            case 2:
                initializeScoreMeasuringBuffers();
                initializeTimeMeasuringBuffers();
                break;
        }
    }

    /**
     * Initilize the buffers for time measuring.
     */
    private void initializeTimeMeasuringBuffers(){
        timeSpentRound = new StringBuilder();
        timeSpentInChangeColor = new StringBuilder();
        timeSpentInCopyNameAndType = new StringBuilder();
        timeSpentInMakeMoveGrredyPlayer = new StringBuilder();
    }

    /**
     * Initilize the buffers for score measuring.
     */
    private void initializeScoreMeasuringBuffers(){
        bufferForResults = new StringBuilder();
        scoreInRound = new StringBuilder();
    }

    /**
     * General creating method deciding which file writers to initialize.
     */
    public void createFiles(){
        switch(variantOfBufferingResults){
            case 0: createFWritersForScores();
                break;
            case 1: createFWritersForTimes();
                break;
            case 2:
                createFWritersForScores();
                createFWritersForTimes();
                break;
        }
    }

    /**
     * Decides if the value should be stored to suitable buffer or not by the variant of output.
     * @param text Text to add to buffer.
     */
    public void appendToTimeSpentInCopyNameAndType(String text){
        switch(variantOfBufferingResults){
            case 0:
                break;
            case 1:
            case 2:
                timeSpentInCopyNameAndType.append(text).append("\n");
                break;
        }
    }

    /**
     * Decides if the value should be stored to suitable buffer or not by the variant of output.
     * @param text Text to add to buffer.
     */
    public void appendToTimeSpentInMakeMoveGrredyPlayer(String text){
        switch(variantOfBufferingResults){
            case 0:
                break;
            case 1:
            case 2:
                timeSpentInMakeMoveGrredyPlayer.append(text).append("\n");
                break;
        }
    }

    /**
     * Decides if the value should be stored to suitable buffer or not by the variant of output.
     * @param text Text to add to buffer.
     */
    public void appendToTimeSpentInChangeColor(String text){
        switch(variantOfBufferingResults){
            case 0:
                break;
            case 1:
            case 2:
                timeSpentInChangeColor.append(text).append("\n");
                break;
        }
    }

    /**
     * Decides if the value should be stored to suitable buffer or not by the variant of output.
     * @param text Text to add to buffer.
     */
    public void appendToTimeSpentRound(String text){
        switch(variantOfBufferingResults){
            case 0:
                break;
            case 1:
            case 2:
                timeSpentRound.append(text).append("\n");
                break;
        }
    }

    /**
     * General method deciding which value are to be written to files.
     */
    public void writeToFiles(){
        createFiles();
        switch(variantOfBufferingResults){
            case 0: writeScore();
                break;
            case 1: writeTime();
                break;
            case 2:
                writeScore();
                writeTime();
                break;
        }
        initializeBuffers();
    }

    /**
     * Create file writers for the score buffers.
     */
    private void createFWritersForScores(){
        scoreInRoundWriter = eoc.createFileWriter(experimentOutputName + "_scoresInRound");
        writer = eoc.createFileWriter(experimentOutputName);
    }

    /**
     * Create file writers for the time buffers.
     */
    private void createFWritersForTimes(){
        timeInRoundWriter = eoc.createFileWriter(experimentOutputName + "_timeInRound_ms");
        timeInCopyNameWriter = eoc.createFileWriter(experimentOutputName + "_InCopyName_ms");
        timeInChangeColorWriter = eoc.createFileWriter(experimentOutputName + "_timeInChangeColor_ms");
        timeInMakeMoveGreedyWriter = eoc.createFileWriter(experimentOutputName + "_timeInMakeMoveGreedy_ms");
    }

    /**
     * Write to files the values stored in time buffers.
     */
    private void writeTime(){
        writeToFileWriter(timeInChangeColorWriter, eoc, timeSpentInChangeColor);
        writeToFileWriter(timeInRoundWriter, eoc, timeSpentRound);
        writeToFileWriter(timeInCopyNameWriter, eoc, timeSpentInCopyNameAndType);
        writeToFileWriter(timeInMakeMoveGreedyWriter, eoc, timeSpentInMakeMoveGrredyPlayer);
    }

    /**
     * Write to files the values stored in score buffers.
     */
    private void writeScore(){
        writeToFileWriter(writer, eoc, bufferForResults);
        writeToFileWriter(scoreInRoundWriter, eoc, scoreInRound);
    }

    /**
     * General method to decide if the values are to be written to the buffer.
     * @param cardsOnTable Cards on table.
     */
    public void writeToBuffer(ArrayList<Card> cardsOnTable){
        switch(variantOfBufferingResults){
            case 0:
            case 2:
                writeToGeneralBuffer(cardsOnTable);
                break;
            case 1:
                break;
        }
    }

    /**
     * Method for storing important values about the played game.
     * @param cardsOnTable Cards on table.
     */
    public void writeToGeneralBuffer(ArrayList<Card> cardsOnTable){
        double totalNumberOfRounds = 0;

        for(PlayerOrAI player : players) {
            totalNumberOfRounds += player.getNumberOfRoundsPlayed();
        }
        bufferForResults.append(totalNumberOfRounds).append(";");

        ArrayList<Integer> allEndingCardsOfAllPlayers = new ArrayList<>();
        Set<Integer> setOfCards = new HashSet<>();
        for(PlayerOrAI player : players){
            if(player instanceof GreedyPlayer){
                List<Integer> listIds = ((GreedyPlayer)player).getBestHandAfterTheEnd().stream().map(Card::getId).collect(Collectors.toList());
                allEndingCardsOfAllPlayers.addAll(listIds);
                setOfCards.addAll(listIds);
            }
        }
        if(allEndingCardsOfAllPlayers.size() == setOfCards.size()){
            bufferForResults.append("UNIKATNI;");
        } else{
            bufferForResults.append("DUPLIKATY;");
        }

        for(PlayerOrAI player : players){

            scoreInRound.append(player.getName()).append(";");
            for(Integer score : player.getScoresInRound()){
                scoreInRound.append(score).append(";");
            }
            scoreInRound.append("\n");
            bufferForResults.append(player.getName()).append(";").append(player.getNumberOfRoundsPlayed()).append(";").append(player.getScore()).append(";");
            Card fromNecromancer = null;
            for(Card c: player.getHand()){
                // There is necromancer card in the hand
                if(c.getId() == 13){
                    ArrayList<Interactive> toIterate = new ArrayList<>(c.getInteractives());
                    for(Interactive interactive : toIterate){
                        if(interactive instanceof TakeCardOfTypeAtTheEnd){
                            fromNecromancer = ((TakeCardOfTypeAtTheEnd)interactive).giveCardToTakeFromTable(player.getHand(),
                                    cardsOnTable, server).getKey();

                        }
                    }
                }
                bufferForResults.append(c.getName()).append(";");
            }

            // Include card grabbed by the necromancer card
            if(fromNecromancer == null){
                bufferForResults.append("-;");
            } else{
                bufferForResults.append(fromNecromancer.getName()).append(";");
            }

            Card fromNecromancer2 = null;
            if(player instanceof GreedyPlayer) {
                bufferForResults.append(((GreedyPlayer) player).getNumberOfChangedCardsInIdealHand()).append(";");
                bufferForResults.append(((GreedyPlayer) player).getBestScoreAfterTheEnd()).append(";");
                for (Card c : ((GreedyPlayer) player).getBestHandAfterTheEnd()) {
                    // There is necromancer card in the hand
                    if (c.getId() == 13) {
                        ArrayList<Interactive> toIterate = new ArrayList<>(c.getInteractives());
                        for (Interactive interactive : toIterate) {
                            if (interactive instanceof TakeCardOfTypeAtTheEnd) {
                                fromNecromancer2 = ((TakeCardOfTypeAtTheEnd) interactive).giveCardToTakeFromTable(player.getHand(),
                                        cardsOnTable, server).getKey();
                            }
                        }
                    }
                    bufferForResults.append(c.getName()).append(";");
                }

                // Include card grabbed by the necromancer card
                if (fromNecromancer2 == null) {
                    bufferForResults.append("-;");
                } else {
                    bufferForResults.append(fromNecromancer2.getName()).append(";");
                }
            }
        }

        bufferForResults.append("Table:;");
        for(Card c : cardsOnTable) bufferForResults.append(c.getName()).append(";");
        // Writes the content to the file
        bufferForResults.append("\n");
    }

    /**
     * Write the buffers to file writer.
     * @param writer File writer to write to.
     * @param eoc Support class for writing to output file.
     * @param sb The buffer to write to file.
     */
    private void writeToFileWriter(FileWriter writer, ExperimentOutputCreator eoc, StringBuilder sb){
        players.sort(Comparator.comparing(PlayerOrAI::getName));
        eoc.writeToFileWriter(writer, sb);
    }
}
