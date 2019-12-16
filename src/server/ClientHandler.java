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
    int maxPlayers;
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

    public void setNextPlayer(ClientHandler ch){
        this.nextPlayer = ch;
    }

    @Override
    public void run() {

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
                    String rec = dis.readUTF();
                    StringTokenizer st = new StringTokenizer(rec, "#");
                    this.name = st.nextToken();
                    String maxPlayers= st.nextToken();
                    hostingServer.maxClients = Integer.parseInt(maxPlayers);
                    System.out.println(this.name + " "+ hostingServer.maxClients);
                    String bool = st.nextToken();

                    System.out.println("started giveStartingCards");
                    try
                    {
                        // receive the string

                        for(Card c: hand){
                  /*  String allText = new String();
                    if(c.bonuses != null){
                        allText += "BONUS\n";
                        for(Bonus b: c.bonuses){
                            allText += b.text + "\n";
                        }
                    }
                    if(c.maluses != null){
                        allText += "MALUS\n";
                        for(Malus m: c.maluses){
                            allText += m.text + "\n";
                        }
                    }
                    if(c.interactives != null){
                        for(Interactive b: c.interactives){
                            allText += b.text + "\n";
                        }
                    }
                    String message = "CARD_TO_HAND" + "#" + c.id + "#" + c.name + "#" + BigSwitches.switchTypeForName(c.type) + "#" + c.strength +"#" + allText ;
                    dos.writeUTF(message);
                    System.out.println("Giving cards to player name: " + c.name);

                    */
                            dos.writeUTF("CARD_TO_HAND");
                            dos.flush();
                            dos.writeObject(c);
                            dos.flush();
                        }
                        System.out.println("Gave 7 cards to player " + name);
                        giveStartingCards = false;

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }

                if(received.equals("logout")){
                    this.isloggedin=false;
                    this.s.close();
                    break;
                }

                // break the string into message and recipient part


            } catch (IOException e) {

                e.printStackTrace();
            }


        }








    }

} 