package util;

public class Pair<X, Y> {
    private X x;
    private Y y;

    public X getX() {
        return x;
    }

    public void setX(X x) {
        this.x = x;
    }

    public Y getY() {
        return y;
    }

    public void setY(Y y) {
        this.y = y;
    }

    public X getKey(){
        return x;
    }

    public Y getValue(){
        return y;
    }

    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}
