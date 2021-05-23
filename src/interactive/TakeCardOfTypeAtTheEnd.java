package interactive;


import artificialintelligence.ScoreCounterForAI;
import artificialintelligence.State;
import server.Card;
import server.ClientHandler;
import server.Server;
import server.Type;
import util.BigSwitches;
import util.HandCloner;
import util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The interactive bonus that implements the possibility of taking a card of given type from the table
 * at the end of the game.
 * @author Tereza Miklóšová
 */
public class TakeCardOfTypeAtTheEnd extends Interactive  {
    /**
     * This card is resolved first.
     */
    public int priority = 0;
    /**
     * Types of cards that can be taken from the table.
     */
    public ArrayList<Type> types;
    /**
     * Id of the card containing this bonus.
     */
    private int thiscardid;

    /**
     * Constructor for this interactive bonus.
     * @param id Id of the card containing this bonus.
     * @param types Types of cards that can be taken from the table.
     */
    public TakeCardOfTypeAtTheEnd(int id,ArrayList<Type> types) {
        this.thiscardid = id;
        this.types = types;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("interactive.CardInteractives",loc);
        ResourceBundle typ = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(rb.getString("takeCardOfTypeAtTheEnd")).append(" ");
        sb.append(typ.getString("any4" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types, locale,4));
        sb.append(rb.getString("takeCardOfTypeAtTheEnd2"));
        return sb.toString();
    }

    @Override
    public int getPriority(){ return this.priority; }

    private String getNamesOfTypeCardsOnTable(ClientHandler client){
        StringBuilder str = new StringBuilder();

        for(Type t: types){
            for(Card c: client.hostingServer.cardsOnTable){
                if(c.getType().equals(t)){
                    str.append(c.getNameLoc(client.locale.getLanguage())).append(",");
                }
            }
        }
        return str.toString();
    }

    @Override
    public void askPlayer(ClientHandler client) {

        client.sendMessage("TakeCardOfType#" + thiscardid + "#" + getNamesOfTypeCardsOnTable(client));
    }

    /**
     * Computes which card is the most suitable to get from the table.
     * @param originalHand Cards in hand.
     * @param cardsOnTable Cards on table to choose from.
     * @return Pair of the best card to take and the score it gives.
     */
    public Pair<Card, Integer> giveCardToTakeFromTable(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable, Server server){
        int bestScore;
        Card bestCardToTake = null;
        ScoreCounterForAI sc = new ScoreCounterForAI(server);
        // Remove this interactive from card to not get stuck in loop
        for (Card original : originalHand) {
            if (thiscardid == original.getId()) {
                original.getInteractives().remove(this);
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
        // Count the best beginning score without any other card from the table
        bestScore = sc.countScore(newHand, cardsOnTable, true);

        ArrayList<Card> newCardsOnTableToIterate = null;
        try {
            newCardsOnTableToIterate = hc.cloneHand(null, cardsOnTable);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        // If there are any cards on the table, try to take any of suitable type and see if the score is greater.
        if(newCardsOnTableToIterate != null) {
            for (Card cardOnTable : newCardsOnTableToIterate) {
                //the card is of appropriate type, try to take it
                if (types.contains(cardOnTable.getType())) {
                    // Make new hand and try to count the score
                    try {
                        ArrayList<Card> newHand2 = hc.cloneHand(null, originalHand);
                        ArrayList<Card> newCardsOnTable = hc.cloneHand(null, cardsOnTable);
                        newCardsOnTable.remove(cardOnTable);
                        newHand2.add(cardOnTable);
                        int currentScore = sc.countScore(newHand2, newCardsOnTable, true);
                        // The score is greater than without the card, so its good to take this one.
                        if (currentScore > bestScore) {
                            bestScore = currentScore;
                            bestCardToTake = cardOnTable;
                        }


                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new Pair<>(bestCardToTake, bestScore);
    }

    @Override
    public int changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable, Server server,
                                         Integer idOfCardToChange, Card toChangeInto) throws CloneNotSupportedException {
        Pair<Card,Integer> bestToTake =  giveCardToTakeFromTable(originalHand, cardsOnTable, server);
        ScoreCounterForAI sc = new ScoreCounterForAI(server);
        //System.out.println("Counting TakeCardOfType");
        Card bestCardToTake = bestToTake.getKey();
        int bestScore = bestToTake.getValue();
        // if there is at least one suitable card, take it. If the card gives negative points, it should not be taken.
        if(bestCardToTake != null){
            originalHand.add(bestCardToTake);
            cardsOnTable.remove(bestCardToTake);
            if(bestCardToTake.getInteractives() != null){
                for(Interactive innew : bestCardToTake.getInteractives()){
                    // If we drew another card with Necromancer like bonus, resolve it immediately
                    if(innew instanceof TakeCardOfTypeAtTheEnd){
                        innew.changeHandWithInteractive(originalHand, cardsOnTable, server,
                                idOfCardToChange, toChangeInto);
                    }
                }
            }
            bestScore = sc.countScore(originalHand, cardsOnTable,false);
        }
        return bestScore;
    }

    @Override
    public Interactive clone() throws CloneNotSupportedException{
        TakeCardOfTypeAtTheEnd newi = (TakeCardOfTypeAtTheEnd)super.clone();
        newi.priority = this.priority;
        newi.types = new ArrayList<>(this.types);
        newi.thiscardid = this.thiscardid;
        return newi;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }
}
