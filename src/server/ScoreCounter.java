package server;

import bonuses.Bonus;
import interactive.Interactive;
import maluses.Malus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScoreCounter extends Thread {
    private ClientHandler client;

    public ScoreCounter(ClientHandler client){
        this.client = client;
    }


    @Override
    public void run() {
        client.score = countScore();
        while(client.score < 0){
            // wait;
        }
        client.hostingServer.increaseCountedScoreNumber();
    }

    private int countScore() {

        client.futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() {
                try {

                    int sum = 0;
                    System.out.println("Pocet karet v ruce: " + client.getHand().size());
                    HashMap<Type, Malus> types_maluses = new HashMap<>();
                    ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(client.getHand());
                    int MAX_PRIORITY = 8;
                    int MIN_PRIORITY = 0;
                    client.setWaitingCounterForInteractives();
                    for (int i = MIN_PRIORITY; i <= MAX_PRIORITY; i++) {

                        if (i == 5) {
                            while (client.interactivesResolved.get() < client.interactivesCount) {
                                // wait
                                System.out.println("MaxCount: " + client.interactivesCount + " and we have: " + client.interactivesResolved.get());
                                try {
                                    Thread.sleep(1000);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        for (Card c : copyDeckToMakeChanges) {
                            //System.out.println("Counting card: " + c.name);
                            if (client.getHand().contains(c)) {
                                if (c.interactives != null)
                                    for (Interactive in : c.interactives) {

                                        if (in.getPriority() == i) {
                                            client.interactivesResolvedAtomicBoolean = new AtomicBoolean(false);
                                            boolean gotAnswer = in.askPlayer(client);

                                            try {
                                                int k = 0;
                                                while(!client.interactivesResolvedAtomicBoolean.get()){
                                                    Thread.sleep(1000);
                                                    System.out.println("Thread slept " + k + " times.");
                                                    k++;
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                        }


                                    }
                                if (c.bonuses != null)
                                    for (Bonus b : c.bonuses) {
                                        if (b.getPriority() == i) {
                                            int sumForCardBonus = b.count(client.getHand());
                                            sum += sumForCardBonus;
                                            System.out.println("SumForBonusOfCard: " + c.name + " : " + sumForCardBonus);
                                        }
                                    }
                                if (c.maluses != null)
                                    for (Malus m : c.maluses) {
                                        if (m.getPriority() == 6 && i == 6) {
                                            types_maluses.put(c.type, m);
                                        } else if (m.getPriority() == i) {

                                            int sumForCardMalus = m.count(client.getHand());
                                            sum += sumForCardMalus;
                                            System.out.println("SumForMalusOfCard: " + c.name + " : " + sumForCardMalus + "priority is: " + i);
                                        }
                                    }
                            }
                        }
                        if (i == 6) {
                            for (Malus m : Sorts.topologicalSort(client, types_maluses)) {
                                System.out.println("Topologically sorted malus: " + m.getText());
                                sum += m.count(client.getHand());
                            }
                        }
                    }
                    for (Card c : client.getHand()) {
                        sum += c.strength;
                    }
                    System.out.println("The sum of score is: " + sum);

                    return sum;


                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                    System.out.println("All count score");
                }
                return -1;
            }

        }
        );

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(client.futureTask);
        Integer score = 0;
        try {
            score = client.futureTask.get();
            System.out.println("Score got from FutureTask: " + score.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        es.shutdown();

        return score;
    }
}
