package interactive;



import server.Card;
import server.ClientHandler;
import server.Type;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class TakeCardOfTypeAtTheEnd extends Interactive  {
    public int priority = 0;
    public final String text;
    public ArrayList<Type> types;
    private int thiscardid;

    public TakeCardOfTypeAtTheEnd(int id,ArrayList<Type> types) {
        this.thiscardid = id;
        this.text = "At the end of the game, you can take one card from the table which is of type " + giveListOfTypesWithSeparator(types, " or ") + " as your eighth card";
        this.types = types;
    }

    @Override
    public String getText(){
        return this.text;
    }
    @Override
    public int getPriority(){ return this.priority; }

    private String getNamesOfTypeCardsOnTable(ClientHandler client){
        StringBuilder str = new StringBuilder();

        for(Type t: types){
            for(Card c: client.hostingServer.cardsOnTable){
                if(c.type.equals(t)){
                    str.append(c.name + ",");
                }
            }
        }

        return str.toString();
    }

    @Override
    public boolean askPlayer(ClientHandler client) {

        return client.sendInteractive("TakeCardOfType#" + thiscardid + "#" + getNamesOfTypeCardsOnTable(client));
    }
}
