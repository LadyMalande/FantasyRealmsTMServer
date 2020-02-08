package interactive;

import server.BigSwitches;
import server.ClientHandler;
import server.Server;
import server.Type;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

public class ChangeColor extends Interactive {
    public int priority = 2;
    public final String text;
    public int thiscardid;

    public ChangeColor(int id) {
        this.text = "Change type of one card in your hand";
        this.thiscardid = id;
    }

    @Override
    public String getText(){
        return this.text;
    }

    // All dialogs made with the help of example on https://code.makery.ch/blog/javafx-dialogs-official/

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
                final String result2 = st.nextToken();
                if (result1 != null && result2 != null) {gotanswer = true;

                    ch.getHand().stream().filter(x -> x.name.equals(result1)).findFirst().get().type = BigSwitches.switchNameForType(result2);
                }
            } catch (IOException e) {

                e.printStackTrace();
            }
        }


// Traditional way to get the response value.

*/

    }
}
