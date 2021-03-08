package interactive;

import artificialintelligence.ScoreCounterForAI;
import bonuses.Bonus;
import maluses.Malus;
import server.Card;
import server.ClientHandler;
import server.Type;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class DeleteOneMalusOnType extends Interactive {
    public int priority = 2;
    public String text;
    public ArrayList<Type> types;
    private int thiscardid;

    public DeleteOneMalusOnType(int id, ArrayList<Type> types) {
        this.text = "Delete one malus on type " + giveListOfTypesWithSeparator(types, " or ");
        this.types = types;
        this.thiscardid = id;
        //System.out.println("Card INIT: Text: " + getText());
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    private String getAllMalusesForTypesToString(ClientHandler client){
        StringBuilder str = new StringBuilder();
        boolean firstTime = true;
        for(Type t: types){
            for(Card c: client.getHand()){
                if(c.type.equals(t)){
                    if(c.maluses != null && !c.maluses.isEmpty()){
                        if(!firstTime){
                            str.append("%");
                        }

                        boolean firstTimeInMaluses = true;
                        for(Malus m: c.maluses){
                            if(!firstTimeInMaluses){
                                str.append("%");
                            }
                            str.append(c.name + ": ");
                            str.append(m.getText());
                            firstTimeInMaluses = false;
                        }
                        firstTime = false;
                    }
                }
            }
        }
        return str.toString();
    }

    @Override
    public boolean askPlayer(ClientHandler client) {
        //            ch.getHand().stream().filter(card -> card.name.equals(result1)).findAny().ifPresent(removeMalusHere -> removeMalusHere.maluses.removeIf(malus -> malus.getText().equals(result2)));
       String s = getAllMalusesForTypesToString(client);
        if(s.isEmpty()){
            synchronized(client.interactivesResolvedAtomicBoolean) {
                client.interactivesResolved.incrementAndGet();
                //client.futureTask.notify();
                client.interactivesResolvedAtomicBoolean.set(true);
                return true;
            }
        }
        else{
            return client.sendInteractive("DeleteOneMalusOnType#" + thiscardid + "#" + getAllMalusesForTypesToString(client));

        }
    }

    @Override
    public void changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        ArrayList<Integer> malusesScore = new ArrayList<>();
        Card onWhichDeleteMalus = null;
        Malus whichMalusToDelete = null;

        // Remove this interactive from card to not get stuck in loop
        for(Card original : originalHand){
            if(thiscardid == original.id){
                original.interactives.remove(this);
            }
        }

        //Copy hand for computing the original score on Hand
        List<Card> newHandOldScore = new ArrayList<>();
        for(Card copy : originalHand){
            ArrayList<Malus> maluses = new ArrayList<>();
            ArrayList<Interactive> interactives = new ArrayList<>();
            // bonuses are never deleted, they can be the same objects
            // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
            if(copy.maluses != null){
                System.out.println("Klonuji kartu " + copy.name);
                for(Malus m : copy.maluses){
                    maluses.add(m.clone());
                }
            }
            // The same rule apply to interactives = they can be deleted to signal it has been used
            if(copy.interactives != null){
                for(Interactive inter : copy.interactives){
                    interactives.add(inter.clone());
                }
            }
            newHandOldScore.add(new Card(copy.id,copy.name,copy.strength, copy.type, copy.bonuses, maluses, interactives));
        }

        ScoreCounterForAI sc = new ScoreCounterForAI();
        int bestScore = sc.countScore(newHandOldScore, cardsOnTable);

        for(Card c : originalHand){
            if(this.types.contains(c.type)) {
                if (c.maluses != null) {
                    for (int i = 0; i < c.maluses.size(); i++){
                        Malus storeMalus = c.maluses.get(i);
                        c.maluses.remove(i);


                        List<Card> newHand = new ArrayList<>();
                        for(Card copy : originalHand){
                            ArrayList<Malus> maluses = new ArrayList<>();
                            ArrayList<Interactive> interactives = new ArrayList<>();
                            // bonuses are never deleted, they can be the same objects
                            // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
                            if(copy.maluses != null){
                                System.out.println("Klonuji kartu " + copy.name);
                                for(Malus m : copy.maluses){
                                    maluses.add(m.clone());
                                }
                            }
                            // The same rule apply to interactives = they can be deleted to signal it has been used
                            if(copy.interactives != null){
                                for(Interactive inter : copy.interactives){
                                    interactives.add(inter.clone());
                                }
                            }
                            newHand.add(new Card(copy.id,copy.name,copy.strength, copy.type, copy.bonuses, maluses, interactives));
                        }


                        int currentScore = sc.countScore(newHand, cardsOnTable);
                        if (currentScore > bestScore) {
                            bestScore = currentScore;
                            whichMalusToDelete = storeMalus;
                            onWhichDeleteMalus = c;
                            System.out.println("Without Malus on card " + c.name + ": " + storeMalus.text + " the score is " + currentScore);
                            System.out.println("Which is worse than the previous score: best score so far = " + bestScore);
                        }
                        c.maluses.add(i, storeMalus);
                    }
                }
            }
        }
        if(onWhichDeleteMalus != null && whichMalusToDelete != null){
            onWhichDeleteMalus.maluses.remove(whichMalusToDelete);
            System.out.println("Finally deleting Malus on card " + onWhichDeleteMalus.name + ": " + whichMalusToDelete.text);
        }

    }

    @Override
    public Interactive clone() throws CloneNotSupportedException{
        DeleteOneMalusOnType newi = (DeleteOneMalusOnType)super.clone();
        newi.priority = this.priority;
        newi.text = this.text;
        newi.types = (ArrayList<Type>) this.types.clone();
        newi.thiscardid = this.thiscardid;
        return newi;
    }
}
