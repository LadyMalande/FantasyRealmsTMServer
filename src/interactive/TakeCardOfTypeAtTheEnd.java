package interactive;


import artificialintelligence.ScoreCounterForAI;
import artificialintelligence.State;
import server.BigSwitches;
import server.Card;
import server.ClientHandler;
import server.Type;
import util.HandCloner;
import util.Pair;

import java.util.*;

public class TakeCardOfTypeAtTheEnd extends Interactive  {
    public int priority = 0;
    public String text;
    public ArrayList<Type> types;
    private int thiscardid;

    public TakeCardOfTypeAtTheEnd(int id,ArrayList<Type> types) {
        this.thiscardid = id;
        this.text = "At the end of the game, you can take one card from the table which is of type " + giveListOfTypesWithSeparator(types, " or ") + " as your eighth card";
        this.types = types;
        //System.out.println("Card INIT: Text: " + getText());
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }

    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("interactive.CardInteractives",loc);
        ResourceBundle typ = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(rb.getString("takeCardOfTypeAtTheEnd"));
        sb.append(typ.getString("any4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types, "or",locale,4));
        sb.append(rb.getString("takeCardOfTypeAtTheEnd2"));
        return sb.toString();
    }

    @Override
    public int getPriority(){ return this.priority; }

    private String getNamesOfTypeCardsOnTable(ClientHandler client){
        StringBuilder str = new StringBuilder();

        for(Type t: types){
            for(Card c: client.hostingServer.cardsOnTable){
                if(c.type.equals(t)){
                    str.append(c.getNameLoc(client.locale.getLanguage())).append(",");
                }
            }
        }

        return str.toString();
    }

    @Override
    public boolean askPlayer(ClientHandler client) {

        return client.sendInteractive("TakeCardOfType#" + thiscardid + "#" + getNamesOfTypeCardsOnTable(client));
    }

    public Pair<Card, Integer> giveCardToTakeFromTable(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable){
        int bestScore = 0;
        Card thisInteractivesCard = null;
        Card bestCardToTake = null;
        ScoreCounterForAI sc = new ScoreCounterForAI();
        // Remove this interactive from card to not get stuck in loop
        for (Card original : originalHand) {
            if (thiscardid == original.id) {
                original.interactives.remove(this);
            }
        }
        HandCloner hc = new HandCloner();
        //Copy hand and count current score
        List<Card> newHand = null;
        try {
            newHand = hc.cloneHand(null, originalHand);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }


        bestScore = sc.countScore(newHand, cardsOnTable, true);
        int scoreBefore = bestScore;
        ArrayList<Card> newCardsOnTableToIterate = null;
        try {
            newCardsOnTableToIterate = hc.cloneHand(null, cardsOnTable);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        for(Card cardOnTable : newCardsOnTableToIterate) {
            //the card is of appropriate type, try to take it
            if (types.contains(cardOnTable.type)) {
                //System.out.println("Card was considered by Necro");
                // Make new hand and try to count the score

                try {
                    ArrayList<Card> newHand2 = hc.cloneHand(null, originalHand);
                    ArrayList<Card> newCardsOnTable = hc.cloneHand(null, cardsOnTable);
                    newCardsOnTable.remove(cardOnTable);
                    newHand2.add(cardOnTable);
                    //System.out.println("////Pocitam skore pro novou ruku s 8. kartou z nekromancerskeho interactive: ");
                    //super.writeAllCardsAndTheirAttributes(newHand2);
                    int currentScore = sc.countScore(newHand2, newCardsOnTable, true);
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestCardToTake = cardOnTable;
                    }


                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }



            }
        }
        /*
        if(bestCardToTake != null){
            System.out.println("Necromancer chose to take " + bestCardToTake.name + " from table.");

        } else{
            System.out.println("Necromancers chosen card is null, before=" + scoreBefore + " after=" + bestScore);
        }

         */
        return new Pair<Card, Integer>(bestCardToTake, bestScore);
    }

    @Override
    public int changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        long startTime = System.nanoTime();
        Pair<Card,Integer> bestToTake =  giveCardToTakeFromTable(originalHand, cardsOnTable);
        ScoreCounterForAI sc = new ScoreCounterForAI();
        //System.out.println("Counting TakeCardOfType");
        Card bestCardToTake = bestToTake.getKey();
        int bestScore = bestToTake.getValue();
        // if there is at least one suitable card, take it. If the card gives negative points, it should not be taken.
        if(bestCardToTake != null){
            originalHand.add(bestCardToTake);
            cardsOnTable.remove(bestCardToTake);
            if(bestCardToTake.interactives != null){
                for(Interactive innew : bestCardToTake.interactives){
                    // If we drew another card with Necromancer like bonus, resolve it immediately
                    if(innew instanceof TakeCardOfTypeAtTheEnd){
                        innew.changeHandWithInteractive(originalHand, cardsOnTable);
                    }
                }
            }
            bestScore = sc.countScore(originalHand, cardsOnTable,false);
            //System.out.println("Out of all cards on table: ");
            for(Card cardOnTable : cardsOnTable){
                //System.out.print(cardOnTable.name + ", ");
            }
            //System.out.print(" the AI chose to take " + bestCardToTake.name);
        }
        long elapsedTime = System.nanoTime() - startTime;
        //System.out.println("Total execution time spent in TakeCardOfType in millis: " + elapsedTime/1000000);

        return bestScore;
    }

    @Override
    public Interactive clone() throws CloneNotSupportedException{
        TakeCardOfTypeAtTheEnd newi = (TakeCardOfTypeAtTheEnd)super.clone();
        newi.priority = this.priority;
        newi.text = this.text;
        newi.types = (ArrayList<Type>) this.types.clone();
        newi.thiscardid = this.thiscardid;
        return newi;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        double potential = 0.0;
        // TODO
        return potential;
    }
}
