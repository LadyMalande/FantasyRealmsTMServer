package server;

import artificialintelligence.ArtificialIntelligenceInterface;
import artificialintelligence.GreedyPlayer;
import artificialintelligence.GreedyStopPlanning;
import artificialintelligence.LearningPlayer;
import util.BufferForExperimentalResults;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * The main logic class for the server. Directs the flow of the game. Holds information about the game.
 * @author Tereza Miklóšová
 */
public class Server extends Thread {
    /**
     * AI types known to the server.
     */
    private final String[] AITypes = {"GREEDY", "LEARNING","PLANNING", "PAIRS"};
    /**
     * Number of the cards on the table that triggers the end of the game and score counting.
     */
    public static int END_GAME_NUMBER_OF_CARDS = 10;
    /**
     * Number of cards player has in his hand when its not his turn.
     */
    public static int CARDS_ON_HAND = 7;

    /**
     * Arguments passed by the command line.
     */
    private String[] args;

    /**
     * Cards on the table.
     */
    public ArrayList<Card> cardsOnTable;

    /**
     * True if there are players so the AI needs to pause between making decisions so the player can notice the changes.
     */
    boolean needDelay;

    /**
     * Deck full of cards for the game.
     */
    private Deck deck;

    /**
     * Server socket on which to listen to.
     */
    ServerSocket ss;

    /**
     * Socket on which the communication is going on.
     */
    public Socket s;

    /**
     * Text with abbreviations of the AI types to form the name for the experimential file.
     */
    StringBuilder varietyOfPlayersAI;
    /**
     * True if the player wants to play with random deck.
     */
    String randomOrNot;
    /**
     * Tells the number of players that the server is waiting for.
     */
    public final AtomicInteger maxClients = new AtomicInteger(6);
    /**
     * Players that joined the game.
      */
    Vector<PlayerOrAI> players = new Vector<>();
    /**
     * Number of already counted scores at the end of the game.
     */
    public AtomicInteger numberOfCountedScores = new AtomicInteger(0);

    /**
     * Holds card that real player is trying to put on the table. If null the server waits for the players answer.
     */
    public AtomicReference<Card> cardToTableFromClient = new AtomicReference<>();
    /**
     * Number of AI to play the game.
     */
    int numberOfAI = 0;
    /**
     * Counter of played rounds of the game in one game.
     */
    int numberOfRounds = 0;
    /**
     * Number of games to play if only AI is playing.
     */
    int numberOfGamesToPlay = 0;
    /**
     * Cards that still might be in the deck.
     */
    private ArrayList<Card> mightBeInDeck = new ArrayList<>();
    /**
     * Counter of the games played for the output to file flush.
     */
    public int ithRound = 0;


    private boolean running = true;

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    private AtomicBoolean sentAllScore = new AtomicBoolean(true);

    /**
     * Buffers for experimental results.
     */
    private BufferForExperimentalResults bufferForResult;

    /**
     * Gets {@link Server#mightBeInDeck}.
     * @return {@link Server#mightBeInDeck}
     */
    public ArrayList<Card> getMightBeInDeck(){
        return this.mightBeInDeck;
    }

    /**
     * Gets {@link Server#needDelay}.
     * @return {@link Server#needDelay}
     */
    public boolean getNeedDelay(){
        return needDelay;
    }

    /**
     * Gets {@link Server#deck}.
     * @return {@link Server#deck}
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Sets {@link Server#deck}.
     * @param random True if the player wants to play with random deck. False if he wants to play with the original one.
     */
    public void setDeck(boolean random){
        deck.setDeck(random);
    }

    /**
     * Sets {@link Server#numberOfAI}.
     * @param number {@link Server#numberOfAI}
     */
    public void setAI(int number){
        for(int i = 0; i < number; i++){
            try {
                varietyOfPlayersAI.append("G");
                int parameterOfGreedy = 0;
                GreedyPlayer ai = new GreedyPlayer(this, parameterOfGreedy, parameterOfGreedy + "_GREEDY_" + i);
                players.add(ai);
            } catch(CloneNotSupportedException ex){
                ex.printStackTrace();
            }
        }
        numberOfAI = number;
        int newMaxPlayers = maxClients.get() - number;
        maxClients.getAndSet(newMaxPlayers);
    }

    /**
     * Gets {@link Server#players}.
     * @return {@link Server#players}
     */
    public Vector<PlayerOrAI> getPlayers(){
        return players;
    }

    /**
     * Decides if there are only AI players or not. If there are only AI pllayers there is no delay between
     * agent's decision and the executing of counted move.
     */
    private void decideNeedDelay() {
        if (players.size() > 2) {
            // There is at least 1 real player playing against more than 1 AI and needs to slow down their processing
            // to see what is happening on the table
            needDelay = numberOfAI > 1 && (players.size() - numberOfAI) > 0;
        }else{
                needDelay = false;
        }
    }

    /**
     * Gets {@link Server#bufferForResult}.
     * @return {@link Server#bufferForResult}
     */
    public BufferForExperimentalResults getBufferForResult() {
        return bufferForResult;
    }

    /**
     * Constructor for the server.
     * @param ss Server socket for the server.
     * @param args Arguments passed in the command line.
     */
    public Server(ServerSocket ss, String[] args){
        this.ss = ss;
        this.args = args;

        varietyOfPlayersAI = new StringBuilder();
    }

    /**
     * The server starts listening to new-coming clients. Or reads the command line arguments and does experiment with AI.
     */
    public void run()
    {

        while(running) {
            if(sentAllScore.get()){

                players.clear();
                deck = new Deck();
                deck.initializeOriginal();

                cardsOnTable = new ArrayList<>();


                // Read the arguments from the command line
                try {
                    readArgs(args);
                } catch (NotAIType exception) {
                    System.out.println("Error with AI types in parameters of program.");
                }


                // NOT an AI experiment
                if (args.length == 0) {
                    waitForTheClients();
                }
                bufferForResult = new BufferForExperimentalResults(0, players, varietyOfPlayersAI, randomOrNot, this);

                decideNeedDelay();

                sentAllScore.set(false);
                // There are only AI players. Itearate through all the games.
                if (numberOfGamesToPlay != 0) {
                    System.out.println("Experimenty s AI");
                    experimentWithAI();

                } else {
                    // There is also a real player
                    decideStartingPlayer();
                    startTheGame();
                }
            }
        }
    }

    /**
     * Picks a starting player and rotates the players in the vector so that the starting player is at position 0.
     */
    private void decideStartingPlayer(){
        Random randomGeneratorForPlayers = new Random();
        //System.out.println("Number of players " + players.size());
        int index2 = randomGeneratorForPlayers.nextInt(players.size());
        players.elementAt(index2).setPlaying(true);
        rotateRight(players, players.size() - index2);
    }

    /**
     * Executes a loop of games with AI. Writes scores to files if requested.
     */
    private void experimentWithAI(){
        bufferForResult = new BufferForExperimentalResults(0,players, varietyOfPlayersAI, randomOrNot, this);
        for(ithRound = 0; ithRound < numberOfGamesToPlay; ithRound++){
            System.out.println("Starting game number " + ithRound + "---------------------------------------------------------");
            long startTime = System.nanoTime();
            deck = new Deck();
            deck.initializeOriginal();
            cardsOnTable.clear();
            if(randomOrNot.equals("1")){
                deck.setDeck(true);
            } else{
                deck.setDeck(false);
            }
            mightBeInDeck = deck.getDeck();
            Collections.shuffle(deck.getDeck());
            for(PlayerOrAI ai : players){
                try {
                    ai.getInitCards();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
            numberOfRounds = 0;

            decideStartingPlayer();

            numberOfCountedScores = new AtomicInteger(0);
            while(cardsOnTable.size() < END_GAME_NUMBER_OF_CARDS){
                for(PlayerOrAI player : players){
                    if(cardsOnTable.size() < END_GAME_NUMBER_OF_CARDS) {
                        try {
                            putCardOnTable(((ArtificialIntelligenceInterface) player).performMove(cardsOnTable));
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            while(players.size() > numberOfCountedScores.get()){
                System.out.println(players.size() + " != " + numberOfCountedScores.get());
            }

            long elapsedTime = (System.nanoTime() - startTime) / 1000000;

            System.out.println("Execution time for this game: " + elapsedTime);
            bufferForResult.appendToTimeSpentRound(String.valueOf(elapsedTime));


            long startTimeForEndCountings = System.nanoTime();
            //countBetterHandFromState();
            long elapsedTimeForEndCountings = (System.nanoTime() - startTimeForEndCountings) / 1000000;
            //System.out.println("Execution time for aftergame: " + elapsedTimeForEndCountings);

            // Write the results of the round to the buffer
            bufferForResult.writeToBuffer(cardsOnTable);
            // Once in a while flush those buffers to their output files.
            if(ithRound % 100 == 49){
                bufferForResult.writeToFiles();
            }
        }
    }

    /**
     * Listen to new-coming players on the socket. Create new ClientHandlers for those that come.
     */
    private void waitForTheClients(){
        int i = 0;
        System.out.println("Waiting for clients...");
        // running infinite loop for getting
        // client request
        while (true)
        {
            synchronized (maxClients) {
                if (maxClients.get() == i) {
                    System.out.println("Max Clients == "+ i + " , so we break from while(true) in Server.run()");
                    break;
                }
                System.out.println("max clients : " + maxClients);
                // Accept the incoming request
                try {

                    s = ss.accept();

                    // obtain input and output streams
                    ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
                    ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());

                    // Create a new handler object for handling this request.
                    ClientHandler mtch = new ClientHandler(s, "client " + i, dis, dos, this);

                    // Create a new Thread with this object.
                    Thread t = new Thread(mtch);

                    // add this client to active clients list
                    players.add(mtch);
                    System.out.println("players size in acceptClients: " + players.size());
                    // Generate starting hand
                    varietyOfPlayersAI.append("P");

                    // start the thread.
                    t.start();
                    i++;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    System.out.println("Before maxClients.wait() in Server.run()");
                    maxClients.wait();
                    System.out.println("After maxClients.wait() in Server.run()");
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException while waiting");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Rotates the Vector right by given shift.
     * @param tArrayList Vector of players to shift.
     * @param shift How much to shift.
     */
    private void rotateRight(Vector<PlayerOrAI> tArrayList, int shift){
        if(!tArrayList.isEmpty()){
            PlayerOrAI item;
            for(int i = 0; i < shift; i++){
                item = tArrayList.remove(tArrayList.size()-1);
                tArrayList.add(0, item);
            }
        }
    }

    /**
     * Read the command line arguments and set the variables.
     * @param args Command line arguments.
     * @throws NotAIType If there are unknown AI types this exception is thrown.
     */
    private void readArgs(String[] args) throws NotAIType{
        if(args.length != 0) {
            // recognize arguents: number of players, type of AI (number of names of AI as number of players)
            for (int ar = 0; ar < args.length; ar++) {
                if (ar == 0) {
                    try {
                        numberOfAI = Integer.parseInt(args[ar]);
                        maxClients.getAndSet(numberOfAI);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid arguments format. Run the program again with proper arguments in following format: numberOfPlayers, nameOfAI (times numberOfPlayers). Terminating the program.");
                    }
                }

                if (ar == 1) {
                    // read what deck should be used for the experiment
                    if ("RANDOM".equals(args[ar])) {
                        setDeck(true);
                        randomOrNot = "1";
                    } else {
                        setDeck(false);
                        randomOrNot = "0";

                    }
                    mightBeInDeck = deck.getDeck();
                }

                if (ar > 1 && ar < 1 + numberOfAI + 1) {
                    if (Stream.of(AITypes).anyMatch(args[ar]::startsWith)) {
                        if (args[ar].startsWith("GREEDY_")) {
                            try {
                                varietyOfPlayersAI.append("G");
                                int parameterOfGreedy = Integer.parseInt(args[ar].substring(7));
                                GreedyPlayer ai;
                                if (parameterOfGreedy != 0 && parameterOfGreedy != 1) {
                                    varietyOfPlayersAI.append(parameterOfGreedy);
                                    ai = new GreedyPlayer(this, parameterOfGreedy, ar + "_GREEDY_insight" + parameterOfGreedy);

                                } else {
                                    ai = new GreedyPlayer(this, parameterOfGreedy, ar + "_GREEDY");

                                }
                                players.add(ai);
                            } catch (NumberFormatException | CloneNotSupportedException e) {
                                System.out.println("Invalid arguments format of GREEDY type of AI. Correct type is: GREEDY_0 or GREEDY_10 etc. Run the program again with proper arguments. Terminating the program.");

                            }

                        } else if (args[ar].startsWith("LEARNING")) {
                            varietyOfPlayersAI.append("M");
                            try {
                                LearningPlayer ai = new LearningPlayer(this, ar + "_PairLearning", 0.2, 0.4);
                                players.add(ai);
                                System.out.println("Size of players: " + players.size());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (args[ar].startsWith("PLANNING")) {
                            varietyOfPlayersAI.append("L");
                            try {
                                GreedyStopPlanning ai = new GreedyStopPlanning(this, ar + "_Planning", 2, 0.2, 0.05);
                                players.add(ai);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else if (args[ar].startsWith("PAIRS")) {
                            varietyOfPlayersAI.append("R");
                            try {
                                LearningPlayer ai = new LearningPlayer(this, ar + "_PairAgent", 0.2, 0.4);
                                players.add(ai);
                                System.out.println("Size of players: " + players.size());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            throw new NotAIType("Argument of index " + ar + " is not a type of AI. Make right input and restart program.");
                        }
                    }

                }
                if (ar == 1 + numberOfAI + 1) {
                    numberOfGamesToPlay = Integer.parseInt(args[ar]);
                }
            }
        }
    }


    /**
     * Puts the card to table. Informs the clients about this event.
     * @param c Card that is being put on the table. (To the table array.)
     */
    public void putCardOnTable(Card c){
        // Tell all clients to put this card on tables
        mightBeInDeck.remove(c);
        numberOfRounds++;
        if(c != null){
            cardsOnTable.add(c);
            players.forEach(p -> {
                try {
                    p.putCardOnTable(c);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            });
        }
        if(cardsOnTable.size() == END_GAME_NUMBER_OF_CARDS){
            //System.out.println("Before sendEndGame in putCardOnTable");
            sendEndGame();
        }
    }

    /**
     * Starts the game with the real players. Or mixed with real and AI ones.
     */
    private void startTheGame(){
        System.out.println("NumberOfPlayers " + players.size());
        cardsOnTable.clear();
        // Tell all the clients who is the starting player and set the playing player, give all player names
        players.forEach(p -> p.sendNamesInOrder(giveNamesInOrder(p)));
        numberOfCountedScores.set(0);
        while(cardsOnTable.size() < 10){

            for(PlayerOrAI player : players){
                if(cardsOnTable.size() < 10) {
                    cardToTableFromClient.set(null);
                    try {
                        if (player instanceof ArtificialIntelligenceInterface) {
                            System.out.println("Hraje agent");
                            putCardOnTable(((ArtificialIntelligenceInterface) player).performMove(cardsOnTable));
                        } else if (player instanceof ClientHandler) {

                            //noinspection StatementWithEmptyBody
                            while (cardToTableFromClient.get() == null) {
                            }
                            System.out.println("Hraje clovek");
                            putCardOnTable(cardToTableFromClient.get());
                        }
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        System.out.println("End of server startTheGame() cards on table " +  cardsOnTable.size());
    }

    /**
     * Give first card from the top of the deck.
     * @return Card from the deck.
     */
    public Card drawCardFromDeck(){
        try {
            return deck.getDeck().remove(0);
        } catch(IndexOutOfBoundsException ex){
            System.out.println("Stul ("+ cardsOnTable.size() + "): ");
            for(Card c : cardsOnTable){
                System.out.println(c.getName() + ",");
            }
            System.out.println();
            for(PlayerOrAI pl : players){
                System.out.println("Agentova ruka ("+ pl.getHand().size() + "): ");
                for(Card c : pl.getHand()){
                    System.out.println(c.getName() + ",");
                }
                System.out.println();

            }
            System.exit(-1);
        }
        return null;
    }

    /**
     * Takes the card from the table. Informs the clients about this event.
     * @param c The card that is being taken from the table.
     */
    public void takeCardFromTable(Card c){
        // Tell all clients to erase this card from tables
        cardsOnTable.remove(c);
        players.forEach(p -> p.eraseCardFromTable(c));
    }

    /**
     * Sends command to clients to disable all their controls so none can play anymore.
     */
    private void sendEndGame(){
        //Tell all clients to disable all buttons, end of the game, start counting hand
        players.forEach(PlayerOrAI::endGame);
    }

    /**
     * When another score is done counting at the end of the game this method is called to track the progression.
     */
    public void increaseCountedScoreNumber(){
        int nowCounted = numberOfCountedScores.incrementAndGet();
        System.out.println("increaseCountedScore now counted = " + nowCounted +" players size = " + players.size());
        if(players.size() == nowCounted){
            System.out.println("increaseCountedScore now counted = " + nowCounted +" players size = " + players.size());

            while(players.stream().anyMatch(player -> player.getScore() < -666)){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sendCountedScore();
            sentAllScore.set(true);
            System.out.println("sentAllScore = true " + nowCounted +" players size = " + players.size());
        }
    }

    /**
     * Sends the details of the counted scores to players.
     */
    private void sendCountedScore(){
        players.forEach(p -> p.sendScore(gatherScores(p)));
    }

    /**
     * Makes a text of player names and tags the one that begins the game.
     * @param client Which client receives the text.
     * @return The text of player names, the beginning player is tagged. First element
     * is the client that is receiving the text.
     */
    public String giveNamesInOrder(PlayerOrAI client){
        StringBuilder names = new StringBuilder();
        int indexOfClient = players.indexOf(client);
        int gotNames = 0;
        while(gotNames != players.size()){

            if(players.size() <= indexOfClient){
                indexOfClient = 0;
            }
            if(players.elementAt(indexOfClient).getPlaying()){
                names.append("$&$START$&$");
            }
            names.append(players.elementAt(indexOfClient).getName()).append("#");
            gotNames++;
            indexOfClient++;
        }
        names.deleteCharAt(names.length()-1);
        return names.toString();
    }

    /**
     * Makes a text containing all counted scores from all clients and agents in regard to the client that
     * is receiving the text.
     * @param client Target client of the made text.
     * @return Text with ladder at the end of the game.
     */
    public String gatherScores(PlayerOrAI client){
        StringBuilder text = new StringBuilder();
        int indexOfClient = players.indexOf(client);
        countRanks();
        boolean putTable = true;
        int gotScores = 0;
        while(gotScores != players.size()){
            if(players.size() <= indexOfClient){
                indexOfClient = 0;
            }
            if(putTable){
                text.append(players.elementAt(indexOfClient).getScoreTable()).append("#");
            }
            text.append(players.elementAt(indexOfClient).getRank()).append(") ").append(players.elementAt(indexOfClient).getScore()).append("#");
            gotScores++;
            indexOfClient++;
            putTable = true;
        }
        text.deleteCharAt(text.length()-1);

        return text.toString();
    }

    /**
     * Decides the ladder at the end of the game. Sets ranks of all players and agents.
     */
    private void countRanks(){
        PlayerOrAI[] playerScore = new PlayerOrAI[players.size()];
        for(int i = 0; i < players.size();i++){
            for(int j = 0; j <= i; j++ ) {
                if(playerScore[j] == null) {
                    playerScore[j] = players.elementAt(i);
                }else if (players.elementAt(i).getScore() > playerScore[j].getScore()) {
                    if (players.size() - 1 - j >= 0)
                        System.arraycopy(playerScore, j, playerScore, j + 1, players.size() - 1 - j);
                    playerScore[j] = players.elementAt(i);
                    break;
                }
            }
        }
        int rank = 1;
        for(int i = 0; i < players.size();i++){
            playerScore[i].setRank(rank);
            if(i < players.size()-1){
                if(playerScore[i].getScore() == playerScore[i+1].getScore()){
                    //TODO Set other conditions of victory other than total score
                } else{
                    rank++;
                }
            }
        }
    }

}
