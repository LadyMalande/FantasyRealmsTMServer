package test;

import bonuses.Bonus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class ScoreCounterTest {
    ArrayList<Card> deck;
    ArrayList<Card> cardsInHands;
    Card cardToBeTested;

    //cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("")).findAny().get());

    @BeforeEach
    void initHand(){
        deck = OriginalDeck.getDeck();

        cardsInHands = new ArrayList<Card>();

    }

    @Test
    void countWarshipDeletes() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardToBeTested = deck.stream().filter(card -> card.getNameLoc("cs").equals("Válečná loď")).findAny().get();
        cardsInHands.add(cardToBeTested);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Svíčka")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Stoletá voda")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Rytíři")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Strom světa")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bouře")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Princezna")).findAny().get());
        Socket s = new Socket();
        try {
            ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
            ClientHandler c = new ClientHandler(s, "client ", dis, dos, new Server(new ServerSocket(), null));
            ScoreCounter sc = new ScoreCounter(c);
            sc.run();
            wait(1000);
            int score = c.getScore();
            assertEquals(score,155 );
        } catch(IOException | InterruptedException ec){

        }

    }

    @Test
    void countMountainDeletes() {
        ResourceBundle rb = ResourceBundle.getBundle("server.CardNames");
        cardToBeTested = deck.stream().filter(card -> card.getNameLoc("cs").equals("Hora")).findAny().get();
        cardsInHands.add(cardToBeTested);
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Bažina")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Stoletá voda")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Rytíři")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Císařovna")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Hydra")).findAny().get());
        cardsInHands.add(deck.stream().filter(card -> card.getNameLoc("cs").equals("Kouř")).findAny().get());
        Socket s = new Socket();
        try {
            ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
            ClientHandler c = new ClientHandler(s, "client ", dis, dos, new Server(new ServerSocket(), null));
            ScoreCounter sc = new ScoreCounter(c);
            sc.run();
            wait(1000);
            int score = c.getScore();
            assertEquals(score,144 );
        } catch(IOException | InterruptedException ec){

        }

    }
}