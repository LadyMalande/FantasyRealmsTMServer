package interactive;

import server.ClientHandler;
import server.Type;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class DeleteOneMalusOnType extends Interactive {
    public int priority = 1;
    public String text;
    public ArrayList<Type> types;
    private int thiscardid;

    public DeleteOneMalusOnType(int id, ArrayList<Type> types) {
        this.text = "Delete one malus on card of type " + giveListOfTypesWithSeparator(types, " or ");
        this.types = types;
        this.thiscardid = id;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void askPlayer() {

     /*
        //Header CLientHandler ch, Socket s
        boolean gotanswer = false;
        while (!gotanswer) {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
                // receive the string

                String received = dis.readUTF();

                System.out.println(received);

                // break the string into message and recipient part
                StringTokenizer st = new StringTokenizer(received, "#");
                final String result1 = st.nextToken();
                final String result2 = st.nextToken();
                if (result2 != null) {
                    ch.getHand().stream().filter(card -> card.name.equals(result1)).findAny().ifPresent(removeMalusHere -> removeMalusHere.maluses.removeIf(malus -> malus.getText().equals(result2)));
                    gotanswer = true;
                }
            } catch (IOException e) {

                e.printStackTrace();
            }


        }

      */
    }
}
