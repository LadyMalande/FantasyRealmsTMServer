package interactive;





import server.ClientHandler;
import server.DeckInitializer;
import server.Type;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class CopyNameAndType extends Interactive {
    public int priority = 3;
    public String text;
    public ArrayList<Type> types;
    private int thiscardid;

    public CopyNameAndType( int id, ArrayList<Type> types) {
        this.text = "Copy name and type of any card of these types: " + giveListOfTypesWithSeparator(types, " or ");
        this.types = types;
        this.thiscardid = id;
    }

    @Override
    public String getText(){
        return this.text;
    }

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
                final String result1 = st.nextToken();
                if (result1 != null){

                    ch.getHand().stream().filter(card -> card.id == thiscardid).findAny().get().id = DeckInitializer.loadDeckFromFile().stream().filter(card -> card.name.equals(result1)).findAny().get().id;

                }
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

*/
    }
}
