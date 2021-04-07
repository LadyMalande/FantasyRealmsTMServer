package artificialintelligence;

import server.Card;

import java.util.ArrayList;
import java.util.Random;

public interface ArtificialIntelligenceInterface {
    public Card performMove(ArrayList<Card> cardsOnTable) throws CloneNotSupportedException;
    public void getInitCards() throws CloneNotSupportedException;
}
