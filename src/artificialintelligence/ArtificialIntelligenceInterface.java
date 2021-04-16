package artificialintelligence;

import server.Card;

import java.util.ArrayList;

public interface ArtificialIntelligenceInterface {
    public Card performMove(ArrayList<Card> cardsOnTable) throws CloneNotSupportedException;
    public void getInitCards() throws CloneNotSupportedException;
    public void learn();
}
