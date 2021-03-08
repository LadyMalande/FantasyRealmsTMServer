package interactive;

import server.ArrayListCreator;
import server.Card;
import server.ClientHandler;
import server.Type;
import java.util.ArrayList;

public class CopyNameAndType extends Interactive {
    public int priority = 4;
    public String text;
    public ArrayList<Type> types;
    private int thiscardid;

    public CopyNameAndType( int id, ArrayList<Type> types) {
        this.text = "Copy name and type of any card of these types: " + giveListOfTypesWithSeparator(types, " or ");
        this.types = types;
        this.thiscardid = id;
        //System.out.println("Card INIT: Text: " + getText());
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

    @Override
    public void changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable){

    }

    @Override
    public Interactive clone() throws CloneNotSupportedException{
        CopyNameAndType newi = (CopyNameAndType)super.clone();
        newi.priority = this.priority;
        newi.text = this.text;
        newi.types = (ArrayList<Type>) this.types.clone();
        newi.thiscardid = this.thiscardid;
        return newi;
    }
}
