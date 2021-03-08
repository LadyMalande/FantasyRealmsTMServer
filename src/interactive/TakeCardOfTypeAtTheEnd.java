package interactive;



import artificialintelligence.ScoreCounterForAI;
import maluses.Malus;
import server.Card;
import server.ClientHandler;
import server.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    }

    @Override
    public String getText(){
        return this.text;
    }
    @Override
    public int getPriority(){ return this.priority; }

    private String getNamesOfTypeCardsOnTable(ClientHandler client){
        StringBuilder str = new StringBuilder();

        for(Type t: types){
            for(Card c: client.hostingServer.cardsOnTable){
                if(c.type.equals(t)){
                    str.append(c.name).append(",");
                }
            }
        }

        return str.toString();
    }

    @Override
    public boolean askPlayer(ClientHandler client) {

        return client.sendInteractive("TakeCardOfType#" + thiscardid + "#" + getNamesOfTypeCardsOnTable(client));
    }

    @Override
    public void changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        int bestScore = 0;
        Card thisInteractivesCard = null;
        Card bestCardToTake = null;
        for(Card cardOnTable : cardsOnTable) {
            //the card is of appropriate type, try to take it
            if (types.contains(cardOnTable.type)) {
                // Make new hand and try to count the score
                List<Card> newHand1 = new ArrayList<>();
                List<Card> newHand2 = new ArrayList<>();
                for (Card c : originalHand) {
                    ArrayList<Interactive> newInteractives1 = new ArrayList<>();
                    ArrayList<Malus> newMaluses1 = new ArrayList<Malus>();
                    ArrayList<Interactive> newInteractives2 = new ArrayList<>();
                    ArrayList<Malus> newMaluses2 = new ArrayList<Malus>();
                    if (c.interactives != null) {
                        for (Interactive in : c.interactives) {
                            if (in instanceof TakeCardOfTypeAtTheEnd) {
                                //newInteractives.add(new TakeCardOfTypeAtTheEnd(c.id, ((TakeCardOfTypeAtTheEnd) in).types));
                            } else {
                                newInteractives1.add(in);
                                newInteractives2.add(in);
                            }
                        }
                    }
                    if(c.maluses != null){
                        for(Malus m: c.maluses){
                            newMaluses1.add(m.clone());
                            newMaluses2.add(m.clone());
                        }
                    }
                    newHand1.add(new Card(c.id, c.name, c.strength, c.type, c.bonuses, newMaluses1, newInteractives2));
                    newHand2.add(new Card(c.id, c.name, c.strength, c.type, c.bonuses, newMaluses1, newInteractives2));
                }
                ArrayList<Malus> newMalusesForTableCard = new ArrayList<Malus>();
                if(cardOnTable.maluses != null){
                    for(Malus tableMalus : cardOnTable.maluses){
                        newMalusesForTableCard.add(tableMalus.clone());
                    }
                }
                ScoreCounterForAI sc = new ScoreCounterForAI();
                bestScore = sc.countScore(newHand1,cardsOnTable);
                    newHand2.add(new Card(cardOnTable.id, cardOnTable.name, cardOnTable.strength, cardOnTable.type, cardOnTable.bonuses, newMalusesForTableCard, cardOnTable.interactives));
                    ArrayList<Card> newCardsOnTable  = new ArrayList<>();
                    for(Card cardOnOldTable : cardsOnTable){
                        if(cardOnOldTable != cardOnTable) {
                            newCardsOnTable.add(cardOnOldTable);
                        }
                    }

                    //System.out.println("////Pocitam skore pro novou ruku s 8. kartou z nekromancerskeho interactive: ");
                    //super.writeAllCardsAndTheirAttributes(newHand2);
                    int currentScore = sc.countScore(newHand2, newCardsOnTable);
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestCardToTake = cardOnTable;
                    }
            }
        }
        // if there is at least one suitable card, take it. If the card gives negative points, it should not be taken.
        for(Card oldCards : originalHand){
            if(oldCards.id == thiscardid){
                // remove this interactive from this card to not get stuck in loop
                //oldCards.interactives.removeIf(x -> x == this);
                System.out.println("Na teto karte je nyni po rozhodnuti, jakou si vezme kartu, " + oldCards.interactives.size() + " interactives.");
            }
        }


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

            System.out.println("Out of all cards on table: ");
            for(Card cardOnTable : cardsOnTable){
                System.out.print(cardOnTable.name + ", ");
            }
            System.out.print(" the AI chose to take " + bestCardToTake.name);
        }

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
}
