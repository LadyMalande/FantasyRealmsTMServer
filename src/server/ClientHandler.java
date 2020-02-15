package server;

import bonuses.Bonus;
import interactive.Interactive;
import maluses.Malus;
import server.Server;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;

// server.ClientHandler class
public class ClientHandler implements Runnable
{
    ArrayList<Card> hand;
    Scanner scn = new Scanner(System.in);
    public String name;
    boolean playerLobbyInit = true;
    boolean giveStartingCards = false;
    ClientHandler nextPlayer;
    boolean gameOver = false;
    boolean scoring = false;
    boolean scoreTable = false;
    boolean playing = false;
    AtomicInteger maxPlayers;
    final ObjectInputStream dis;
    final ObjectOutputStream dos;
    Socket s;
    boolean isloggedin;
    public Server hostingServer;

    public ArrayList<Card> getHand(){
        return this.hand;
    }
    // constructor 
    public ClientHandler(Socket s, String name,
                         ObjectInputStream dis, ObjectOutputStream dos, Server server) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
        this.hostingServer = server;
        this.hand = new ArrayList<>();
    }

    @Override
    public void run(){

        String received;
        while (true)
        {

            try
            {
                System.out.println("Before receiving UTF");
                    // receive the string

                received = dis.readUTF();
                System.out.println(received);

                if(received.startsWith("INIT")){
                    synchronized (hostingServer.maxClients) {
                        StringTokenizer st = new StringTokenizer(received, "#");
                        String command = st.nextToken();
                        this.name = st.nextToken();
                        String maxPlayers = st.nextToken();
                        hostingServer.maxClients.getAndSet(Integer.parseInt(maxPlayers));
                        hostingServer.maxClients.notifyAll();
                        System.out.println(this.name + " " + hostingServer.maxClients);
                        String bool = st.nextToken();

                        System.out.println("New player joined: " + name + " , max players: " + hostingServer.maxClients);
                        give_init_cards();
                        sendNamesInOrder(hostingServer.giveNamesInOrder(this));
                    }
                }

                if(received.equals("logout")){
                this.isloggedin=false;
                this.s.close();
                break;
                }

                if(received.startsWith("DROP_CARD")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String jumpover = st.nextToken();
                    int id = Integer.parseInt(st.nextToken());
                    Card cardToRemove = hand.stream().filter(card -> card.id == id).findAny().get();
                    if(cardToRemove == null){
                        System.out.println("BAD ID of card to remove!!!");
                    }else{
                        hand.remove(cardToRemove);
                        hostingServer.putCardOnTable(cardToRemove);
                    }


                }

                if(received.startsWith("GOT_CARD_FROM_TABLE")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String jumpover = st.nextToken();
                    int id = Integer.parseInt(st.nextToken());
                    Card cardToAdd = hostingServer.cardsOnTable.stream().filter(card -> card.id == id).findAny().get();
                    if(cardToAdd == null){
                        System.out.println("BAD ID of card to remove!!!");
                    }else{
                        hand.add(cardToAdd);
                        hostingServer.takeCardFromTable(cardToAdd);
                    }
                }

                if(received.startsWith("GIVE_CARD_FROM_DECK")){

                    int index = hostingServer.randomGenerator.nextInt(hostingServer.deck.getDeck().size());

                    Card cardToGive = hostingServer.deck.getDeck().get(index);
                    hostingServer.deck.getDeck().remove(index);
                    hand.add(cardToGive);

                    if(cardToGive == null){
                        System.out.println("NO CARD to give from DECK????!!!");
                    }else{
                        hand.add(cardToGive);
                        giveCardToHand(cardToGive);
                    }
                }


            } catch(EOFException eof) {
                System.out.println("Player disconnected. Closing the game.");
                // TODO: Tell clients that one of them disconnected and the game is over // or the game goes on
                System.exit(2);

            } catch (IOException e){

                e.printStackTrace();
            }


        }








    }

    private void giveCardToHand(Card c){
        String message = "CARD_TO_HAND#" +
                c.id + "#" +
                c.name + "#" +
                c.strength +"#" +
                BigSwitches.switchTypeForName(c.type) + "#" +
                getAllText(c) ;
        try {
            dos.writeUTF(message);
            dos.flush();
            System.out.println("Giving card to player (" + name + "), name of card: " + c.name);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void give_init_cards(){
        try
        {
            // receive the string

            for(Card c: hand){

                String message =    "INIT_CARD_TO_HAND" + "#" +
                        c.id + "#" +
                        c.name + "#" +
                        c.strength +"#" +
                        BigSwitches.switchTypeForName(c.type) + "#" +
                        getAllText(c) ;

                dos.writeUTF(message);
                dos.flush();
                System.out.println("Giving card to player ("+ name + "), name of card: " + c.name);

            }
            System.out.println("Gave 7 cards to player " + name);
            giveStartingCards = false;

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private String getAllText(Card c){
        StringBuilder allText = new StringBuilder();
        if(c.bonuses != null){
            allText.append("BONUS\n");
            for(Bonus b: c.bonuses){
                allText.append(b.getText()).append("\n");
                //System.out.println("Name: " + c.name + " TEXT: " + b.getText());
            }
        }
        if(c.maluses != null){
            allText.append("MALUS\n");
            for(Malus m: c.maluses){
                allText.append(m.getText()).append("\n");
            }
        }
        if(c.interactives != null){
            for(Interactive b: c.interactives){
                allText.append(b.getText()).append("\n");
            }
        }
        return allText.toString();
    }

    public void putCardOnTable(Card c){
        String message =    "CARD_TO_TABLE" + "#" +
                c.id + "#" +
                c.name + "#" +
                c.strength +"#" +
                BigSwitches.switchTypeForName(c.type) + "#" +
                getAllText(c);
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        hostingServer.setNextPlayer();
        System.out.println("Giving card to table for player ("+ name + "), name of card: " + c.name);
    }


    public void eraseCardFromTable(Card c){
        String message = "REMOVE_CARD_FROM_TABLE#" + c.id;
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Telling player ("+ name + ") to remove this card from table, name of card: " + c.name);
    }

    public void sendNamesInOrder(String names){
        String message = "NAMES#" + names;
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Telling player ("+ name + ") names of other players: " + names);

    }

}



