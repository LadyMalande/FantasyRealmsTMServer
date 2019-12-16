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
    public void askPlayer() {

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
}
