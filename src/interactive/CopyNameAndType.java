package interactive;





import server.ArrayListCreator;
import server.ClientHandler;
import server.DeckInitializer;
import server.Type;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class CopyNameAndType extends Interactive {
    public int priority = 3;
    public final String text;
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
    public boolean askPlayer(ClientHandler client) {
        ArrayList<String> choices = new ArrayList<>();
        choices = ArrayListCreator.createListOfNamesFromTypes(types);
        String toSend = giveStringOfStringsWithSeparator(choices, ",");
        return client.sendInteractive("CopyNameAndType#" + thiscardid + "#" + toSend);
    }
}
