package interactive;

import artificialintelligence.State;
import server.Card;
import server.ClientHandler;

import java.util.ArrayList;

public interface InteractiveBonusInterface {
    public boolean askPlayer(ClientHandler client);
    public int changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException;
    public Interactive clone() throws CloneNotSupportedException;
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state);
}
