package artificialintelligence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Node {
    public Node(Node parent, ArrayList<Node> children, State state) {
        this.parent = parent;
        this.children = children;
        this.state = state;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    Node parent;
    ArrayList<Node> children;
    State state;

    public Node getBestChild() {
        return Collections.max(this.children, Comparator.comparing(c -> {
            return c.getState().getVisitCount();
        }));
    }


    public Node getRandomChild() {
        int noOfPossibleMoves = this.children.size();
        int selectRandom = (int) (Math.random() * noOfPossibleMoves);
        return this.children.get(selectRandom);
    }

}
