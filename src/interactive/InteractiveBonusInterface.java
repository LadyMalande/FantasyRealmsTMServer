package interactive;

import artificialintelligence.State;
import server.Card;
import server.ClientHandler;
import server.Server;

import java.util.ArrayList;

/**
 * Interface for all the interactive bonuses.
 * @author Tereza Miklóšová
 */
public interface InteractiveBonusInterface {
    /**
     * Asks the client how to handle given interactive bonus.
     * @param client Which client to ask.
     */
    void askPlayer(ClientHandler client);

    /**
     * How to affect the hand with this bonus. Counts the best possible score and then applies it.
     * @param originalHand The cards in hand.
     * @param cardsOnTable Cards on table.
     * @return Maximal reachable score with given hand.
     * @throws CloneNotSupportedException Thrown if cards have problems with cloning attributes.
     */
    int changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable, Server server,
                                  Integer idOfCardToChange, Card toChangeInto) throws CloneNotSupportedException;

    /**
     * Clones the Interactive.
     * @return Cloned Interactive.
     * @throws CloneNotSupportedException Thrown if cards have problems with cloning Interactive.
     */
    Interactive clone() throws CloneNotSupportedException;

    /**
     * Counts the potential value of the interactive bonus.
     * @param hand Cards in hand.
     * @param table Cards on table.
     * @param deckSize The number of cards remaining in the deck.
     * @param unknownCards Cards that are unknown to the agent.
     * @param state State of the agent's game.
     * @return Potential value of this Interactive bonus.
     */
    double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state);
}
