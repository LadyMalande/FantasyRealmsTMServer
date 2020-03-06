package interactive;

import server.Card;
import server.ClientHandler;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

public class CopyNameColorStrengthMalusFromHand extends Interactive  {
    public int priority = 4;
    public final String text = "Copy name, type, strength and malus of any card in your hand";
    private int thiscardid;


    @Override
    public String getText(){
        return this.text;
    }
    public CopyNameColorStrengthMalusFromHand(int id){
        this.thiscardid = id;
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public boolean askPlayer(ClientHandler client) {
        return client.sendInteractive("CopyCardFromHand#" + thiscardid);

    }
}
