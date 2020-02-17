package interactive;

import server.BigSwitches;
import server.ClientHandler;
import server.Type;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

public class Interactive implements InteractiveBonusInterface , Serializable {
    public String text;
    public int priority=3;

    public String getText(){
        return this.text;
    }
    public int getPriority(){
        return this.priority;
    }
    @Override
    public boolean askPlayer(ClientHandler client) {
        return true;
    }

    public String giveListOfTypesWithSeparator(ArrayList<Type> types, String separator){
        String listtypes = "";
        boolean first = true;
        for(Type type: types){
            if(!first){
                listtypes += separator;
            }
            listtypes += BigSwitches.switchTypeForName(type);
            first = false;
        }
        return listtypes;
    }

    public String giveStringOfStringsWithSeparator(ArrayList<String> strings, String separator){
        String listtypes = "";
        boolean first = true;
        for(String str: strings){
            if(!first){
                listtypes += separator;
            }
            listtypes += str;
            first = false;
        }
        return listtypes;
    }
}
