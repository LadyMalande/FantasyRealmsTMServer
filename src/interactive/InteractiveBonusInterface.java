package interactive;

import server.Card;
import server.ClientHandler;

import java.net.Socket;
import java.util.ArrayList;

public interface InteractiveBonusInterface {
    public boolean askPlayer(ClientHandler client);
    public void changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException;
    public Interactive clone() throws CloneNotSupportedException;
}
