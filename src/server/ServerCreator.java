package server;

import artificialintelligence.ArtificialIntelligenceInterface;
import artificialintelligence.GreedyPlayer;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/*
// server.Server class
public class
ServerCreator extends Thread
{

    private ServerSocket ss;
    private String[] args;
    // counter for clients

    public ServerCreator(ServerSocket ss, String[] args){
        this.ss = ss;
        this.args = args;
    }
    public void run()
    {
        try {
            readArgs(args);
        } catch(NotAIType exception){
            System.out.println("Error with AI types in parameters of program.");
        }

        // NOT an AI experiment
        if(args.length == 0){
            int i = 0;
            // running infinite loop for getting
            // client request
            while (true)
            {
                    // Accept the incoming request
                    try {

                        Socket s = ss.accept();

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
                        // start the thread.
                        t.start();
                        i++;
                        //System.out.println("We just increased player number with i++, now its: " + i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    try {
                        maxClients.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }



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


}
*/
