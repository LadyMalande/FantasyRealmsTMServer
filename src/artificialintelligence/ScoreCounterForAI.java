package artificialintelligence;

import bonuses.Bonus;
import interactive.Interactive;
import interactive.TakeCardOfTypeAtTheEnd;
import maluses.Malus;
import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class ScoreCounterForAI {
    private  FutureTask<Integer> futureTask;
    public static int countBestPossibleScore(ArrayList<Card> handOriginal, ArrayList<Card> cardsOnTable){
        try {
            ArrayList<Card> whatToRemove = new ArrayList<>();
            int sum = 0;
            HashMap<Type, Malus> types_maluses = new HashMap<>();
            // Alter hand by adding a new card with Necromancer before resolving the rest of the cards in loop
            checkIfThereIsNecromancerLikeCard(handOriginal, cardsOnTable);
            ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(handOriginal);
            ArrayList<Card> resolveOnceAgain = new ArrayList<>();
            int MAX_PRIORITY = 8;
            int MIN_PRIORITY = 0;

            for (int i = MIN_PRIORITY; i <= MAX_PRIORITY; i++) {

                if(i == 6){
                    whatToRemove.clear();
                }
                for (Card c : copyDeckToMakeChanges) {
                    if (handOriginal.contains(c)) {

                        if (!whatToRemove.contains(c)) {

                            if (c.interactives != null) {
                                ArrayList<Interactive> interactivesToMakeChanges = new ArrayList<>(c.interactives);
                                for (Interactive in : interactivesToMakeChanges) {

                                    if (in.getPriority() == i) {
                                        if (!(in instanceof TakeCardOfTypeAtTheEnd)) {
                                            //somehow use the interactives
                                            in.changeHandWithInteractive(handOriginal, cardsOnTable);

                                        }
                                    }

                                    if (i == 2) {
                                        whatToRemove.clear();
                                    }
                                }
                            }
                            if (c.bonuses != null)
                                for (Bonus b : c.bonuses) {
                                    if (b.getPriority() == 5 && i == 2) {
                                        int sumForCardBonus = b.count(handOriginal);
                                        sum += sumForCardBonus;
                                       // System.out.println("SumForBonusOfCard: " + c.name + " : " + sumForCardBonus);
                                    }
                                    if (b.getPriority() == 5 && i == 0) {
                                        int sumForCardBonus = b.count(handOriginal);
                                        sum += sumForCardBonus;
                                       // System.out.println("SumForBonusOfCard: " + c.name + " : " + sumForCardBonus);
                                    }
                                    if (b.getPriority() == i) {
                                        int sumForCardBonus = b.count(handOriginal);
                                        sum += sumForCardBonus;
                                        //System.out.println("SumForBonusOfCard: " + c.name + " : " + sumForCardBonus);
                                    }
                                }
                            if (c.maluses != null) {
                                //System.out.println("Maluses are not null, lets resolve them");
                                for (Malus m : c.maluses) {
                                    if (m.getPriority() == 6 && i == 1) {
                                        //System.out.println("Now cards in hands after resolving malus with priority 6 and i=" + i + " is " + handOriginal.size());
                                        sum += m.count(handOriginal, whatToRemove);
                                    } else if (m.getPriority() == 6 && i == 6) {
                                        //System.out.println("Now cards in hands after resolving malus with priority 6 and i=" + i + " is " + handOriginal.size());

                                        sum += m.count(handOriginal, whatToRemove);
                                    } else if (m.getPriority() == 6 && i == 2) {
                                        //System.out.println("Now cards in hands after resolving malus with priority 6 and i=" + i + " is " + handOriginal.size());

                                        sum += m.count(handOriginal, whatToRemove);
                                    } else if (m.getPriority() == 7) {
                                        //System.out.println("Now cards in hands after resolving malus with priority 6 and i=" + i + " is " + handOriginal.size());

                                        sum += m.count(handOriginal, whatToRemove);
                                    }
                                    else if (m.getPriority() == i) {

                                        int sumForCardMalus = m.count(handOriginal);
                                        sum += sumForCardMalus;
                                        //System.out.println("SumForMalusOfCard: " + c.name + " : " + sumForCardMalus + " priority is: " + i);
                                       // System.out.println("Now cards in hands after resolving malus with priority " + i + " is " + handOriginal.size());
                                    }
                                }
                            }
                        }
                        }


                if(i == 6){
                    for(Card cardToRemove : whatToRemove){
                        //System.out.println("THis card is in whatToRemove and will be BLANKED: " + cardToRemove.name);
                    }
                    handOriginal.removeIf(whatToRemove::contains);
                    //System.out.println("Hand after removing whatToRemove has " + handOriginal.size() + " cards");
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
                sum += c.strength;
            }

            // Briefing of score
            StringBuilder scoreTable = new StringBuilder();
            Collections.sort(handOriginal);
            ArrayList<Card> cdeckToIterateThrough = new ArrayList<>(handOriginal);
            for (Card c : cdeckToIterateThrough) {
                //sum += c.strength;
                int tabCount = 3 - (c.name.length() / 8);
                scoreTable.append(server.BigSwitches.switchIdForName(c.id, "en") + "->" + c.name + "[" + c.type + "] contributed with " + c.strength + " basic strength. ");
                int bonus = 0;
                if(c.bonuses != null && !c.bonuses.isEmpty()){
                    for(Bonus b: c.bonuses){
                        bonus += b.count(handOriginal);
                    }
                    if(bonus != 0) {
                        for(int i=0; i< tabCount; i++) {
                            scoreTable.append("\t");
                        }
                        scoreTable.append("BONUS: +" + bonus);
                    }
                    scoreTable.append(" Pocet bonusu: " + c.bonuses.size());

                }
                int malus = 0;
                if(c.maluses != null){
                    for(Malus m: c.maluses){
                        if(m.priority == 7){
                            malus += 0;
                        } else{
                            malus += m.count(handOriginal);
                        }

                    }
                    if(malus != 0) {
                        if(bonus != 0) {
                            scoreTable.append("\tMALUS: " + malus);
                        } else{
                            for(int i=0; i< tabCount; i++) {
                                scoreTable.append("\t");
                            }
                            scoreTable.append("MALUS: " + malus);
                        }
                    }
                    scoreTable.append(" Pocet postihu: " + c.maluses.size());

                }
                scoreTable.append("\n");
                //System.out.println("Strength of card " + c.name + " is " + c.strength);
            }
            for(Card c: whatToRemove){
                scoreTable.append("Card: " + c.name + " was BLANKED. \n");
            }
            //System.out.println("   The sum of score is: " + sum);
            //System.out.println(scoreTable);
            return sum;

        } catch (NullPointerException | CloneNotSupportedException ex) {
            ex.printStackTrace();
            System.out.println("All count score");
        }
        return -1;
    }


    public int countScore(List<Card> handOriginal, List<Card> cardsOnTable){
        futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() {
                try {
                    return countBestPossibleScore(new ArrayList<>(handOriginal), new ArrayList<>(cardsOnTable));
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                    System.out.println("Nullpointer Exception while counting Score for AI");
                }
                return -1;
            }});
                ExecutorService es = Executors.newSingleThreadExecutor();
                es.execute(this.futureTask);
                Integer score = -1000;
                try {
                score = this.futureTask.get();
                //System.out.print("Score got from FutureTask for hand ^^^^: " + score.toString());
                for(Card c: handOriginal){
                    //System.out.print(", " + c.name );
                }
                //System.out.println();
                } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                }

                es.shutdown();

                return score;

    }

    private static void checkIfThereIsNecromancerLikeCard(ArrayList<Card> handOriginal, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        //System.out.println("Inside checkIfThereIsNecromancerLikeCard");
        ArrayList<Card> copyDeckToMakeChanges = new ArrayList<>(handOriginal);

        for(Card c : copyDeckToMakeChanges){
            Interactive interactiveToDelete = null;
            if(c.interactives != null){
                //System.out.println("Interactives are not null, size is " + c.interactives.size());
                for(Interactive in : c.interactives){
                    //System.out.println(in.text);
                    if(in instanceof TakeCardOfTypeAtTheEnd){
                        //System.out.println("There is a Necromancer like card.");
                        in.changeHandWithInteractive(handOriginal, cardsOnTable);
                        interactiveToDelete = in;
                    }
                }
            }
            // Delete this interactive to not get stuck in endless loop
            if(interactiveToDelete != null)
            c.interactives.remove(interactiveToDelete);
        }

    }
}
