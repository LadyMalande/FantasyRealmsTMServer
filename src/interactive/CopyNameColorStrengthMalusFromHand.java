package interactive;

import server.Card;
import server.ClientHandler;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
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
                    Card thiscard = ch.getHand().stream().filter(card -> card.id == thiscardid).findAny().get();
                    Card tocopy = ch.getHand().stream().filter(card -> card.name.equals(result1)).findAny().get();
                    thiscard.name = result1;
                    thiscard.id = tocopy.id;
                    thiscard.strength = tocopy.strength;
                    thiscard.type = tocopy.type;
                    thiscard.maluses = tocopy.maluses;
                    gotanswer = true;
                }

            } catch (IOException e) {

                e.printStackTrace();
            }
        }


*/


    }
}
