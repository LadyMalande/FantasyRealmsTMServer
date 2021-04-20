package interactive;



import artificialintelligence.ScoreCounterForAI;
import artificialintelligence.State;
import maluses.Malus;
import server.BigSwitches;
import server.Card;
import server.ClientHandler;
import server.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
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

    @Override
    public void changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        long startTime = System.nanoTime();
        //System.out.println("Counting TakeCardOfType");

        int bestScore = 0;
        Card thisInteractivesCard = null;
        Card bestCardToTake = null;
        ScoreCounterForAI sc = new ScoreCounterForAI();

        //Copy hand and count current score
        List<Card> newHand = new ArrayList<>();
        for(Card copy : originalHand){
            ArrayList<Malus> maluses = new ArrayList<>();
            ArrayList<Interactive> interactives = new ArrayList<>();
            // bonuses are never deleted, they can be the same objects
            // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
            if(copy.maluses != null){
                //System.out.println("Klonuji kartu " + copy.name);
                for(Malus m : copy.maluses){
                    maluses.add(m.clone());
                }
            }
            // The same rule apply to interactives = they can be deleted to signal it has been used
            if(copy.interactives != null){
                for(Interactive inter : copy.interactives){
                    if (inter instanceof TakeCardOfTypeAtTheEnd) {
                        //newInteractives.add(new TakeCardOfTypeAtTheEnd(c.id, ((TakeCardOfTypeAtTheEnd) in).types));
                    } else {
                        interactives.add(inter.clone());
                    }
                }
            }
            newHand.add(new Card(copy.id,copy.name,copy.strength, copy.type, copy.bonuses, maluses, interactives));
        }


        bestScore = sc.countScore(newHand, cardsOnTable);
        for(Card cardOnTable : cardsOnTable) {
            //the card is of appropriate type, try to take it
            if (types.contains(cardOnTable.type)) {
                // Make new hand and try to count the score
                List<Card> newHand2 = new ArrayList<>();
                for (Card c : originalHand) {
                    ArrayList<Interactive> newInteractives2 = new ArrayList<>();
                    ArrayList<Malus> newMaluses2 = new ArrayList<Malus>();
                    if (c.interactives != null) {
                        for (Interactive in : c.interactives) {
                            if (in instanceof TakeCardOfTypeAtTheEnd) {
                                //newInteractives.add(new TakeCardOfTypeAtTheEnd(c.id, ((TakeCardOfTypeAtTheEnd) in).types));
                            } else {
                                newInteractives2.add(in);
                            }
                        }
                    }
                    if(c.maluses != null){
                        for(Malus m: c.maluses){
                            newMaluses2.add(m.clone());
                        }
                    }
                    newHand2.add(new Card(c.id, c.name, c.strength, c.type, c.bonuses, newMaluses2, newInteractives2));
                }
                ArrayList<Malus> newMalusesForTableCard = new ArrayList<Malus>();
                if(cardOnTable.maluses != null){
                    for(Malus tableMalus : cardOnTable.maluses){
                        newMalusesForTableCard.add(tableMalus.clone());
                    }
                }

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
               //System.out.println("Na teto karte je nyni po rozhodnuti, jakou si vezme kartu, " + oldCards.interactives.size() + " interactives.");
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

            //System.out.println("Out of all cards on table: ");
            for(Card cardOnTable : cardsOnTable){
                //System.out.print(cardOnTable.name + ", ");
            }
            //System.out.print(" the AI chose to take " + bestCardToTake.name);
        }
        long elapsedTime = System.nanoTime() - startTime;
        //System.out.println("Total execution time spent in TakeCardOfType in millis: " + elapsedTime/1000000);
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
