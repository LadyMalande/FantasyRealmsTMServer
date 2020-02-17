package server;

import bonuses.Bonus;
import interactive.CopyNameColorStrengthMalusFromHand;
import interactive.Interactive;
import maluses.Malus;
import server.Server;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
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
    int score;
    int rank;
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
                        //sendNamesInOrder(hostingServer.giveNamesInOrder(this));
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
                    System.out.println("Is filter in DROP_CARD working properly? ID: " + cardToRemove.id + " Name: " + cardToRemove.name);
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
                    System.out.println("Is filter in GOT_CARD_FROM_TABLE working properly? ID: " + cardToAdd.id + " Name: " + cardToAdd.name);
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
                    if(cardToGive == null){
                        System.out.println("NO CARD to give from DECK????!!!");
                    }else{
                        hand.add(cardToGive);
                        giveCardToHand(cardToGive);
                    }
                }

                if(received.startsWith("ChangeColor")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String command = st.nextToken();
                    String name = st.nextToken();
                    String typeString = st.nextToken();
                    Card cardToChange = hand.stream().filter(card -> card.name.equals(name)).findAny().get();
                    if(cardToChange == null){
                        System.out.println("NO CARD to ChangeColor!");
                    }else{
                        //Change the color
                        cardToChange.type = BigSwitches.switchNameForType(typeString);
                    }
                }

                if(received.startsWith("CopyNameAndType")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String command = st.nextToken();
                    int idOfCardToChange = Integer.parseInt(st.nextToken());
                    String text = st.nextToken();
                    String[] splitted = text.split(" ");
                    String name = splitted[0];
                    Card cardToChange = hand.stream().filter(card -> card.id == idOfCardToChange).findAny().get();
                    if(cardToChange == null){
                        System.out.println("NO CARD to Change!");
                    }else{

                        //Find the card we want to change to by name
                        ArrayList<Card> deck = DeckInitializer.loadDeckFromFile();
                        Card howToChange = deck.stream().filter(card -> card.name.equals(name)).findAny().get();
                        //Change the Name and Type
                        if(howToChange != null){
                            cardToChange.id = howToChange.id;
                            cardToChange.name = howToChange.name;
                            cardToChange.type = howToChange.type;
                        }

                    }
                }
                if(received.startsWith("CopyCardFromHand")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String command = st.nextToken();
                    String name = st.nextToken();
                    Card howToChange = hand.stream().filter(card -> card.name.equals(name)).findAny().get();
                    Card cardToChange = hand.stream().filter(card -> card.id == 53).findAny().get();
                    if(howToChange == null || cardToChange == null){
                        System.out.println("NO card how to change or what to change!");
                    }else{
                        //Change what is needed
                        cardToChange.id = howToChange.id;
                        cardToChange.name = howToChange.name;
                        cardToChange.type = howToChange.type;
                        cardToChange.strength = howToChange.strength;
                        cardToChange.maluses = howToChange.maluses;
                    }
                }

                if(received.startsWith("DeleteOneMalusOnType")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String command = st.nextToken();
                    String text = st.nextToken();
                    String[] splitted = text.split(": ");
                    String name = splitted[0];
                    String malusToDelete = splitted[1];
                    Card cardToChange = hand.stream().filter(card -> card.name.equals(name)).findAny().get();

                    if(cardToChange == null){
                        System.out.println("NO card how to change or what to change!");
                    }else{
                        // Delete malus
                        cardToChange.maluses.removeIf(malus -> malus.text.equals(malusToDelete));
                    }
                }

                if(received.startsWith("TakeCardOfType")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String command = st.nextToken();
                    String name = st.nextToken();
                    Card cardToAdd = hostingServer.cardsOnTable.stream().filter(card -> card.name.equals(name)).findAny().get();
                    if(cardToAdd == null){
                        System.out.println("NO CARD to ChangeColor!");
                    }else{
                        //Change the color
                        hand.add(cardToAdd);
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

    public void endGame(){
        String message = "END#";
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Telling player ("+ name + ") to end game, disable buttons.");
        score = countScore();
        hostingServer.increaseCountedScoreNumber();
    }

    public void sendScore(String text){
    String message = "SCORES#" + text;
    try {
        dos.writeUTF(message);
        dos.flush();
    } catch (IOException e) {
        e.printStackTrace();
    }
    System.out.println("Telling player ("+ name + ") score");

    }

    public boolean sendInteractive(String text){

        String message = text;
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Telling player ("+ name + ") to ask about interactive: " + text);
        return true;

    }

    private int countScore(){
        int sum = 0;
        System.out.println("Pocet karet v ruce: " + hand.size());
        HashMap<Type, Malus> types_maluses = new HashMap<>();
        ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(hand);
        int MAX_PRIORITY = 8;
        int MIN_PRIORITY = 0;
        for(int i = MIN_PRIORITY; i <= MAX_PRIORITY; i++ ){
            for(Card c: copyDeckToMakeChanges){
                //System.out.println("Counting card: " + c.name);
                if(hand.contains(c)){
                    if(c.interactives != null)
                        for(Interactive in: c.interactives){

                            if(in.getPriority() == i) {
                                boolean gotAnswer = in.askPlayer(this);

                                while(!gotAnswer){
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }


                        }
                    if(c.bonuses != null)
                        for(Bonus b: c.bonuses){
                            if(b.getPriority() == i) {
                                int sumForCardBonus = b.count(hand);
                                sum += sumForCardBonus;
                                System.out.println("SumForBonusOfCard: " + c.name + " : " + sumForCardBonus);
                            }
                        }
                    if(c.maluses != null)
                        for(Malus m: c.maluses){
                            if(m.getPriority() == 3){
                                types_maluses.put(c.type, m);
                            }
                            else if(m.getPriority() == i) {

                                int sumForCardMalus = m.count(hand);
                                sum += sumForCardMalus;
                                System.out.println("SumForMalusOfCard: "  + c.name + " : " + sumForCardMalus);
                            }
                        }


                }
            }
            if(i == 6){
                for(Malus m: Sorts.topologicalSort(this,types_maluses)){
                    sum += m.count(hand);
                }
            }
        }
        for(Card c: hand){
            sum += c.strength;
        }


        return sum;
    }

}



