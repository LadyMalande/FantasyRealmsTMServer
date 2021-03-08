package artificialintelligence;

import server.Card;

import java.util.ArrayList;

public interface ArtificialIntelligenceInterface {
    public void performMove(ArrayList<Card> cardsOnTable) throws CloneNotSupportedException;
}
