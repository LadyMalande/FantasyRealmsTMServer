package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Card;
import server.ClientHandler;
import server.ScoreCounter;
import server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreCounterTest {
    ArrayList<Card> deck;
    ArrayList<Card> cardsInHands;
    Card cardToBeTested;

    @BeforeEach
    void initHand(){
        deck = OriginalDeck.getDeck();

        cardsInHands = new ArrayList<>();

    }

    @Test
    void countWarshipDeletes() {
        cardToBeTested = deck.stream().filter(card -> card.getNameLoc("cs").equals("Válečná loď")).findAny().orElse(null);
        cardsInHands.add(cardToBeTested);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Svíčka")).findAny().orElse(null));
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Stoletá voda")).findAny().orElse(null));
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Rytíři")).findAny().orElse(null));
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Strom světa")).findAny().orElse(null));
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bouře")).findAny().orElse(null));
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Princezna")).findAny().orElse(null));
        Socket s = new Socket();
        try {
            ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
            ClientHandler c = new ClientHandler(s, "client ", dis, dos, new Server(new ServerSocket(), null));
            ScoreCounter sc = new ScoreCounter(c);
            sc.start();
            wait(1000);
            int score = c.getScore();
            assertEquals(score,155 );
        } catch(IOException | InterruptedException ex){
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }

    }

    @Test
    void countMountainDeletes() {
        cardToBeTested = deck.stream().filter(card -> card.getNameLoc("cs").equals("Hora")).findAny().orElse(null);
        cardsInHands.add(cardToBeTested);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bažina")).findAny().orElse(null));
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Stoletá voda")).findAny().orElse(null));
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Rytíři")).findAny().orElse(null));
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Císařovna")).findAny().orElse(null));
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Hydra")).findAny().orElse(null));
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Kouř")).findAny().orElse(null));
        Socket s = new Socket();
        try {
            ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());

            ClientHandler c = new ClientHandler(s, "client ", dis, dos, new Server(new ServerSocket(), null));
            ScoreCounter sc = new ScoreCounter(c);
            sc.start();
            wait(1000);
            int score = c.getScore();
            assertEquals(score,144 );
        } catch(IOException | InterruptedException ex){
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }

    }
}