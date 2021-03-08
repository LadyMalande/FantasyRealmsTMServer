package interactive;

import server.*;

import java.util.ArrayList;


public class ChangeColor extends Interactive {
    public int priority = 3;
    public String text;
    public int thiscardid;

    public ChangeColor(int id) {
        this.text = "Change type of one card in your hand";
        this.thiscardid = id;
        //System.out.println("Card INIT: Text: " + getText());
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

    @Override
    public void changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable){

    }

    @Override
    public Interactive clone() throws CloneNotSupportedException{
        ChangeColor newi = (ChangeColor)super.clone();
        newi.priority = this.priority;
        newi.text = this.text;
        newi.thiscardid = this.thiscardid;
        return newi;
    }
}
