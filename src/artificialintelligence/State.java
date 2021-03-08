package artificialintelligence;

import server.Card;

import java.util.ArrayList;
import java.util.List;

public class State {
    private ArrayList<Card> board;
    private int visitCount;
    private double winScore;

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public List<State> getAllPossibleStates() {
        List<State> possibleStates = new ArrayList<>();

        return possibleStates;
    }

    void incrementVisit() {
        this.visitCount++;
    }

    void addScore(double score) {
        if (this.winScore != Integer.MIN_VALUE)
            this.winScore += score;
    }

    void randomPlay() {

    }

}
