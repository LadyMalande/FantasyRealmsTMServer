package server;

import bonuses.Bonus;
import interactive.Interactive;
import maluses.Malus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerOrAI {
    boolean playing;
    String name;
    public StringBuilder scoreTable;
    int rank;
    public int score;


    public void sendScore(String s){

    }

    public ArrayList<Card> getHand(){
        return null;
    }

    public void sendNamesInOrder(String s) {
        String[] message = s.split("#");
        if (message[1].startsWith("$&$START$&$")) {
            playing = true;

        }
    }

    public void countScore(){

    }

    public void putCardOnTable(Card c) throws CloneNotSupportedException {

    }
    public void eraseCardFromTable(Card c){

    }
    public void endGame(){
        countScore();
    }



}
