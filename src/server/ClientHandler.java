package server;

import artificialintelligence.ScoreCounterForAI;
import bonuses.Bonus;
import interactive.Interactive;
import maluses.Malus;
import util.BigSwitches;
import util.HandCloner;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class listens to the connected client and sends him messages to coordinate the game.
 * @author Tereza Miklóšová
 */
public class ClientHandler implements Runnable, PlayerOrAI
{
    /**
     * The cards in player's hand.
     */
    ArrayList<Card> hand;
    /**
     * The player's name.
     */
    public String name;

    /**
     * Future task for computing final score of the player.
     */
    public FutureTask<Integer> futureTask;
    /**
     * True if the client is current player.
     */
    boolean playing = false;
    /**
     * Object input stream to listen to.
     */
    final ObjectInputStream dis;
    /**
     * Object output stream on which to send the messages.
     */
    final ObjectOutputStream dos;
    /**
     * Socket on which to listen.
     */
    Socket s;
    /**
     * The final score of the player.
     */
    int score = -999;
    /**
     * The final position of the player rated by this score.
     */
    int rank;
    /**
     * True if the player is looged in and not disconnected.
     */
    boolean isloggedin;
    /**
     * Server to communicate with and send its messages.
     */
    public Server hostingServer;
    /**
     * How many interactives have been already resolved.
     */
    public AtomicInteger interactivesResolved;
    /**
     * How many interactives need to be resolved at the end.
     */
    public int interactivesCount;
    /**
     * Flag if the all the interactives in the clients hand have been resolved.
     */
    public AtomicBoolean interactivesResolvedAtomicBoolean;
    /**
     * The detailed score of the final hand.
     */
    public StringBuilder scoreTable;
    /**
     * Client's preferred locale.
     */
    public Locale locale;
    /**
     * String with all the beginning cards in hand.
     */
    String beginningHandCards;

    private boolean running = true;

    @Override
    public ArrayList<Card> getHand(){
        return this.hand;
    }

    /**
     * Constructor for the CLient thread.
     * @param s Socket on which to listen.
     * @param name Name of the client.
     * @param dis Object input stream to listen to.
     * @param dos Object output stream on which to send the messages.
     * @param server Server to communicate with and send its messages.
     */
    public ClientHandler(Socket s, String name,
                         ObjectInputStream dis, ObjectOutputStream dos, Server server) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.interactivesResolvedAtomicBoolean = new AtomicBoolean(false);
        this.isloggedin=true;
        this.hostingServer = server;
        this.hand = new ArrayList<>();
    }

    @Override
    public void run() {
        String received;
        while (running) {

            System.out.println("Before receiving UTF");
            // receive the string

            try {
                received = dis.readUTF();
                System.out.println(received);

                if (received.startsWith("INIT")) {
                    synchronized (hostingServer.maxClients) {
                        StringTokenizer st = new StringTokenizer(received, "#");
                        st.nextToken();
                        this.name = st.nextToken();
                        String maxPlayers = st.nextToken();
                        hostingServer.maxClients.getAndSet(Integer.parseInt(maxPlayers));
                        hostingServer.maxClients.notifyAll();
                        System.out.println(this.name + " " + hostingServer.maxClients);
                        String randomDeck = st.nextToken();
                        boolean putRandomDeck = Boolean.parseBoolean(randomDeck);

                        hostingServer.setDeck(putRandomDeck);
                        hostingServer.getDeck().shuffleDeck();
                        StringBuilder sb = new StringBuilder();
                        Random randomGenerator = new Random();
                        for (int j = 0; j < hostingServer.CARDS_ON_HAND; j++) {
                            int index = randomGenerator.nextInt(hostingServer.getDeck().getDeck().size());
                            hand.add(hostingServer.getDeck().getDeck().get(index));
                            sb.append(hostingServer.getDeck().getDeck().get(index).getName()).append(";");
                            hostingServer.getDeck().getDeck().remove(index);
                        }
                        beginningHandCards = sb.toString();
                        System.out.println("New player joined: " + name + " , max players: " + hostingServer.maxClients);
                        String loc = st.nextToken();
                        locale = new Locale(loc);
                        int numberOfAI = Integer.parseInt(st.nextToken());
                        if (numberOfAI != 0) {
                            hostingServer.setAI(numberOfAI);
                        }
                        give_init_cards();
                        //sendNamesInOrder(hostingServer.giveNamesInOrder(this));
                    }
                }

                if (received.equals("logout")) {
                    this.isloggedin = false;
                    this.s.close();
                    break;
                }

                if (received.startsWith("DROP_CARD")) {
                    StringTokenizer st = new StringTokenizer(received, "#");
                    st.nextToken();
                    int id = Integer.parseInt(st.nextToken());
                    Card cardToRemove = hand.stream().filter(card -> card.getId() == id).findAny().orElse(null);
                    hand.remove(cardToRemove);
                    hostingServer.cardToTableFromClient.set(cardToRemove);
                }

                if (received.startsWith("ENDCONNECTION")) {
                    hostingServer.setRunning(false);
                    running = false;
                }

                if (received.startsWith("GOT_CARD_FROM_TABLE")) {
                    StringTokenizer st = new StringTokenizer(received, "#");
                    st.nextToken();
                    int id = Integer.parseInt(st.nextToken());
                    Card cardToAdd = hostingServer.cardsOnTable.stream().filter(card -> card.getId() == id).findAny().
                            orElse(null);
                    hand.add(cardToAdd);
                    hostingServer.takeCardFromTable(cardToAdd);
                }

                if (received.startsWith("GIVE_CARD_FROM_DECK")) {
                    Random randomGenerator = new Random();
                    int index = randomGenerator.nextInt(hostingServer.getDeck().getDeck().size());

                    Card cardToGive = hostingServer.getDeck().getDeck().get(index);
                    hostingServer.getDeck().getDeck().remove(index);
                    if (cardToGive != null) {
                        hand.add(cardToGive);
                        giveCardToHand(cardToGive);
                    }
                }

                if (received.startsWith("ChangeColor")) {
                    StringTokenizer st = new StringTokenizer(received, "#");
                    st.nextToken();
                    int id = Integer.parseInt(st.nextToken());
                    if (id != -1) {
                        String typeString = st.nextToken();
                        Card cardToChange = hand.stream().filter(card -> card.getId() == id).findAny().orElse(null);
                        //Change the color
                        assert cardToChange != null;
                        cardToChange.setType(BigSwitches.switchNameForType(typeString));
                    }
                    synchronized (interactivesResolvedAtomicBoolean) {
                        interactivesResolved.incrementAndGet();
                        interactivesResolvedAtomicBoolean.set(true);
                    }
                }

                if (received.startsWith("CopyNameAndType")) {
                    StringTokenizer st = new StringTokenizer(received, "#");
                    st.nextToken();
                    int idOfCardToChange = Integer.parseInt(st.nextToken());
                    if (idOfCardToChange != -1) {
                        String text = st.nextToken();
                        String[] splitted = text.split("( \\()");
                        String name = splitted[0];
                        Card cardToChange = hand.stream().filter(card -> card.getId() == idOfCardToChange).findAny().orElse(null);

                        //Find the card we want to change to by name
                        ArrayList<Card> deck = DeckInitializer.loadDeckFromFile();
                        String originalName = BigSwitches.switchNameForOriginalName(name, locale);
                        Card howToChange = deck.stream().filter(card -> card.getName().equals(originalName)).findAny().orElse(null);

                        //Change the Name and Type
                        assert cardToChange != null;
                        assert howToChange != null;
                        cardToChange.setName(howToChange.getName());
                        cardToChange.setType(howToChange.getType());
                    }
                    synchronized (interactivesResolvedAtomicBoolean) {
                        interactivesResolved.incrementAndGet();
                        interactivesResolvedAtomicBoolean.set(true);
                        interactivesResolvedAtomicBoolean.notifyAll();
                    }
                }
                if (received.startsWith("CopyCardFromHand")) {
                    StringTokenizer st = new StringTokenizer(received, "#");
                    st.nextToken();
                    int idHowToChange = Integer.parseInt(st.nextToken());
                    int id = Integer.parseInt(st.nextToken());
                    if (id != -1) {
                        Card howToChange = hand.stream().filter(card -> card.getId() == idHowToChange).findAny().orElse(null);
                        Card cardToChange = hand.stream().filter(card -> card.getId() == id).findAny().orElse(null);
                        //Change what is needed
                        assert cardToChange != null;
                        assert howToChange != null;
                        cardToChange.setName(howToChange.getName());
                        cardToChange.setType(howToChange.getType());
                        cardToChange.setStrength(howToChange.getStrength());
                        cardToChange.setMaluses(howToChange.getMaluses());
                    }
                    synchronized (interactivesResolvedAtomicBoolean) {
                        interactivesResolved.incrementAndGet();
                        interactivesResolvedAtomicBoolean.set(true);
                        interactivesResolvedAtomicBoolean.notifyAll();
                    }
                }

                if (received.startsWith("DeleteOneMalusOnType")) {
                    StringTokenizer st = new StringTokenizer(received, "#");
                    st.nextToken();
                    String text = st.nextToken();
                    String[] splitted = text.split(": ");
                    int id = Integer.parseInt(splitted[0]);
                    if (id != -1) {
                        String malusToDelete = splitted[1];
                        Card cardToChange = hand.stream().filter(card -> card.getId() == id).findAny().orElse(null);
                        assert cardToChange != null;
                        cardToChange.getMaluses().removeIf(malus -> malus.getText(locale.getLanguage()).equals(malusToDelete));
                    }
                    synchronized (interactivesResolvedAtomicBoolean) {
                        // Delete malus
                        interactivesResolved.incrementAndGet();
                        interactivesResolvedAtomicBoolean.set(true);
                        interactivesResolvedAtomicBoolean.notifyAll();
                    }
                }

                if (received.startsWith("TakeCardOfType")) {
                    StringTokenizer st = new StringTokenizer(received, "#");
                    st.nextToken();
                    if (st.hasMoreTokens()) {
                        int id = Integer.parseInt(st.nextToken());
                        if (id != -1) {
                            Card cardToAdd = hostingServer.cardsOnTable.stream().filter(card -> card.getId() == id).findAny().orElse(null);
                            //Put the card to hand
                            assert cardToAdd != null;

                            if (cardToAdd.getInteractives() != null && !cardToAdd.getInteractives().isEmpty() &&
                                    cardToAdd.getInteractives().get(0).getPriority() < 2) {
                                cardToAdd.getInteractives().get(0).priority = 3;
                            }
                            hand.add(cardToAdd);

                            if (cardToAdd.getInteractives() != null && !cardToAdd.getInteractives().isEmpty()) {
                                interactivesCount += cardToAdd.getInteractives().size();
                            }
                        }
                        synchronized (interactivesResolvedAtomicBoolean) {

                            interactivesResolved.incrementAndGet();
                            interactivesResolvedAtomicBoolean.set(true);
                            interactivesResolvedAtomicBoolean.notifyAll();
                        }
                    } else {
                        interactivesResolved.incrementAndGet();
                        System.out.println("The interactiveResolved should have been increased...No card added to hand.");

                    }
                }
                if (received.startsWith("COUNTSCORE")) {
                    ScoreCounterForAI sc = new ScoreCounterForAI();
                    HandCloner hc = new HandCloner();
                    ArrayList<Card> copiedHand = HandCloner.cloneHand(null, this.hand);
                    int score = sc.countScore(copiedHand, hostingServer.cardsOnTable, true);
                    sendMessage("COUNTSCORE#" + score);
                }
            } catch (EOFException eof) {
                System.out.println("Player disconnected. Closing the game.");
                // TODO: Tell clients that one of them disconnected and the game is over // or the game goes on
                System.exit(2);

            } catch (SocketException disconnected) {
                hostingServer.setRunning(false);
                running = false;
            } catch (IOException e) {

                e.printStackTrace();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

    }

    }

    /**
     * Sets the size of the interactives that need to be resolved before revealing scores of all players.
     */
    public void setWaitingCounterForInteractives(){
        interactivesResolved = new AtomicInteger(0);
        interactivesCount = 0;
        for(Card c: hand){
            if(c.getInteractives() != null && !c.getInteractives().isEmpty()){
                interactivesCount += c.getInteractives().size();
            }
        }
    }

    /**
     * Sends string of the requested card the client wants to get to hand.
     * @param c Card to be taken to hand by the client.
     */
    private void giveCardToHand(Card c){
        String message = "CARD_TO_HAND#" +
                c.getId() + "#" +
                c.getNameLoc(locale.getLanguage()) + "#" +
                c.getStrength() +"#" +
                BigSwitches.switchTypeForName(c.getType(), locale.getLanguage()) + "#" +
                getAllText(c) ;
        sendMessage(message);
    }

    /**
     * Send the 7 initial cards to client.
     */
    private void give_init_cards(){
        // receive the string

        for(Card c: hand){

            String message =    "INIT_CARD_TO_HAND" + "#" +
                    c.getId() + "#" +
                    c.getNameLoc(locale.getLanguage()) + "#" +
                    c.getStrength() +"#" +
                    BigSwitches.switchTypeForName(c.getType(), locale.getLanguage()) + "#" +
                    getAllText(c) ;
                sendMessage(message);
        }
    }

    /**
     * Builds all the text shown on the cards. Takes texts from all bonuses/maluses/interactives and joins them together.
     * @param c Card to make a text for.
     * @return The complete text of the card.
     */
    private String getAllText(Card c){
        StringBuilder allText = new StringBuilder();
        boolean first = true;
        if(c.getBonuses() != null){
            for(Bonus b: c.getBonuses()){
                if(!first){
                    allText.append("\n");
                }
                allText.append(b.getText(locale.getLanguage())).append("\n");
                first = false;
            }
        }
        if(c.getMaluses() != null){
            for(Malus m: c.getMaluses()){
                if(!first){
                    allText.append("\n");
                }
                allText.append(m.getText(locale.getLanguage())).append("\n");
                first = false;
            }
        }
        if(c.getInteractives() != null){
            for(Interactive b: c.getInteractives()){
                allText.append(b.getText(locale.getLanguage()));
            }
        }
        if(c.getInteractives() == null && c.getBonuses() == null && c.getMaluses()==null){
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
    public void putCardOnTable(Card c){
        String message =    "CARD_TO_TABLE" + "#" +
                c.getId() + "#" +
                c.getNameLoc(locale.getLanguage()) + "#" +
                c.getStrength() +"#" +
                BigSwitches.switchTypeForName(c.getType(), locale.getLanguage()) + "#" +
                getAllText(c);
        sendMessage(message);
    }

    @Override
    public void eraseCardFromTable(Card c){
        String message = "REMOVE_CARD_FROM_TABLE#" + c.getId();
        sendMessage(message);
        System.out.println("Telling player ("+ name + ") to remove this card from table, name of card: " + c.getName());
    }

    @Override
    public void sendNamesInOrder(String names){
        String message = "NAMES#" + names;
        sendMessage(message);
    }

    @Override
    public void countScore() {

    }

    @Override
    public void endGame(){
        String message = "END#";
        sendMessage(message);
        System.out.println("Telling player ("+ name + ") to end game, disable buttons.");

        ScoreCounter scoreCounter = new ScoreCounter(this);
        scoreCounter.start();
    }

    @Override
    public void getInitCards(){

    }

    @Override
    public ArrayList<Card> getStoredHand() {
        return null;
    }

    @Override
    public ArrayList<Integer> getScoresInRound() {
        return null;
    }

    @Override
    public void sendScore(String text){
    String message = "SCORES#" + text;
    sendMessage(message);
    System.out.println("Telling player ("+ name + ") score : " + message);
    }

    @Override
    public int getNumberOfRoundsPlayed() {
        return 0;
    }

    /**
     * Sends message to connected client.
     * @param message String message to send to client.
     */
    public void sendMessage(String message){
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



