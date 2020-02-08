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
    @Override
    public void askPlayer() {

        /*
        boolean gotanswer = false;
        while(!gotanswer) {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
                // receive the string

                String received = dis.readUTF();

                System.out.println(received);

                // break the string into message and recipient part
                StringTokenizer st = new StringTokenizer(received, "#");
                final String result = st.nextToken();
                if (result != null){
                    if(ch.hostingServer.cardsOnTable.stream().anyMatch(card -> card.name.equals(result))) {
                        Card tocopy = ch.hostingServer.cardsOnTable.stream().filter(card -> card.name.equals(result)).findAny().orElse(null);
                        ch.getHand().add(tocopy);
                        ch.hostingServer.cardsOnTable.remove(tocopy);
                    }
                    gotanswer = true;
                }
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

*/
    }
}
