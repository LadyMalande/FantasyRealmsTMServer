package interactive;

import maluses.Malus;
import server.Card;
import server.ClientHandler;
import server.Type;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class DeleteOneMalusOnType extends Interactive {
    public int priority = 0;
    public final String text;
    public ArrayList<Type> types;
    private int thiscardid;

    public DeleteOneMalusOnType(int id, ArrayList<Type> types) {
        this.text = "Delete one malus on type " + giveListOfTypesWithSeparator(types, " or ");
        this.types = types;
        this.thiscardid = id;
        System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    private String getAllMalusesForTypesToString(ClientHandler client){
        StringBuilder str = new StringBuilder();
        boolean firstTime = true;
        for(Type t: types){
            for(Card c: client.getHand()){
                if(c.type.equals(t)){
                    if(c.maluses != null && !c.maluses.isEmpty()){
                        if(!firstTime){
                            str.append("%");
                        }

                        boolean firstTimeInMaluses = true;
                        for(Malus m: c.maluses){
                            if(!firstTimeInMaluses){
                                str.append("%");
                            }
                            str.append(c.name + ": ");
                            str.append(m.getText());
                            firstTimeInMaluses = false;
                        }
                        firstTime = false;
                    }
                }
            }
        }
        return str.toString();
    }

    @Override
    public boolean askPlayer(ClientHandler client) {
        //            ch.getHand().stream().filter(card -> card.name.equals(result1)).findAny().ifPresent(removeMalusHere -> removeMalusHere.maluses.removeIf(malus -> malus.getText().equals(result2)));
       String s = getAllMalusesForTypesToString(client);
        if(s.isEmpty()){
            synchronized(client.interactivesResolvedAtomicBoolean) {
                client.interactivesResolved.incrementAndGet();
                //client.futureTask.notify();
                client.interactivesResolvedAtomicBoolean.set(true);
                return true;
            }
        }
        else{
            return client.sendInteractive("DeleteOneMalusOnType#" + thiscardid + "#" + getAllMalusesForTypesToString(client));

        }
    }
}
