package interactive;

import server.ClientHandler;

import java.net.Socket;

public interface InteractiveBonusInterface {
    public boolean askPlayer(ClientHandler client);
}
