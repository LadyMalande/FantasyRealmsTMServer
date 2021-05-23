package artificialintelligence;

import bonuses.Bonus;
import interactive.ChangeColor;
import interactive.CopyNameAndType;
import interactive.Interactive;
import interactive.TakeCardOfTypeAtTheEnd;
import maluses.Malus;
import server.Card;
import server.PlayerOrAI;
import server.Server;
import util.BigSwitches;
import util.HandCloner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Class for counting the best possible score from the card combination given.
 * Uses the interactive bonuses to its best computation.
 * @author Tereza Miklóšová
 */
public class ScoreCounterForAI {
    /**
     * Server to give statistics to.
     */
    private static Server server;

    private static PlayerOrAI agent = null;

    public static void setAgent(PlayerOrAI agent) {
        ScoreCounterForAI.agent = agent;
    }

    /**
     * Set {@link ScoreCounterForAI#server}
     * @param server {@link ScoreCounterForAI#server}
     */
    public void setServer(Server server) {
        ScoreCounterForAI.server = server;
    }

    public ScoreCounterForAI(Server server){
        this.setServer(server);
    }
    public ScoreCounterForAI(){

    }


    /**
     * Goes incrementally through all the cards in hand and applies the bonuses, penalties and interactives base on their priorities.
     * @param handOriginal Hand to compute the best score for.
     * @param cardsOnTable Cards on the table. Used for Necromancer.
     * @param quickCount True if the agent doesn't want to compute the whole method after obtaining the best possible
     *                   score from some of the other computations needed.
     * @return The best possible score the agent can get from this hand.
     */
    public static int countBestPossibleScore(ArrayList<Card> handOriginal, ArrayList<Card> cardsOnTable, boolean quickCount){
        try {
            ArrayList<Card> whatToRemove = new ArrayList<>();
            int sum = 0;
            // Alter hand by adding a new card with Necromancer before resolving the rest of the cards in loop
            int score = checkIfThereIsNecromancerLikeCard(handOriginal, cardsOnTable);
            if(score != -999){
                return score;
            }

            ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(handOriginal);
            int MAX_PRIORITY = 8;
            int MIN_PRIORITY = 0;

            for (int i = MIN_PRIORITY; i <= MAX_PRIORITY; i++) {

                if(i == 6){
                    whatToRemove.clear();
                }
                for (Card c : copyDeckToMakeChanges) {
                    if (handOriginal.contains(c)) {

                        if (!whatToRemove.contains(c)) {
                            // Compute the interactive bonuses if there are any
                            if (c.getInteractives() != null) {
                                ArrayList<Interactive> interactivesToMakeChanges = new ArrayList<>(c.getInteractives());
                                for (Interactive in : interactivesToMakeChanges) {

                                    if (in.getPriority() == i) {
                                        if (!(in instanceof TakeCardOfTypeAtTheEnd)) {
                                            //somehow use the interactives
                                            long startTime = System.nanoTime();
                                            Integer integer = null;
                                            Card cardToChangeInto = null;
                                            int finalScore = in.changeHandWithInteractive(handOriginal, cardsOnTable,
                                                    server, integer, cardToChangeInto );
                                            //System.out.println("Hand after change with Interactive: ");
                                            for(Card cardAfterchange : handOriginal){
                                                //System.out.println(BigSwitches.switchIdForName(cardAfterchange.getId()) + " " + cardAfterchange.getName() + " " + cardAfterchange.getType() + " " + cardAfterchange.getStrength());
                                            }
                                            if (in instanceof ChangeColor) {
                                                long elapsedTime = (System.nanoTime() - startTime) / 1000000;
                                                if(server != null){
                                                    if(server.getBufferForResult() != null)
                                                    server.getBufferForResult().appendToTimeSpentInChangeColor(String.valueOf(elapsedTime));

                                                }
                                            }
                                            if (in instanceof CopyNameAndType) {
                                                long elapsedTime = (System.nanoTime() - startTime) / 1000000;
                                                if(server != null){
                                                    if(server.getBufferForResult() != null)
                                                    server.getBufferForResult().appendToTimeSpentInCopyNameAndType(String.valueOf(elapsedTime));

                                                }
                                            }
                                            if (quickCount) {
                                                return finalScore;
                                            }
                                        }
                                    }
                                    if (i == 2) {
                                        whatToRemove.clear();
                                    }
                                }
                            }
                            // Compute the bonuses if the card has any
                            if (c.getBonuses() != null)
                                for (Bonus b : c.getBonuses()) {
                                    if (b.getPriority() == 5 && i == 2) {
                                        int sumForCardBonus = b.count(handOriginal);
                                        sum += sumForCardBonus;
                                    }
                                    if (b.getPriority() == 5 && i == 0) {
                                        int sumForCardBonus = b.count(handOriginal);
                                        sum += sumForCardBonus;
                                    }
                                    if (b.getPriority() == i) {
                                        int sumForCardBonus = b.count(handOriginal);
                                        sum += sumForCardBonus;
                                    }
                                }

                            // Compute the penalties, or compute which cards will be deleted.
                            if (c.getMaluses() != null) {
                                for (Malus m : c.getMaluses() ) {
                                    if (m.getPriority() == 6 && i == 1) {
                                        sum += m.count(handOriginal, whatToRemove);
                                    } else if (m.getPriority() == 6 && i == 6) {
                                        sum += m.count(handOriginal, whatToRemove);
                                    } else if (m.getPriority() == 6 && i == 2) {
                                        sum += m.count(handOriginal, whatToRemove);
                                    } else if (m.getPriority() == 7) {
                                        sum += m.count(handOriginal, whatToRemove);
                                    }
                                    else if (m.getPriority() == i) {
                                        int sumForCardMalus = m.count(handOriginal);
                                        sum += sumForCardMalus;
                                    }
                                }
                            }
                        }
                    }

                // Removes all the cards that are deleted from the hand.
                if(i == 6){
                    handOriginal.removeIf(whatToRemove::contains);
                }
                }
            }

            int time = 0;
            while(time < 100){
                time++;
            }

            // Count the scores on the cards that werent deleted
            for (Card c : handOriginal) {
                // Count the basic strength of the card
                sum += c.getStrength();
            }

            // Gather details about score
            if(agent != null && !quickCount) {
                HandCloner hc = new HandCloner();
                try {
                    ArrayList<Card> clonedHand = HandCloner.cloneHand(null, handOriginal);

                    for (Card c : clonedHand) {
                        agent.getScoreTable().append(BigSwitches.switchNameForSimplifiedlName(c.getName())).append(";").append(c.getType().toString()).append(";").append(c.getStrength());
                        int bonus = 0;
                        if (c.getBonuses() != null && !c.getBonuses().isEmpty()) {
                            for (Bonus b : c.getBonuses()) {
                                bonus += b.count(agent.getHand());
                            }
                            if (bonus != 0) {
                                agent.getScoreTable().append(";BONUS:").append(bonus);
                            }
                        }
                        int malus = 0;
                        if (c.getMaluses() != null && !c.getMaluses().isEmpty()) {
                            for (Malus m : c.getMaluses()) {
                                malus += m.count(agent.getHand());
                            }
                            if (malus != 0) {

                                agent.getScoreTable().append(";MALUS:").append(malus);


                            }
                        }
                        agent.getScoreTable().append("\n");
                    }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

            for(Card c: whatToRemove){
                agent.getScoreTable().append(BigSwitches.switchNameForSimplifiedlName(c.getName())).append(";").append(c.getType().toString()).append(";").append("DELETED\n");
            }
            //System.out.println(agent.getScoreTable().toString());

            }
            return sum;

        } catch (NullPointerException | CloneNotSupportedException ex) {
            ex.printStackTrace();
            System.out.println("All count score");
        }
        return -1;
    }


    /**
     * Uses the FutureTask to compute the score the agent has in its given combination of cards.
     * @param handOriginal The cards to be computed.
     * @param cardsOnTable The cards on the table. Used for the Necromancer type of card.
     * @param quickCount True if the counting is to be finished as soon as the method gets the best possible score.
     * @return The best possible score obtainable with the hand.
     */
    public int countScore(List<Card> handOriginal, List<Card> cardsOnTable, boolean quickCount){
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            try {
                if (cardsOnTable == null) {
                    if (!quickCount) {
                        return countBestPossibleScore(new ArrayList<>(handOriginal), new ArrayList<>(), false);
                    }
                    return countBestPossibleScore(new ArrayList<>(handOriginal), new ArrayList<>(), true);
                } else {
                    if (!quickCount) {
                        return countBestPossibleScore(new ArrayList<>(handOriginal), new ArrayList<>(), false);
                    }
                    return countBestPossibleScore(new ArrayList<>(handOriginal), new ArrayList<>(cardsOnTable), true);
                }


            } catch (NullPointerException ex) {
                ex.printStackTrace();
                for (Card c : handOriginal) {
                    System.out.println(c.getName());
                }
                System.out.println("Nullpointer Exception while counting Score for AI");
            }
            return -1;
        });
                ExecutorService es = Executors.newSingleThreadExecutor();
                es.execute(futureTask);
                Integer score = -1000;
                try {
                    score = futureTask.get();
                } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                }

                es.shutdown();
                return score;

    }

    /**
     * Uses the Necromancer card separately so that the cards are not affected by it.
     * @param handOriginal The cards in hand.
     * @param cardsOnTable The cards on the table.
     * @return The best possible score computed in this method.
     * @throws CloneNotSupportedException Thrown if something went wrong with the card cloning.
     */
    private static int checkIfThereIsNecromancerLikeCard(ArrayList<Card> handOriginal, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(handOriginal);
        Interactive interactiveToDelete = null;
        ArrayList<Interactive> whereToDelete = null;
        for (Card c : copyDeckToMakeChanges) {

            if (c.getInteractives() != null) {
                ArrayList<Interactive> toIterateThrough = new ArrayList<>(c.getInteractives() );
                for (Interactive in : toIterateThrough) {
                    if (in instanceof TakeCardOfTypeAtTheEnd) {
                        Integer integer = null;
                        Card cardToChangeInto = null;
                        int score = in.changeHandWithInteractive(handOriginal, cardsOnTable,
                                server, integer, cardToChangeInto );
                            if(score != -999){
                                return score;
                            }
                        whereToDelete = c.getInteractives() ;
                        interactiveToDelete = in;
                    }
                }
            }

        }
           // Delete this interactive to not get stuck in endless loop
        if (interactiveToDelete != null){
            whereToDelete.remove(interactiveToDelete);
        }
        return -999;
    }
}
