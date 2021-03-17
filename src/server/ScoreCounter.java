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
            while (client.score < 0) {
                // wait;
            }
            client.hostingServer.increaseCountedScoreNumber();

    }



    private int countScore() {

        client.futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() {
                try {
                    ArrayList<Card> whatToRemove = new ArrayList<>();
                    int sum = 0;
                    //System.out.println("Pocet karet v ruce: " + client.getHand().size());
                    HashMap<Type, Malus> types_maluses = new HashMap<>();
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
                                //System.out.println("MaxCount: " + client.interactivesCount + " and we have: " + client.interactivesResolved.get());
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
                                if(!whatToRemove.contains(c))
                                if (c.interactives != null)
                                    for (Interactive in : c.interactives) {

                                        if (in.getPriority() == i) {
                                            client.interactivesResolvedAtomicBoolean = new AtomicBoolean(false);
                                            boolean gotAnswer = in.askPlayer(client);

                                            try {
                                                int k = 0;
                                                while(!client.interactivesResolvedAtomicBoolean.get()){
                                                    Thread.sleep(1000);
                                                    //System.out.println("Thread slept " + k + " times.");
                                                    k++;
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        if(i == 2){
                                            whatToRemove.clear();
                                        }
                                    }
                                if (c.bonuses != null)
                                    for (Bonus b : c.bonuses) {
                                        if (b.getPriority() == 5 && i == 2) {
                                            int sumForCardBonus = b.count(client.getHand());
                                            sum += sumForCardBonus;
                                            //System.out.println("SumForBonusOfCard: " + c.name + " : " + sumForCardBonus);
                                        }
                                        if (b.getPriority() == 5 && i == 0) {
                                            int sumForCardBonus = b.count(client.getHand());
                                            sum += sumForCardBonus;
                                            //System.out.println("SumForBonusOfCard: " + c.name + " : " + sumForCardBonus);
                                        }
                                        if (b.getPriority() == i) {
                                            int sumForCardBonus = b.count(client.getHand());
                                            sum += sumForCardBonus;
                                            //System.out.println("SumForBonusOfCard: " + c.name + " : " + sumForCardBonus);
                                        }
                                    }
                                if (c.maluses != null)
                                    for (Malus m : c.maluses) {
                                        if (m.getPriority() == 6 && i == 1) {

                                            sum += m.count(client.getHand(), whatToRemove);
                                            //types_maluses.put(c.type, m);
                                            //System.out.println("Malus added for topological sorting: " + m.getText());
                                        } else if(m.getPriority() == 6 && i == 6){
                                            sum += m.count(client.getHand(), whatToRemove);
                                        }
                                        else if(m.getPriority() == 6 && i == 2){
                                            sum += m.count(client.getHand(), whatToRemove);
                                        }
                                        else if (m.getPriority() == i) {

                                            int sumForCardMalus = m.count(client.getHand());
                                            sum += sumForCardMalus;
                                            //System.out.println("SumForMalusOfCard: " + c.name + " : " + sumForCardMalus + " priority is: " + i);
                                        }
                                    }
                            }
                        }

                        if (i == 1 || i == 2) {
                            for(Card c:whatToRemove){
                                if(c.interactives != null && !c.interactives.isEmpty()){
                                    client.interactivesCount--;
                                }
                            }

                        }
                        if(i == 6){
                            client.getHand().removeIf(whatToRemove::contains);
                        }
/*
                            for (Malus m : Sorts.topologicalSort(client, types_maluses)) {

                                System.out.println("Topologically sorted malus: " + m.getText());
                                sum += m.count(client.getHand());
                            }


                        }

                         */
                    }
                    client.scoreTable = new StringBuilder();
                    for (Card c : client.getHand()) {
                        sum += c.strength;
                        int tabCount = 3 - (c.name.length() / 8);
                        client.scoreTable.append(c.name + " contributed with " + c.strength + " basic strength. ");
                        int bonus = 0;
                        if(c.bonuses != null && !c.bonuses.isEmpty()){
                            for(Bonus b: c.bonuses){
                                bonus += b.count(client.getHand());
                            }
                            if(bonus != 0) {
                                for(int i=0; i< tabCount; i++) {
                                    client.scoreTable.append("\t");
                                }
                                client.scoreTable.append("BONUS: +" + bonus);
                            }
                        }
                        int malus = 0;
                        if(c.maluses != null && !c.maluses.isEmpty()){
                            for(Malus m: c.maluses){
                                malus += m.count(client.getHand());
                            }
                            if(malus != 0) {
                                if(bonus != 0) {
                                    client.scoreTable.append("\tMALUS: " + malus);
                                } else{
                                    for(int i=0; i< tabCount; i++) {
                                        client.scoreTable.append("\t");
                                    }
                                    client.scoreTable.append("MALUS: " + malus);
                                }
                            }
                        }
                        client.scoreTable.append("\n");
                        //System.out.println("Strength of card " + c.name + " is " + c.strength);
                    }
                    for(Card c: whatToRemove){
                        client.scoreTable.append("Card: " + c.name + " was BLANKED. \n");
                    }
                    //System.out.println("The sum of score is: " + sum);

                    return sum;


                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                    //System.out.println("All count score");
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
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        es.shutdown();

        return score;
    }
}
