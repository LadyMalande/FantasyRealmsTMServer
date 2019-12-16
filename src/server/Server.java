package server;

import java.io.*;
import java.util.*;
import java.net.*;


// server.Server class
public class Server extends Thread
{
    public ArrayList<Card> cardsOnTable;
    boolean gameOver = false;
    boolean allPlayersInitialized = false;
    Deck deck;
    ServerSocket ss;
    public Socket s;
    int maxClients = 10;
    // Vector to store active clients
    Vector<ClientHandler> players = new Vector<>();

    public Vector<ClientHandler> getPlayers(){
        return players;
    }
    public void setMaxClients(int i){
        this.maxClients = i;
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
        while (i < maxClients)
        {

            // Accept the incoming request
            try {

                s = ss.accept();


                System.out.println("New client request received : " + s);


                    // obtain input and output streams
                ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
                ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());

                System.out.println("Creating a new handler for this client...");

                // Create a new handler object for handling this request.
                ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos, this);

                // Create a new Thread with this object.
                Thread t = new Thread(mtch);

                System.out.println("Adding this client to active client list");

                // add this client to active clients list
                players.add(mtch);
                // Generate starting hand
                Random randomGenerator = new Random();
                for (int j = 0; j < 7; j++) {
                    int index = randomGenerator.nextInt(deck.getDeck().size());
                    mtch.getHand().add(deck.getDeck().get(index));
                    deck.getDeck().remove(index);
                }

                // start the thread.
                t.start();

                i = maxClients;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Players size" + players.size());
        System.out.println("End of Server");



    }
}
