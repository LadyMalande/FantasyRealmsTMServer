package server;

import bonuses.Bonus;
import interactive.Interactive;
import maluses.Malus;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// server.ClientHandler class
public class ClientHandler extends PlayerOrAI implements Runnable
{
    ArrayList<Card> hand;
    public String name;
    boolean giveStartingCards = false;
    public FutureTask<Integer> futureTask;
    boolean playing = false;
    final ObjectInputStream dis;
    final ObjectOutputStream dos;
    Socket s;
    int score = -1;
    int rank;
    boolean isloggedin;
    public Server hostingServer;
    public AtomicInteger interactivesResolved;
    public int interactivesCount;
    public AtomicBoolean interactivesResolvedAtomicBoolean;
    public StringBuilder scoreTable;
    public Locale locale;
    int beginningHandScore = 0;
    String beginningHandCards;


    @Override
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
                        String randomDeck = st.nextToken();
                        boolean putRandomDeck = Boolean.parseBoolean(randomDeck);

                            hostingServer.deck.setDeck(putRandomDeck);

                            StringBuilder sb = new StringBuilder();
                        Random randomGenerator = new Random();
                        for (int j = 0; j < 7; j++) {
                            int index = randomGenerator.nextInt(hostingServer.deck.getDeck().size());
                            hand.add(hostingServer.deck.getDeck().get(index));
                            sb.append(hostingServer.deck.getDeck().get(index).getName() + ";");
                            hostingServer.deck.getDeck().remove(index);
                        }
                        beginningHandCards = sb.toString();
                        System.out.println("New player joined: " + name + " , max players: " + hostingServer.maxClients);
                        String loc = st.nextToken();
                        locale = new Locale(loc);
                        int numberOfAI = Integer.parseInt(st.nextToken());
                        if(numberOfAI != 0){
                            hostingServer.setAI(numberOfAI);
                        }
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
                    hand.remove(cardToRemove);
                    hostingServer.putCardOnTable(cardToRemove);


                }

                if(received.startsWith("GOT_CARD_FROM_TABLE")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String jumpover = st.nextToken();
                    int id = Integer.parseInt(st.nextToken());
                    Card cardToAdd = hostingServer.cardsOnTable.stream().filter(card -> card.id == id).findAny().get();
                    System.out.println("Is filter in GOT_CARD_FROM_TABLE working properly? ID: " + cardToAdd.id + " Name: " + cardToAdd.name);
                    hand.add(cardToAdd);
                    hostingServer.takeCardFromTable(cardToAdd);
                }

                if(received.startsWith("GIVE_CARD_FROM_DECK")){
                    Random randomGenerator = new Random();
                    int index = randomGenerator.nextInt(hostingServer.deck.getDeck().size());

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
                    Integer id = Integer.parseInt(st.nextToken());
                    if(id != -1){
                        //String name = st.nextToken();
                        String typeString = st.nextToken();
                        Card cardToChange = hand.stream().filter(card -> card.id == id).findAny().get();
                        //Card cardToChange = hand.stream().filter(card -> card.name.equals(name)).findAny().get();
                        //Change the color
                        cardToChange.type = BigSwitches.switchNameForType(typeString);
                    }
                    synchronized(interactivesResolvedAtomicBoolean) {
                        interactivesResolved.incrementAndGet();
                        System.out.println("The interactiveResolved should have been increased...");
                        interactivesResolvedAtomicBoolean.set(true);
                    }
                }

                if(received.startsWith("CopyNameAndType")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String command = st.nextToken();
                    int idOfCardToChange = Integer.parseInt(st.nextToken());
                    if(idOfCardToChange != -1) {
                        String text = st.nextToken();
                        String[] splitted = text.split("( \\()");
                        String name = splitted[0];
                        Card cardToChange = hand.stream().filter(card -> card.id == idOfCardToChange).findAny().get();

                        //Find the card we want to change to by name
                        ArrayList<Card> deck = DeckInitializer.loadDeckFromFile();
                        String originalName = BigSwitches.switchNameForOriginalName(name, locale);
                        Card howToChange = deck.stream().filter(card -> card.name.equals(originalName)).findAny().get();

                        //Change the Name and Type
                        cardToChange.name = howToChange.name;
                        cardToChange.type = howToChange.type;
                    }
                    synchronized(interactivesResolvedAtomicBoolean) {
                        interactivesResolved.incrementAndGet();
                        System.out.println("The interactiveResolved should have been increased...");
                        interactivesResolvedAtomicBoolean.set(true);
                        interactivesResolvedAtomicBoolean.notifyAll();
                    }
                }
                if(received.startsWith("CopyCardFromHand")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String command = st.nextToken();
                    int idHowToChange = Integer.parseInt(st.nextToken());
                    int id = Integer.parseInt(st.nextToken());
                    if(id != -1) {
                        Card howToChange = hand.stream().filter(card -> card.id == idHowToChange).findAny().get();
                        System.out.println("How to change card:" + howToChange.name);
                        Card cardToChange = hand.stream().filter(card -> card.id == id).findAny().get();
                        //Change what is needed
                        cardToChange.name = howToChange.name;
                        cardToChange.type = howToChange.type;
                        cardToChange.strength = howToChange.strength;
                        cardToChange.maluses = howToChange.maluses;
                    }
                    synchronized(interactivesResolvedAtomicBoolean) {
                        interactivesResolved.incrementAndGet();
                        System.out.println("The interactiveResolved should have been increased...");
                        interactivesResolvedAtomicBoolean.set(true);
                        interactivesResolvedAtomicBoolean.notifyAll();
                    }
                }

                if(received.startsWith("DeleteOneMalusOnType")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String command = st.nextToken();
                    String text = st.nextToken();
                    String[] splitted = text.split(": ");
                    int id = Integer.parseInt(splitted[0]);
                    if(id != -1){
                        String malusToDelete = splitted[1];
                        Card cardToChange = hand.stream().filter(card -> card.id == id).findAny().get();
                        cardToChange.maluses.removeIf(malus -> malus.getText(locale.getLanguage()).equals(malusToDelete));
                    }
                    synchronized(interactivesResolvedAtomicBoolean) {
                        // Delete malus
                        interactivesResolved.incrementAndGet();
                        System.out.println("The interactiveResolved should have been increased...");
                        interactivesResolvedAtomicBoolean.set(true);
                        interactivesResolvedAtomicBoolean.notifyAll();
                    }
                }

                if(received.startsWith("TakeCardOfType")){
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String command = st.nextToken();
                    if(st.hasMoreTokens()){
                        int id = Integer.parseInt(st.nextToken());
                        if(id != -1){
                            Card cardToAdd = hostingServer.cardsOnTable.stream().filter(card -> card.id == id).findAny().get();
                            //Put the card to hand
                            if(cardToAdd.interactives != null && !cardToAdd.interactives.isEmpty() && cardToAdd.interactives.get(0).getPriority() < 2){
                                cardToAdd.interactives.get(0).priority = 3;
                            }
                            hand.add(cardToAdd);

                            if (cardToAdd.interactives != null && !cardToAdd.interactives.isEmpty()) {
                                interactivesCount += cardToAdd.interactives.size();
                            }
                        }
                        synchronized(interactivesResolvedAtomicBoolean) {

                            interactivesResolved.incrementAndGet();
                            System.out.println("The interactiveResolved should have been increased...");
                            interactivesResolvedAtomicBoolean.set(true);
                            interactivesResolvedAtomicBoolean.notifyAll();
                        }
                    } else{
                        interactivesResolved.incrementAndGet();
                        System.out.println("The interactiveResolved should have been increased...No card added to hand.");

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

    public void setWaitingCounterForInteractives(){
        interactivesResolved = new AtomicInteger(0);
        interactivesCount = 0;
        for(Card c: hand){
            if(c.interactives != null && !c.interactives.isEmpty()){
                interactivesCount += c.interactives.size();
            }
        }
        //System.out.println("Player " + name + " has now " + interactivesCount + " interactive cards on hand.");
    }

    private void giveCardToHand(Card c){
        String message = "CARD_TO_HAND#" +
                c.id + "#" +
                c.getNameLoc(locale.getLanguage()) + "#" +
                c.strength +"#" +
                BigSwitches.switchTypeForName(c.type, locale.getLanguage()) + "#" +
                getAllText(c) ;
        try {
            dos.writeUTF(message);
            dos.flush();
            //System.out.println("Giving card to player (" + name + "), name of card: " + c.name);
            //System.out.println(message);
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
                        c.getNameLoc(locale.getLanguage()) + "#" +
                        c.strength +"#" +
                        BigSwitches.switchTypeForName(c.type, locale.getLanguage()) + "#" +
                        getAllText(c) ;

                dos.writeUTF(message);
                dos.flush();
                //System.out.println("Giving card to player ("+ name + "), name of card: " + c.name);

            }
            //System.out.println("Gave 7 cards to player " + name);
            giveStartingCards = false;

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private String getAllText(Card c){
        StringBuilder allText = new StringBuilder();
        boolean first = true;
        if(c.bonuses != null){
            for(Bonus b: c.bonuses){
                if(!first){
                    allText.append("\n");
                }
                allText.append(b.getText(locale.getLanguage())).append("\n");
                //System.out.println("Name: " + c.name + " TEXT: " + b.getText());
                first = false;
            }
        }
        ResourceBundle rb = ResourceBundle.getBundle("maluses.CardMaluses",locale);
        if(c.maluses != null){
            allText.append(rb.getString("malus") + "\n");
            for(Malus m: c.maluses){
                if(!first){
                    allText.append("\n");
                }
                allText.append(m.getText(locale.getLanguage())).append("\n");
                first = false;
            }
        }
        if(c.interactives != null){
            for(Interactive b: c.interactives){
                allText.append(b.getText(locale.getLanguage()));
            }
        }
        if(c.interactives == null && c.bonuses == null && c.maluses==null){
            System.out.println("All is null, card doesnt have any text");
        }
        return allText.toString();
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public boolean getPlaying(){
        return this.playing;
    }

    @Override
    public void setPlaying(boolean playing){this.playing = playing;}

    @Override
    public void setScoreTable(StringBuilder sb){
        this.scoreTable = sb;}

    @Override
    public StringBuilder getScoreTable(){
        return this.scoreTable;
    }

    @Override
    public int getRank(){return this.rank;}
    @Override
    public int getScore(){return this.score;}
    @Override
    public void setRank(int r){this.rank = r;}
    @Override
    public void setScore(int s){this.score = s;}

    @Override
    public void putCardOnTable(Card c) throws CloneNotSupportedException {
        String message =    "CARD_TO_TABLE" + "#" +
                c.id + "#" +
                c.getNameLoc(locale.getLanguage()) + "#" +
                c.strength +"#" +
                BigSwitches.switchTypeForName(c.type, locale.getLanguage()) + "#" +
                getAllText(c);
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //hostingServer.setNextPlayer();
        //System.out.println("Giving card to table for player ("+ name + "), name of card: " + c.name);
    }

    @Override
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

    @Override
    public void sendNamesInOrder(String names){
        String message = "NAMES#" + names;
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("Telling player ("+ name + ") names of other players: " + names);
    }

    @Override
    public void endGame(){
        String message = "END#";
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Telling player ("+ name + ") to end game, disable buttons.");

        ScoreCounter scoreCounter = new ScoreCounter(this);
        scoreCounter.start();
    }

    @Override
    public void sendScore(String text){
    String message = "SCORES#" + text;
    try {
        dos.writeUTF(message);
        dos.flush();
    } catch (IOException e) {
        e.printStackTrace();
    }
    System.out.println("Telling player ("+ name + ") score : " + message);

    }

    @Override
    public String getBeginningHandCards(){
        return this.beginningHandCards;
    }

    @Override
    public int getBeginningHandScore(){ return this.beginningHandScore; }

    public boolean sendInteractive(String text){
        try {
            dos.writeUTF(text);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Telling player ("+ name + ") to ask about interactive: " + text);
        return true;

    }

}



