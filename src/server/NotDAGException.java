package server;


import maluses.Malus;

public class NotDAGException extends Throwable {
    private Type t;
    private Malus m;
    public NotDAGException(Type t, Malus m){
        this.t = t;
        this.m = m;
    }

    public String toString() {
        return "This malus breaks acyclicness of malus graph: type " + BigSwitches.switchTypeForName(t) + ", malus" + m.toString();
    }
}