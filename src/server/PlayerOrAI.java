package server;

import java.util.ArrayList;


public class PlayerOrAI {
    boolean playing;
    String name;
    public StringBuilder scoreTable;
    int rank;
    public int score;
    private int numberOfRoundsPlayed;
    String beginningHandCards;
    int beginningHandScore;


    public void sendScore(String s){

    }

    public int getNumberOfRoundsPlayed(){
        return numberOfRoundsPlayed;
    }

    public String getName(){
        return name;
    }

    public String getBeginningHandCards(){
        return beginningHandCards;
    }

    public int getBeginningHandScore(){
        return beginningHandScore;
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


    public void getInitCards() throws CloneNotSupportedException {
    }
}
