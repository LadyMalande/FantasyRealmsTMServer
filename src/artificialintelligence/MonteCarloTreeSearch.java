package artificialintelligence;

import server.Server;

import java.util.List;

public class MonteCarloTreeSearch {

/*
    private int getMillisForCurrentLevel() {
        return 2 * (this.level - 1) + 1;
    }

    // Move is defined as Take Card & Drop Card
    public void findAndPlayNextMove(Server server){
        Tree tree = new Tree(new Node(null,null,state));
        Node root = tree.getRoot();

        long start = System.currentTimeMillis();
        long end = start + 60 * getMillisForCurrentLevel();

        while (System.currentTimeMillis() < end) {
            // Phase 1 = Selection
            Node candidate = selectCandidate(root);

            // Phase 2 = Expansion
            if (candidate.getState().getBoard().checkStatus() == Board.IN_PROGRESS)
                expandNode(candidate);

            // Phase 3 = Simulation
            Node toExplore = candidate;
            if (candidate.getChildren().size() > 0) {
                toExplore = candidate.getRandomChild();
            }
            int playoutResult = simulateGame(toExplore);
            // Phase 4 = Update
            backPropogation(toExplore, playoutResult);
        }

        Node winnerNode = root.getBestChild();
        tree.setRoot(winnerNode);
        // tell which card to choose and which to drop
        return new PickCardDropCard(winnerNode.getAction().getPickedCard(), winnerNode.getState().getDroppedCard());
    }

    private Node selectCandiadate(Node root) {
        Node node = root;
        while (node.getChildren().size() != 0) {
            node = UCT.findBestUCTinChildren(node);
        }
        return node;
    }

    private void expandNode(Node node) {
        List<State> possibleStates = node.getState().getAllPossibleStates();
        possibleStates.forEach(state -> {
            Node newNode = new Node(state);
            newNode.setParent(node);
            node.getChildren().add(newNode);
        });
    }

    private void backPropogation(Node nodeToExplore, int playerNo) {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getPlayerNo() == playerNo)
                tempNode.getState().addScore(WIN_SCORE);
            tempNode = tempNode.getParent();
        }
    }

    private int simulateRandomPlayout(Node node) {
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();
        int boardStatus = tempState.getBoard().checkStatus();

        if (boardStatus == opponent) {
            tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return boardStatus;
        }
        while (boardStatus == Board.IN_PROGRESS) {
            tempState.togglePlayer();
            tempState.randomPlay();
            boardStatus = tempState.getBoard().checkStatus();
        }

        return boardStatus;
    }


 */

}
