package artificialintelligence;

import server.Card;

import java.util.ArrayList;

/**
 * Interface for all AI agents.
 * @author Tereza Miklóšová
 */
public interface ArtificialIntelligenceInterface {
    /**
     * This is main logic behind agents behaviour. Plays the agents turn.
     * Provides answers how to play first action and the second action.
     * @param cardsOnTable Cards on the table used for calculating of this round.
     * @return Card to drop on the table.
     * @throws CloneNotSupportedException Can throw this exception if something goes wrong with cloning on cards.
     */
    Card performMove(ArrayList<Card> cardsOnTable) throws CloneNotSupportedException;

    /**
     * All agents must know how to get initial cards to their hands.
     * @throws CloneNotSupportedException Can throw this exception if something goes wrong with cloning on cards.
     */
    void getInitCards() throws CloneNotSupportedException;

    /**
     * If the agent wants to learn it must implement this method.
     */
    void learn();
}
