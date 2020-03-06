package interactive;

import server.BigSwitches;
import server.ClientHandler;
import server.Server;
import server.Type;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

public class ChangeColor extends Interactive {
    public int priority = 3;
    public final String text;
    public int thiscardid;

    public ChangeColor(int id) {
        this.text = "Change type of one card in your hand";
        this.thiscardid = id;
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText(){
        return this.text;
    }

    // All dialogs made with the help of example on https://code.makery.ch/blog/javafx-dialogs-official/

    @Override
    public boolean askPlayer(ClientHandler client) {
        return client.sendInteractive( "ChangeColor#"+thiscardid);

    }
}
