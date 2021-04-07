package artificialintelligence;

import server.Card;
import server.PlayerOrAI;
import server.Server;

import java.util.ArrayList;
import java.util.concurrent.FutureTask;

public class MonteCarloPlayer extends PlayerOrAI implements ArtificialIntelligenceInterface{
        ArrayList<Card> hand;
        Card bestCardToTake;
        Card bestCardToDrop;
        Server server;
        int bestPossibleScore;
        int gainThreshold;
        int rank;
        String name;
        private FutureTask<Integer> futureTask;
        private int numberOfRoundsPlayed;
        String beginningHandCards;
        int beginningHandScore;
        boolean playing = false;

    public MonteCarloPlayer(Server server, String name) throws CloneNotSupportedException {
            this.name = name;
            System.out.println("Name of GreedyPlayer " + this.name);
            hand = new ArrayList<>();
            this.server = server;
            bestPossibleScore = 0;
            bestCardToTake = null;
            bestCardToDrop = null;
            numberOfRoundsPlayed = 0;
            this.gainThreshold = gainThreshold;
            getInitCards();
            //System.out.println("Making Greedy player with gain threshold: " + gainThreshold);
        }

        @Override
        public ArrayList<Card> getHand(){
            return hand;
        }

        @Override
        public String getName(){
            return this.name;
        }


        @Override
        public void setPlaying(boolean playing){this.playing = playing;}

        @Override
        public boolean getPlaying(){
            return this.playing;
        }

        @Override
        public int getNumberOfRoundsPlayed(){
            return numberOfRoundsPlayed;
        }

        @Override
        public String getBeginningHandCards(){
            return beginningHandCards;
        }

        @Override
        public int getBeginningHandScore(){
            return beginningHandScore;
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
    public Card performMove(ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        return null;
    }
}
