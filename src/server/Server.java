package server;

import artificialintelligence.ArtificialIntelligenceInterface;
import artificialintelligence.CacheMap;
import artificialintelligence.GreedyPlayer;
import artificialintelligence.LearningPlayer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


// server.Server class
public class
Server extends Thread
{
    private final String[] AITypes = {"GREEDY", "MC"};
    public int END_GAME_NUMBER_OF_CARDS = 10;
    public final int CARDS_ON_HAND = 7;

    private String[] args;
    public ArrayList<Card> cardsOnTable;
    boolean gameOver = false;
    boolean allPlayersInitialized = false;
    boolean needDelay;
    PlayerOrAI currentPlayer;
    public Deck deck;
    ServerSocket ss;
    public Socket s;
    String experimentOutputName;
    StringBuilder varietyOfPlayersAI;
    String randomOrNot;
    Random randomGenerator;
    public AtomicInteger maxClients = new AtomicInteger(6);
    // Vector to store active clients
    Vector<PlayerOrAI> players = new Vector<>();
    public AtomicInteger numberOfCountedScores = new AtomicInteger(0);
    int numberOfAI = 0;
    public Vector<PlayerOrAI> getPlayers(){
        return players;
    }
    int numberOfRounds = 0;
    int numberOfGamesToPlay = 0;
    StringBuilder startingDeck;
    ArrayList<Card> mightBeInDeck = new ArrayList<>();
    public int ithRound = 0;
    CacheMap cacheMap;
    StringBuilder bufferForResults;
    // counter for clients


    public ArrayList<Card> getMightBeInDeck(){
        return this.mightBeInDeck;
    }

    public boolean getNeedDelay(){
        return needDelay;
    }

    private void decideNeedDelay() {
        if (players.size() > 2) {
            if (numberOfAI > 1 && (players.size() - numberOfAI) > 0) {
                // There is at least 1 real player playing against more than 1 AI and needs to slow down their processing
                // to see what is happening on the table
                needDelay = true;
            } else {
                needDelay = false;
            }
        }else{
                needDelay = false;
            }
    }

    public Server(ServerSocket ss, String[] args){
        this.ss = ss;
        this.args = args;
        varietyOfPlayersAI = new StringBuilder();
        bufferForResults = new StringBuilder();
    }
    public void run()
    {
        deck = new Deck();
        deck.initializeOriginal();
    startingDeck = new StringBuilder();

        //deck.initializeRandom();
        cardsOnTable = new ArrayList<>();

        try {
            readArgs(args);
        } catch(NotAIType exception){
            System.out.println("Error with AI types in parameters of program.");
        }

        // NOT an AI experiment
        if(args.length == 0){
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
                    System.out.println(maxClients);
                    // Accept the incoming request
                    try {

                        s = ss.accept();

                        //System.out.println("New client request received : " + s);

                        // obtain input and output streams
                        ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
                        ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());

                        //System.out.println("Creating a new handler for this client...");

                        // Create a new handler object for handling this request.
                        ClientHandler mtch = new ClientHandler(s, "client " + i, dis, dos, this);

                        // Create a new Thread with this object.
                        Thread t = new Thread(mtch);

                        //System.out.println("Adding this client to active client list");

                        // add this client to active clients list
                        players.add(mtch);
                        // Generate starting hand
                        varietyOfPlayersAI.append("P");



                        // start the thread.
                        t.start();
                        //System.out.println("Max clients can be: " + maxClients);
                        i++;
                        //System.out.println("We just increased player number with i++, now its: " + i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    try {
                        //System.out.println("Before maxClients.wait() in Server.run()");
                        maxClients.wait();
                        //System.out.println("After maxClients.wait() in Server.run()");
                    } catch (InterruptedException e) {
                        System.out.println("InterruptedException while waiting");
                        e.printStackTrace();
                    }
                }
            }
        }
        if(numberOfGamesToPlay != 0){
            decideNeedDelay();
            ExperimentOutputCreator eoc = new ExperimentOutputCreator(players, startingDeck);
            FileWriter writer = eoc.createFileWriter(experimentOutputName);
            for(ithRound = 0; ithRound < numberOfGamesToPlay; ithRound++){
                if(ithRound % 100 == 0){
                    writer = eoc.createFileWriter(experimentOutputName);
                }
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
                startingDeck = new StringBuilder();
                Collections.shuffle(deck.getDeck());
                for(Card c : deck.getDeck()){
                    startingDeck.append(c.getName() + ";");
                }
                //System.out.println("Number of cards in deck " + deck.getDeck().size());
                for(PlayerOrAI ai : players){
                    try {
                        ai.getInitCards();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                numberOfRounds = 0;
                //System.out.println("Number of cards in deck after init give cards" + deck.getDeck().size());

                Random randomGeneratorForPlayers = new Random();
                int index2 = randomGeneratorForPlayers.nextInt(players.size());
                players.elementAt(index2).setPlaying(true);
                currentPlayer = players.elementAt(index2);
                //for(PlayerOrAI pl:players){System.out.print(pl.getName());}
                rotateRight(players, players.size() - index2);
                //for(PlayerOrAI pl:players){System.out.print(">" + pl.getName());}

                experimentOutputName = players.size() + varietyOfPlayersAI.toString() + randomOrNot;
                numberOfCountedScores = new AtomicInteger(0);
                while(cardsOnTable.size() < END_GAME_NUMBER_OF_CARDS){
                    for(PlayerOrAI player : players){
                        try {
                            putCardOnTable(((ArtificialIntelligenceInterface)player).performMove(cardsOnTable));
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                long elapsedTime = System.nanoTime() - startTime;
                System.out.println("Execution time for this game: "
                        + elapsedTime/1000000);
                //startTheGame();
               //System.out.println("End of Server game number " + i);
                writeToBuffer();
                if(ithRound % 100 == 99){
                    writeToFileWriter(writer, eoc);
                    eoc.flushFileWriter(writer);

                    bufferForResults = new StringBuilder();
                }
            }
            eoc.flushFileWriter(writer);
        } else {
            //System.out.println("After end of while players < maxplayers, BEFORE RANDOM STARTING PLAYER");
            Random randomGeneratorForPlayers = new Random();
            //System.out.println("players.size() == " + players.size() );
            int index2 = randomGeneratorForPlayers.nextInt(players.size());
            players.elementAt(index2).setPlaying(true);
            currentPlayer = players.elementAt(index2);
            for(PlayerOrAI p: players){
                //System.out.println("Player name in initialize " + p.getName() + " is playing " + p.getPlaying());
            }
            //System.out.println("Starting player is player <" + players.elementAt(index2).getName() + ">");

            experimentOutputName = players.size() + varietyOfPlayersAI.toString() + randomOrNot;
            decideNeedDelay();

            //TuplesMapCreator tmc = new TuplesMapCreator(new int[]{1, 2, 3, 4, 5, 6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54});
            //tmc.makeStateMap();
            //StateMapCreator smc = new StateMapCreator(new int[]{1, 2, 3, 4, 5, 6,7,8,9},3,2);
            //smc.makeStateMap();

            startTheGame();

            //System.out.println("Players size" + players.size());
            //System.out.println("End of Server");
            writeToFile();

        }

    }

    private void rotateRight(Vector<PlayerOrAI> tArrayList, int shift){
        if(!tArrayList.isEmpty()){
            PlayerOrAI item = null;
            for(int i = 0; i < shift; i++){
                item = tArrayList.remove(tArrayList.size()-1);
                tArrayList.add(0, item);
            }
        }
    }

    private void writeToBuffer(){
        double totalNumberOfRounds = 0;

        for(PlayerOrAI player : players) {
            totalNumberOfRounds += player.getNumberOfRoundsPlayed();
        }
        bufferForResults.append(totalNumberOfRounds + ";");

        for(PlayerOrAI player : players){

            bufferForResults.append(player.getName() + ";" + player.getNumberOfRoundsPlayed() + ";"+ player.score + ";" );

            for(Card c: player.getHand()){
                bufferForResults.append(c.name + ";");
            }
            if(player.getHand().size() < 8){
                for(int i = player.getHand().size(); i < 8; i++){
                    bufferForResults.append("-;");
                }
            }
        }
        // Writes the content to the file
        bufferForResults.append("\n");
    }

    private void writeToFileWriter(FileWriter writer, ExperimentOutputCreator eoc){
        players.sort(Comparator.comparing(PlayerOrAI::getName));
        eoc.writeToFileWriter(writer, bufferForResults);

    }

    private void writeToFile(){
        players.sort(Comparator.comparing(PlayerOrAI::getName));
        ExperimentOutputCreator eoc = new ExperimentOutputCreator(players, startingDeck);

        eoc.createOutput(experimentOutputName);
    }

    private void readArgs(String[] args) throws NotAIType{
        if(args.length != 0){
            // recognize arguents: number of players, type of AI (number of names of AI as number of players)

            for(int ar = 0; ar < args.length; ar++){
                if(ar == 0) {
                    try {
                        numberOfAI = Integer.parseInt(args[ar]);
                        //System.out.println("Number of AI players: " + numberOfAI);
                        maxClients.getAndSet(numberOfAI);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid arguments format. Run the program again with proper arguments in following format: numberOfPlayers, nameOfAI (times numberOfPlayers). Terminating the program.");

                    }
                }

                if(ar == 1){
                    // read what deck should be used for the experiment
                    if ("RANDOM".equals(args[ar])) {
                        deck.setDeck(true);
                        randomOrNot= "1";
                    } else {
                        deck.setDeck(false);
                        randomOrNot = "0";

                    }
                    //System.out.println(args[ar]);
                    mightBeInDeck = deck.getDeck();
                }

                if(ar > 1 && ar < 1 + numberOfAI + 1){
                    if (Stream.of(AITypes).anyMatch(args[ar]::startsWith)){
                        if(args[ar].startsWith("GREEDY_")){
                            cacheMap = new CacheMap();
                            try {
                                varietyOfPlayersAI.append("G");
                                int parameterOfGreedy = Integer.parseInt(args[ar].substring(7));
                                GreedyPlayer ai = new GreedyPlayer(this, parameterOfGreedy, ar + "_GREEDY", cacheMap);
                                players.add(ai);
                            } catch (NumberFormatException | CloneNotSupportedException e) {
                                System.out.println("Invalid arguments format of GREEDY type of AI. Correct type is: GREEDY_0 or GREEDY_10 etc. Run the program again with proper arguments. Terminating the program.");

                            }

                        } else if(args[ar].startsWith("MC")){
                            varietyOfPlayersAI.append("M");
                            try {
                                LearningPlayer ai = new LearningPlayer(this, ar + "_MonteCarlo", 0.2, 0.05);
                                players.add(ai);
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else{
                        throw new NotAIType("Argument of index " + ar + " is not a type of AI. Make right input and restart program.");
                    }
                } else{
                    //TODO insert other types of AI to recognize on args input here
                }
                if(ar == 1+numberOfAI +1){
                    numberOfGamesToPlay = Integer.parseInt(args[ar]);
                }
            }
        }
    }


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
        //System.out.println("Cards on table: " + cardsOnTable.size());
        if(cardsOnTable.size() == END_GAME_NUMBER_OF_CARDS){
            //System.out.println("Ending game, 10 cards on table");
            sendEndGame();
        }
        /*
        else if(cardsOnTable.size() < END_GAME_NUMBER_OF_CARDS){
            //System.out.println("-----Telling all clients to put this card on table: " + c.name + " NOW cards on table: " + cardsOnTable.size());
            try{
                setNextPlayer();
            } catch(CloneNotSupportedException ex){
                ex.printStackTrace();
            }
        }
*/

        //System.out.println("????__________________________________________>>>><<<???");
        //System.out.println("????__________________________________________>>>><<<???");
        //System.out.println("????__________________________________________>>>><<<???");
    }

    private void startTheGame(){
        cardsOnTable.clear();
        // Tell all the clients who is the starting player and set the playing player, give all player names
        players.forEach(p -> p.sendNamesInOrder(giveNamesInOrder(p)));
    }

    public Card drawCardFromDeck(){
        return deck.getDeck().remove(0);
    }

    public void takeCardFromTable(Card c){
        // Tell all clients to erase this card from tables
        cardsOnTable.remove(c);
        players.forEach(p -> p.eraseCardFromTable(c));
        //System.out.println("Telling all clients to erase this card from table: " + c.name);
    }

    private void sendEndGame(){
        //Tell all clients to disable all buttons, end of the game, start counting hand

        players.forEach(p -> p.endGame());

    }


    public void increaseCountedScoreNumber(){
        int nowCounted = numberOfCountedScores.incrementAndGet();
        //System.out.println("nowCounted incremented in increaseCountedScoreNumber");
        if(players.size() == nowCounted){
            //System.out.println("Sending counted score in increaseCountedScoreNumber");
            sendCountedScore();
        }
    }

    private void sendCountedScore(){
        //System.out.println("inside sendCOuntedScore");
        players.forEach(p -> p.sendScore(gatherScores(p)));

        //System.out.println("Telling all clients about final score");
    }

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

    public String gatherScores(PlayerOrAI client){
        //System.out.println("Inside gatherScores at start");
        StringBuilder text = new StringBuilder();
        int indexOfClient = players.indexOf(client);
        countRanks();
        boolean putTable = true;
        int gotScores = 0;
        while(gotScores != players.size()){
            //System.out.println("Inside gatherScores loop while");
            if(players.size() <= indexOfClient){
                indexOfClient = 0;
            }
            if(putTable){
                text.append(players.elementAt(indexOfClient).getScoreTable()).append("#");
            }
            text.append(players.elementAt(indexOfClient).getRank()).append(") ").append(players.elementAt(indexOfClient).getScore()).append("#");
            gotScores++;
            indexOfClient++;
            putTable = false;
        }
        text.deleteCharAt(text.length()-1);

        return text.toString();
    }

    private void countRanks(){
        //System.out.println("maxClients.get() = " + maxClients.get());
        PlayerOrAI[] playerScore = new PlayerOrAI[players.size()];
        //System.out.println("player.size() = " + players.size());
        for(int i = 0; i < players.size();i++){
            for(int j = 0; j <= i; j++ ) {
                if(playerScore[j] == null) {
                    playerScore[j] = players.elementAt(i);
                }else {
                    //System.out.println("Player name: " + players.elementAt(i).name + " Score: " + players.elementAt(i).score + " <? " + playerScore[j].score + " j = " + j);
                    if (players.elementAt(i).getScore() > playerScore[j].getScore()) {
                        for (int k = players.size() - 1; k > j; k--) {
                            playerScore[k] = playerScore[k - 1];
                            //if(playerScore[k]!=null)
                            //System.out.println("playerScore[" + k + "]=" + playerScore[k].name);
                        }
                        playerScore[j] = players.elementAt(i);
                        //System.out.println("playerScore[" + j + "]=" + playerScore[j].name);
                        break;
                    }
                }
            }
            //System.out.println("i = " + i + " a playerScore[i] = " + playerScore[i].name);
        }
        int rank = 1;
        for(int i = 0; i < players.size();i++){
            String name = playerScore[i].getName();
            //System.out.println("Prave se bude nastavovat rank " + rank + " hráči " + name + " i=" + i + " {" +  playerScore[i].getScore() + "}");
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

    public void setNextPlayer() throws CloneNotSupportedException {
        if(cardsOnTable.size() < END_GAME_NUMBER_OF_CARDS){
            int currentIndex = players.indexOf(currentPlayer);
            currentPlayer.playing = false;
            currentIndex++;
            if(currentIndex >= players.size()){
                currentIndex = 0;
            }
            currentPlayer = players.elementAt(currentIndex);
            //System.out.println("Current playing is " + currentPlayer.getName() + " at index in players " + currentIndex);
            currentPlayer.playing = true;
            // If the player is AI, tell it to perform its move. Player has its own way how to determine if they are playing or not.
            if (currentPlayer instanceof GreedyPlayer){
                //System.out.println("Current player before putcardOnTable");
                putCardOnTable(((ArtificialIntelligenceInterface) currentPlayer).performMove(cardsOnTable));

            }
        }

    }

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
        int newMaxPlayers = maxClients.get() - number;
        maxClients.getAndSet(newMaxPlayers);
    }
}
