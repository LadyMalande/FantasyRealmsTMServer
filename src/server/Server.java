package server;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;


// server.Server class
public class
Server extends Thread
{
    public ArrayList<Card> cardsOnTable;
    boolean gameOver = false;
    boolean allPlayersInitialized = false;
    ClientHandler currentPlayer;
    Deck deck;
    ServerSocket ss;
    public Socket s;
    Random randomGenerator;
    public AtomicInteger maxClients = new AtomicInteger(6);
    // Vector to store active clients
    Vector<ClientHandler> players = new Vector<>();

    public Vector<ClientHandler> getPlayers(){
        return players;
    }


    // counter for clients

    public Server(ServerSocket ss){
        this.ss = ss;
    }
    public void run()
    {
        deck = new Deck();
        cardsOnTable = new ArrayList<>();
        int i = 0;
        System.out.println("Waiting for clients...");
        // running infinite loop for getting
        // client request
        while (true)
        {
            synchronized (maxClients) {
                if (maxClients.get() == i) {
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
                    randomGenerator = new Random();
                    for (int j = 0; j < 7; j++) {
                        int index = randomGenerator.nextInt(deck.getDeck().size());
                        mtch.getHand().add(deck.getDeck().get(index));
                        deck.getDeck().remove(index);
                    }

                    // start the thread.
                    t.start();
                    System.out.println("Max clients can be: " + maxClients);
                    i++;
                    System.out.println("We just increased player number with i++, now its: " + i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(maxClients);
                System.out.println(maxClients);
                try {
                    maxClients.wait();
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException while waiting");
                    e.printStackTrace();
                }
            }
        }
        System.out.println("After end of while players < maxplayers, BEFORE RANDOM STARTING PLAYER");
        Random randomGeneratorForPlayers = new Random();
        int index2 = randomGeneratorForPlayers.nextInt(players.size());
        players.elementAt(index2).playing = true;
        currentPlayer = players.elementAt(index2);
        System.out.println("Starting player is player <" + players.elementAt(index2).name +">");

        startTheGame();

        System.out.println("Players size" + players.size());
        System.out.println("End of Server");



    }

    public void putCardOnTable(Card c){
        // Tell all clients to put this card on tables
        cardsOnTable.add(c);
        players.forEach(p -> p.putCardOnTable(c));
        System.out.println("Telling all clients to put this card on table: " + c.name);
    }

    private void startTheGame(){
        // Tell all the clients who is the starting player and set the playing player, give all player names
        players.forEach(p -> p.sendNamesInOrder(giveNamesInOrder(p)));
    }

    public void takeCardFromTable(Card c){
        // Tell all clients to erase this card from tables
        cardsOnTable.remove(c);
        players.forEach(p -> p.eraseCardFromTable(c));
        System.out.println("Telling all clients to erase this card from table: " + c.name);
    }

    public String giveNamesInOrder(ClientHandler client){
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

    public void setNextPlayer(){
        int currentIndex = players.indexOf(currentPlayer);
        currentPlayer.playing = false;
        currentIndex++;
        if(currentIndex >= players.size()){
            currentIndex = 0;
        }
        currentPlayer = players.elementAt(currentIndex);
        currentPlayer.playing = true;
    }
}
