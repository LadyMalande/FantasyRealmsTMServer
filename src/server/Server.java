package server;

import artificialintelligence.ArtificialIntelligenceInterface;
import artificialintelligence.GreedyPlayer;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


// server.Server class
public class
Server extends Thread
{
    private final String[] AITypes = {"GREEDY"};

    private String[] args;
    public ArrayList<Card> cardsOnTable;
    boolean gameOver = false;
    boolean allPlayersInitialized = false;
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


    // counter for clients

    public Server(ServerSocket ss, String[] args){
        this.ss = ss;
        this.args = args;
        varietyOfPlayersAI = new StringBuilder();
    }
    public void run()
    {
        deck = new Deck();
        deck.initializeOriginal();
        deck.initializeRandom();
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
                        System.out.println("Max Clients == i, so we break from while(true) in Server.run()");
                        break;
                    }
                    System.out.println(maxClients);
                    // Accept the incoming request
                    try {

                        s = ss.accept();

                        System.out.println("New client request received : " + s);

                        // obtain input and output streams
                        ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
                        ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());

                        System.out.println("Creating a new handler for this client...");

                        // Create a new handler object for handling this request.
                        ClientHandler mtch = new ClientHandler(s, "client " + i, dis, dos, this);

                        // Create a new Thread with this object.
                        Thread t = new Thread(mtch);

                        System.out.println("Adding this client to active client list");

                        // add this client to active clients list
                        players.add(mtch);
                        // Generate starting hand
                        varietyOfPlayersAI.append("P");

                        // start the thread.
                        t.start();
                        System.out.println("Max clients can be: " + maxClients);
                        i++;
                        //System.out.println("We just increased player number with i++, now its: " + i);
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
        //System.out.println("After end of while players < maxplayers, BEFORE RANDOM STARTING PLAYER");
        Random randomGeneratorForPlayers = new Random();
        int index2 = randomGeneratorForPlayers.nextInt(players.size());
        players.elementAt(index2).playing = true;
        currentPlayer = players.elementAt(index2);
        System.out.println("Starting player is player <" + players.elementAt(index2).name +">");

        experimentOutputName = players.size() + varietyOfPlayersAI.toString() + randomOrNot;
        startTheGame();

        System.out.println("Players size" + players.size());
        System.out.println("End of Server");



    }

    private void readArgs(String[] args) throws NotAIType{
        if(args.length != 0){
            // recognize arguents: number of players, type of AI (number of names of AI as number of players)

            for(int ar = 0; ar < args.length; ar++){
                if(ar == 0) {
                    try {
                        numberOfAI = Integer.parseInt(args[ar]);
                        System.out.println("Number of AI players: " + numberOfAI);
                        maxClients.getAndSet(numberOfAI);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid arguments format. Run the program again with proper arguments in following format: numberOfPlayers, nameOfAI (times numberOfPlayers). Terminating the program.");

                    }
                }

                if(ar == 1){
                    // read what deck should be used for the experiment
                    if ("RANDOM".equals(args[ar])) {
                        deck.setDeck(true);
                    } else {
                        deck.setDeck(false);
                        randomOrNot = "0";

                    }
                    System.out.println(args[ar]);

                }

                if(ar > 1){
                    if (Stream.of(AITypes).anyMatch(args[ar]::startsWith)){
                        if(args[ar].startsWith("GREEDY_")){
                            try {
                                varietyOfPlayersAI.append("G");
                                int parameterOfGreedy = Integer.parseInt(args[ar].substring(7));
                                GreedyPlayer ai = new GreedyPlayer(this, parameterOfGreedy, ar + "_GREEDY");
                                players.add(ai);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid arguments format of GREEDY type of AI. Correct type is: GREEDY_0 or GREEDY_10 etc. Run the program again with proper arguments. Terminating the program.");

                            }

                        }
                    } else{
                        throw new NotAIType("Argument of index " + ar + " is not a type of AI. Make right input and restart program.");
                    }
                } else{
                    //TODO insert other types of AI to recognize on args input here
                }
            }
        }
    }

    public void increaseCountedScoreNumber(){
        int nowCounted = numberOfCountedScores.incrementAndGet();
        System.out.println("nowCounted incremented in increaseCountedScoreNumber");
        if(maxClients.get() == nowCounted){
            System.out.println("Sending counted score in increaseCountedScoreNumber");
            sendCountedScore();
        }
    }

    private void sendCountedScore(){
        players.forEach(p -> p.sendScore(gatherScores(p)));
        ExperimentOutputCreator eoc = new ExperimentOutputCreator(players);
        eoc.createOutput(experimentOutputName);
        System.out.println("Telling all clients about final score");
    }

    public void putCardOnTable(Card c){
        // Tell all clients to put this card on tables
        cardsOnTable.add(c);
        players.forEach(p -> {
            try {
                p.putCardOnTable(c);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
        if(cardsOnTable.size() == 10){
            System.out.println("Ending game, 10 cards on table");
            sendEndGame();
        }
        System.out.println("-----Telling all clients to put this card on table: " + c.name + " NOW cards on table: " + cardsOnTable.size());
        System.out.println("????__________________________________________>>>><<<???");
        System.out.println("????__________________________________________>>>><<<???");
        System.out.println("????__________________________________________>>>><<<???");
    }

    private void startTheGame(){
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
        System.out.println("Telling all clients to erase this card from table: " + c.name);
    }

    private void sendEndGame(){
        //Tell all clients to disable all buttons, end of the game, start counting hand
        players.forEach(p -> p.endGame());

    }

    public String giveNamesInOrder(PlayerOrAI client){
        StringBuilder names = new StringBuilder();
        int indexOfClient = players.indexOf(client);
        int gotNames = 0;
        while(gotNames != maxClients.get()){

            if(players.size() <= indexOfClient){
                indexOfClient = 0;
            }
            if(players.elementAt(indexOfClient).playing){
                names.append("$&$START$&$");
            }
            names.append(players.elementAt(indexOfClient).name).append("#");
            gotNames++;
            indexOfClient++;
        }
        names.deleteCharAt(names.length()-1);
        return names.toString();
    }

    public String gatherScores(PlayerOrAI client){
        StringBuilder text = new StringBuilder();
        int indexOfClient = players.indexOf(client);
        countRanks();
        boolean putTable = true;
        int gotScores = 0;
        while(gotScores != maxClients.get()){

            if(players.size() <= indexOfClient){
                indexOfClient = 0;
            }
            if(putTable){
                text.append(players.elementAt(indexOfClient).scoreTable).append("#");
            }
            text.append(players.elementAt(indexOfClient).rank).append(") ").append(players.elementAt(indexOfClient).score).append("#");
            gotScores++;
            indexOfClient++;
            putTable = false;
        }
        text.deleteCharAt(text.length()-1);

        return text.toString();
    }

    private void countRanks(){
        PlayerOrAI[] playerScore = new PlayerOrAI[maxClients.get()];
        for(int i = 0; i < players.size();i++){
            for(int j = 0; j <= i; j++ ) {
                if(playerScore[j] == null) {
                    playerScore[j] = players.elementAt(i);
                }else {
                    //System.out.println("Player name: " + players.elementAt(i).name + " Score: " + players.elementAt(i).score + " <? " + playerScore[j].score + " j = " + j);
                    if (players.elementAt(i).score > playerScore[j].score) {
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
            String name = playerScore[i].name;
            System.out.println("Prave se bude nastavovat rank " + rank + " hráči " + name + " i=" + i + " {" +  playerScore[i].score + "}");
            playerScore[i].rank = rank;
            if(i < players.size()-1){
                if(playerScore[i].score == playerScore[i+1].score){
                    //TODO Set other conditions of victory other than total score
                } else{
                    rank++;
                }
            }
        }

    }

    public void setNextPlayer() throws CloneNotSupportedException {
        if(cardsOnTable.size() < 10){
            int currentIndex = players.indexOf(currentPlayer);
            currentPlayer.playing = false;
            currentIndex++;
            if(currentIndex >= players.size()){
                currentIndex = 0;
            }
            currentPlayer = players.elementAt(currentIndex);
            currentPlayer.playing = true;
            // If the player is AI, tell it to perform its move. Player has its own way how to determine if they are playing or not.
            if (currentPlayer instanceof ArtificialIntelligenceInterface){
                ((ArtificialIntelligenceInterface) currentPlayer).performMove(cardsOnTable);
            }
        }

    }
}
