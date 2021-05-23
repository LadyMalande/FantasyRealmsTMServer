package artificialintelligence;

public class StateStopPlanning {
    private int numberOfPlayers;
    private int numberOfCardsOnTable;

    public void setWinrate(double winrate) {
        this.winrate = winrate;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public int getNumberOfCardsOnTable() {
        return numberOfCardsOnTable;
    }

    public int getCardsTakenLastRound() {
        return cardsTakenLastRound;
    }

    public int getPlayerScoreApprox() {
        return playerScoreApprox;
    }

    public double getWinrate() {
        return winrate;
    }

    private int cardsTakenLastRound;
    private int playerScoreApprox;
    private double winrate;

    public StateStopPlanning(int numberOfCardsOnTable, int numberOfPlayers, int cardsTakenLastRound, int playerScoreApprox, double winrate) {
        this.numberOfCardsOnTable = numberOfCardsOnTable;
        this.numberOfPlayers = numberOfPlayers;
        this.cardsTakenLastRound = cardsTakenLastRound;
        this.playerScoreApprox = playerScoreApprox;
        this.winrate = winrate;
    }

}
