package bonuses;


import server.Card;

import java.util.ArrayList;

/**
 * All scoring classes must have method to count the value of the scoring class.
 */
public interface ScoringInterface {
    /**
     * Counts the value of the class in given hand.
     * @param hand Cards in the agent's hand.
     * @return Points to final score.
     */
    int count(ArrayList<Card> hand);
}
