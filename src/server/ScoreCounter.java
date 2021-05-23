package server;

import bonuses.Bonus;
import interactive.Interactive;
import maluses.Malus;
import util.BigSwitches;
import util.HandCloner;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class for counting score of real player.
 * @author Tereza Miklóšová
 */
public class ScoreCounter extends Thread {
    /**
     * Client to communicate with if needed for resolving interactives.
     */
    private ClientHandler client;

    /**
     * Constructor for the counter.
     * @param client Client to communicate with if needed.
     */
    public ScoreCounter(ClientHandler client){

        this.client = client;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void run() {
            client.score = countScore();
            while (client.score < 0) {

            }
            client.hostingServer.increaseCountedScoreNumber();
        //System.out.println("After client got score " + client.hostingServer.numberOfCountedScores.get());
    }


    /**
     * Counts the final score of the player.
     * @return The final score for the player at the end of the game.
     */
    private int countScore() {

        client.futureTask = new FutureTask<>(() -> {
            try {
                ArrayList<Card> whatToRemove = new ArrayList<>();
                int sum = 0;
                ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(client.getHand());
                int MAX_PRIORITY = 8;
                int MIN_PRIORITY = 0;
                client.setWaitingCounterForInteractives();
                for (int i = MIN_PRIORITY; i <= MAX_PRIORITY; i++) {

                    if(i == 6){
                        whatToRemove.clear();
                    }
                    if (i == 5) {
                        while (client.interactivesResolved.get() < client.interactivesCount) {
                            // wait
                            try {
                                Thread.sleep(1000);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    for (Card c : copyDeckToMakeChanges) {
                        if (client.getHand().contains(c)) {
                            if(!whatToRemove.contains(c))
                            if (c.getInteractives() != null)
                                for (Interactive in : c.getInteractives()) {

                                    if (in.getPriority() == i) {
                                        client.interactivesResolvedAtomicBoolean = new AtomicBoolean(false);
                                        System.out.println("Asking client for interactive" + client.interactivesResolvedAtomicBoolean.get());
                                        in.askPlayer(client);

                                        try {

                                            while(!client.interactivesResolvedAtomicBoolean.get()){
                                                Thread.sleep(1000);
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    if(i == 2){
                                        whatToRemove.clear();
                                    }
                                }
                            if (c.getBonuses() != null)
                                for (Bonus b : c.getBonuses()) {
                                    if (b.getPriority() == 5 && i == 2) {
                                        int sumForCardBonus = b.count(client.getHand());
                                        sum += sumForCardBonus;
                                    }
                                    if (b.getPriority() == 5 && i == 0) {
                                        int sumForCardBonus = b.count(client.getHand());
                                        sum += sumForCardBonus;
                                    }
                                    if (b.getPriority() == i) {
                                        int sumForCardBonus = b.count(client.getHand());
                                        sum += sumForCardBonus;
                                    }
                                }
                            if (c.getMaluses() != null)
                                for (Malus m : c.getMaluses()) {
                                    if (m.getPriority() == 6 && i == 1) {

                                        sum += m.count(client.getHand(), whatToRemove);
                                    } else if(m.getPriority() == 6 && i == 6){
                                        sum += m.count(client.getHand(), whatToRemove);
                                    }
                                    else if(m.getPriority() == 6 && i == 2){
                                        sum += m.count(client.getHand(), whatToRemove);
                                    }
                                    else if (m.getPriority() == i) {

                                        int sumForCardMalus = m.count(client.getHand());
                                        sum += sumForCardMalus;
                                    }
                                }
                        }
                    }

                    if (i == 1 || i == 2) {
                        for(Card c:whatToRemove){
                            if(c.getInteractives() != null && !c.getInteractives().isEmpty()){
                                client.interactivesCount--;
                            }
                        }

                    }
                    if(i == 6){
                        client.getHand().removeIf(whatToRemove::contains);
                    }
                }
                client.setScoreTable(new StringBuilder());
                HandCloner hc = new HandCloner();
                try {
                    ArrayList<Card> clonedHand = hc.cloneHand(null, client.getHand());

                for (Card c : clonedHand) {
                    sum += c.getStrength();
                    client.getScoreTable().append(BigSwitches.switchNameForSimplifiedlName(c.getName())).append(";").append(c.getType().toString()).append(";").append(c.getStrength());
                    int bonus = 0;
                    if(c.getBonuses() != null && !c.getBonuses().isEmpty()){
                        for(Bonus b: c.getBonuses()){
                            bonus += b.count(client.getHand());
                        }
                        if(bonus != 0) {
                            client.getScoreTable().append(";BONUS:").append(bonus);
                        }
                    }
                    int malus = 0;
                    if(c.getMaluses() != null && !c.getMaluses().isEmpty()){
                        for(Malus m: c.getMaluses()){
                            malus += m.count(client.getHand());
                        }
                        if(malus != 0) {

                                client.getScoreTable().append(";MALUS:").append(malus);


                        }
                    }
                    client.getScoreTable().append("\n");
                }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                for(Card c: whatToRemove){
                    client.getScoreTable().append(BigSwitches.switchNameForSimplifiedlName(c.getName())).append(";").append(c.getType().toString()).append(";").append("DELETED\n");
                }
                System.out.println(client.getScoreTable().toString());
                return sum;


            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
            return -999;
        }
        );

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(client.futureTask);
        Integer score = -1000;
        try {
            score = client.futureTask.get();
            System.out.println("Score got from FutureTask: " + score.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        es.shutdown();

        return score;
    }
}

/*
for (Card c : clonedHand) {
                    sum += c.getStrength();
                    int tabCount = 3 - (c.getName().length() / 8);
                    client.getScoreTable().append(c.getId()).append(";").append(c.getType().toString()).append(";").append(c.getStrength());
                    int bonus = 0;
                    if(c.getBonuses() != null && !c.getBonuses().isEmpty()){
                        for(Bonus b: c.getBonuses()){
                            bonus += b.count(client.getHand());
                        }
                        if(bonus != 0) {
                            for(int i=0; i< tabCount; i++) {
                                client.getScoreTable().append("\t");
                            }
                            client.getScoreTable().append(";BONUS:").append(bonus);
                        }
                    }
                    int malus = 0;
                    if(c.getMaluses() != null && !c.getMaluses().isEmpty()){
                        for(Malus m: c.getMaluses()){
                            malus += m.count(client.getHand());
                        }
                        if(malus != 0) {
                            if(bonus != 0) {
                                client.getScoreTable().append("\tMALUS: ").append(malus);
                            } else{
                                for(int i=0; i< tabCount; i++) {
                                    client.getScoreTable().append("\t");
                                }
                                client.getScoreTable().append("MALUS: ").append(malus);
                            }
                        }
                    }
                    client.getScoreTable().append("\n");
 */
