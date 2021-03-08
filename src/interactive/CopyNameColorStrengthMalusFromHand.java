package interactive;

import server.Card;
import server.ClientHandler;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CopyNameColorStrengthMalusFromHand extends Interactive  {
    public int priority = 4;
    public String text = "Copy name, type, strength and malus of any card in your hand";
    private int thiscardid;


    @Override
    public String getText(){
        return this.text;
    }
    public CopyNameColorStrengthMalusFromHand(int id){
        this.thiscardid = id;
        //System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public boolean askPlayer(ClientHandler client) {
        return client.sendInteractive("CopyCardFromHand#" + thiscardid);

    }

    @Override
    public void changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable){

    }

    @Override
    public Interactive clone() throws CloneNotSupportedException{
        CopyNameColorStrengthMalusFromHand newi = (CopyNameColorStrengthMalusFromHand)super.clone();
        newi.priority = this.priority;
        newi.text = this.text;
        newi.thiscardid = this.thiscardid;
        return newi;
    }
}
