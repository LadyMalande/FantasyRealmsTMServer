package server;

import java.util.ArrayList;

/**
 * Interface for either the agent or the player class.
 * @author Tereza Miklóšová
 */
public interface PlayerOrAI {
    /**
     * Sends the score to the client.
     * @param s Score written in the string.
     */
    void sendScore(String s);

    /**
     * Get the number of rounds that the player played.
     * @return The number of rounds that the player played.
     */
    int getNumberOfRoundsPlayed();

    /**
     * Get the name of the player.
     * @return The name of the player.
     */
    String getName();

    /**
     * Sets the detailed score table.
     * @param sb The detailed score table.
     */
    void setScoreTable(StringBuilder sb);

    /**
     * Get the detailed score table.
     * @return The detailed score table.
     */
    StringBuilder getScoreTable();

    /**
     * Gets the final position after counting score at the end of the game.
     * @return The rank of the player on the final ladder.
     */
    int getRank();

    /**
     * Gets the score of the player.
     * @return Score of the player.
     */
    int getScore();

    /**
     * Sets the rank at the end of the game.
     * @param r Rank to set for the player.
     */
    void setRank(int r);

    /**
     * Sets the score for the player.
     * @param s The score of the player's hand.
     */
    void setScore(int s);

    /**
     * Tells if the player is current player right now.
     * @return True if the player is currently playing.
     */
    boolean getPlaying();

    /**
     * Sets if the player is current player right now.
     * @param playing True if the player is currently playing.
     */
    void setPlaying(boolean playing);

    /**
     * Gets the players cards in hand.
     * @return List of cards in hand.
     */
    ArrayList<Card> getHand();

    /**
     * Sends names of the players in the order so that its aligned for every player that their name is first.
     * @param s Text with the names of the other players.
     */
    void sendNamesInOrder(String s);

    /**
     * Count the score in hand.
     */
    void countScore();

    /**
     * Tells the client to put a card on his table.
     * @param c The card to draw on the table.
     * @throws CloneNotSupportedException Thrown if any part of the card is not cloneable.
     */
    void putCardOnTable(Card c) throws CloneNotSupportedException;

    /**
     * Tells the client to erase a card from the table.
     * @param c The card to put on the client's table.
     */
    void eraseCardFromTable(Card c);

    /**
     * Tells the client to disable all game controls. Also tells to count his score.
     */
    void endGame();

    /**
     * Gives the client initial cards to hand at the start of the game.
     * @throws CloneNotSupportedException Thrown if any part of the card is not cloneable.
     */
    void getInitCards() throws CloneNotSupportedException;

    /**
     * Gets the stored hand with cards.
     * @return The cards stored because somethig was done to the original hand.
     */
    ArrayList<Card> getStoredHand();

    /**
     * Gets list of all scores that the player had at the end of his rounds.
     * @return List of all scores achieved at the end of the rounds + the initial score.
     */
    ArrayList<Integer> getScoresInRound();
}
