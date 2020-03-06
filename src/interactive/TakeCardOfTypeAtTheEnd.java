package interactive;



import server.Card;
import server.ClientHandler;
import server.Type;
import java.util.ArrayList;

public class TakeCardOfTypeAtTheEnd extends Interactive  {
    public int priority = 2;
    public final String text;
    public ArrayList<Type> types;
    private int thiscardid;

    public TakeCardOfTypeAtTheEnd(int id,ArrayList<Type> types) {
        this.thiscardid = id;
        this.text = "At the end of the game, you can take one card from the table which is of type " + giveListOfTypesWithSeparator(types, " or ") + " as your eighth card";
        this.types = types;
        System.out.println("Card INIT: Text: " + getText());
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
                    str.append(c.name).append(",");
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
