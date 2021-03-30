package artificialintelligence;

import bonuses.Bonus;
import interactive.Interactive;
import maluses.Malus;
import server.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class GreedyPlayer extends PlayerOrAI implements ArtificialIntelligenceInterface{
    ArrayList<Card> hand;
    Card bestCardToTake;
    Card bestCardToDrop;
    Server server;
    int bestPossibleScore;
    int gainThreshold;
    int rank;
    String name;
    private FutureTask<Integer> futureTask;
    private int numberOfRoundsPlayed;
    String beginningHandCards;
    int beginningHandScore;
    boolean playing = false;

    public GreedyPlayer(Server server) throws CloneNotSupportedException {
        hand = new ArrayList<>();
        this.server = server;
        bestPossibleScore = 0;
        bestCardToTake = null;
        bestCardToDrop = null;
        gainThreshold = 0;
        getInitCards();
    }

    public GreedyPlayer(Server server, int gainThreshold, String name) throws CloneNotSupportedException {
        this.name = name;
        System.out.println("Name of GreedyPlayer " + this.name);
        hand = new ArrayList<>();
        this.server = server;
        bestPossibleScore = 0;
        bestCardToTake = null;
        bestCardToDrop = null;
        numberOfRoundsPlayed = 0;
        this.gainThreshold = gainThreshold;
        getInitCards();
        //System.out.println("Making Greedy player with gain threshold: " + gainThreshold);
    }

    @Override
    public ArrayList<Card> getHand(){
        return hand;
    }

    @Override
    public String getName(){
        return this.name;
    }


    @Override
    public void setPlaying(boolean playing){this.playing = playing;}

    @Override
    public boolean getPlaying(){
        return this.playing;
    }

    @Override
    public int getNumberOfRoundsPlayed(){
        return numberOfRoundsPlayed;
    }

    @Override
    public String getBeginningHandCards(){
        return beginningHandCards;
    }

    @Override
    public int getBeginningHandScore(){
        return beginningHandScore;
    }

    @Override
    public int getRank(){return this.rank;}
    @Override
    public int getScore(){return this.score;}
    @Override
    public void setRank(int r){this.rank = r;}
    @Override
    public void setScore(int s){this.score = s;}

    @Override
    public void getInitCards() throws CloneNotSupportedException {
        hand.clear();
        numberOfRoundsPlayed = 0;
        Random randomGenerator = new Random();
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < 7; j++) {
            //System.out.println("In getInitCards in GreedyPlayer constructor, number of loop is " + j);
            int index = randomGenerator.nextInt(server.deck.getDeck().size());
            Card newCard = server.deck.getDeck().get(index);
            hand.add(newCard);
            sb.append(newCard.getName()  + ";");
            server.deck.getDeck().remove(index);
        }
        beginningHandCards = sb.toString();
        List<Card> newHandOldScore = new ArrayList<>();
        for(Card copy : hand){
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
                    interactives.add(inter.clone());
                }
            }
            newHandOldScore.add(new Card(copy.id,copy.name,copy.strength, copy.type, copy.bonuses, maluses, interactives));
        }

        // Count if there is gain from the table greater than the gainThreshold and keep the highest combination of them
        ScoreCounterForAI sc = new ScoreCounterForAI();
        beginningHandScore = sc.countScore(newHandOldScore, new ArrayList<>());
    }

    public void performMove(ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {
        //We cant continue playing when 10 cards are on table
        if(cardsOnTable.size() == 10){
            return;

        }
        numberOfRoundsPlayed++;
        // resetting choice
        bestCardToTake = null;
        bestCardToDrop = null;
        List<Card> newTable = new ArrayList<>();


        // Copy the original hand to not get some maluses or interactives deleted while computing the original score
        List<Card> newHandOldScore = new ArrayList<>();
        for(Card copy : hand){
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
                    interactives.add(inter.clone());
                }
            }
            newHandOldScore.add(new Card(copy.id,copy.name,copy.strength, copy.type, copy.bonuses, maluses, interactives));
        }

        // Count if there is gain from the table greater than the gainThreshold and keep the highest combination of them
        ScoreCounterForAI sc = new ScoreCounterForAI();
        int currentScore = sc.countScore(newHandOldScore, cardsOnTable);
        int currentMaxScore = currentScore;
        //System.out.print("Players cards on hand before deciding what to do [score: " + currentMaxScore + "]: ");
        for(Card c: hand){
            //System.out.print(c.name + ", ");
        }
        //System.out.println();

        //System.out.println("------------------------------NOVE ZKOUSENI NEJLEPSI RUKY-----------------------------");

        // Try to change all combination of cards in hand and place one card from table instead
        for(Card cardToChange : hand){
            // Copy all cards except the one that should be changed
            // List<Card> newHand = new ArrayList<>(hand.stream().filter(card -> !card.equals(cardToChange)).collect(Collectors.toList()));
            List<Card> newHand = new ArrayList<>();
            for(Card c : hand){
                if(c != cardToChange){
                    ArrayList<Malus> maluses = new ArrayList<>();
                    ArrayList<Interactive> interactives = new ArrayList<>();
                    // bonuses are never deleted, they can be the same objects
                    // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
                    if(c.maluses != null){
                        //System.out.println("Klonuji kartu " + c.name);
                        for(Malus m : c.maluses){
                            maluses.add(m.clone());
                        }
                    }
                    // The same rule apply to interactives = they can be deleted to signal it has been used
                    if(c.interactives != null){
                        for(Interactive inter : c.interactives){
                            interactives.add(inter.clone());
                        }
                    }
                    newHand.add(new Card(c.id,c.name,c.strength, c.type, c.bonuses, maluses, interactives));
                }
            }
            for(Card cardOnTable : cardsOnTable){
                ArrayList<Malus> malusesForTableCard = new ArrayList<>();
                ArrayList<Interactive> interactivesForTableCard = new ArrayList<>();
                if(cardOnTable.maluses != null){
                    //System.out.println("Klonuji kartu " + cardOnTable.name);
                    for(Malus m : cardOnTable.maluses){
                        malusesForTableCard.add(m.clone());
                    }
                }
                // The same rule apply to interactives = they can be deleted to signal it has been used
                if(cardOnTable.interactives != null){
                    for(Interactive inter : cardOnTable.interactives){
                        interactivesForTableCard.add(inter.clone());
                    }
                }
                Card cardToTakeFromTable = new Card(cardOnTable.id,cardOnTable.name,cardOnTable.strength, cardOnTable.type,
                        cardOnTable.bonuses, malusesForTableCard, interactivesForTableCard);
                newHand.add(cardToTakeFromTable);
                // Count the resulting score, add the cardToChange to table for purposes of Necromancer unique ability
                // TODO : Count the currentScore of the newHand array === IMPLEMENT PROPERLY sc.COUNTSCORE()
                newTable.clear();
                newTable = cardsOnTable.stream().filter(card -> !card.equals(cardOnTable)).collect(Collectors.toList());
                //System.out.println("Pocet karet na stole pred polozenim jedne karty z ruky: " + newTable.size() + " pocet karet na ruce " + hand.size() + " pocet karet na NOVE ruce: " + newHand.size());

                Card cardToChangeFromHand = new Card(cardToChange.id,cardToChange.name,cardToChange.strength, cardToChange.type,
                        cardToChange.bonuses, cardToChange.maluses, cardToChange.interactives);
                newTable.add(cardToChangeFromHand);
                //System.out.println("Karty na novem stole po vybrani karty do ruky: ");
                for(Card cardOnTableToList : newTable){
                   // System.out.print(cardOnTableToList.name + ", ");
                }
                //System.out.println("----------------------------------------------------------------------");
                //System.out.print("Karty na nove ruce: ");
                for(Card c: newHand){
                    //System.out.print(c.name + ", ");
                }
                //System.out.println();
                //System.out.println("Pocet karet na stole: " + newTable.size() + " pocet karet na ruce " + hand.size() + " pocet karet na NOVE ruce: " + newHand.size());

                // Count the score with the new cards on hand and on table
                currentScore = sc.countScore(newHand, newTable);
                while (currentScore < 0) {
                    // wait;
                }
                if(currentScore > currentMaxScore){
                    currentMaxScore = currentScore;
                    bestCardToTake = cardOnTable;
                    bestCardToDrop = cardToChange;
                }
                //Take the card back so another one can be tested
                newTable.remove(cardToChangeFromHand);
                newHand.remove(cardToTakeFromTable);
            }
        }


        // Finally performing actions: take card from table if good, otherwise draw card
        if(bestCardToTake != null){
            server.takeCardFromTable(bestCardToTake);
            hand.add(bestCardToTake);
        } else{
            // The AI draws the card in the moment when no card on the table offers increase of the score
            Card cardFromDeck = server.drawCardFromDeck();
            //System.out.println("AI drew card from the deck: " + cardFromDeck.name);
            // Now the AI must decide whether drop the card immediately or it gives better score
            for(Card cardToChange : hand){
                ArrayList<Card> newHand = new ArrayList<>();
                for(Card cardOnHand : hand){
                    if(cardOnHand != cardToChange){
                        ArrayList<Bonus> bonuses = new ArrayList<>();
                        ArrayList<Malus> maluses = new ArrayList<>();
                        ArrayList<Interactive> interactives = new ArrayList<>();
                        // bonuses are never deleted, they can be the same objects
                        // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
                        if(cardOnHand.maluses != null){
                            //System.out.println("Klonuji kartu " + cardOnHand.name);
                            for(Malus m : cardOnHand.maluses){
                                maluses.add(m.clone());
                            }
                        }
                        // The same rule apply to interactives = they can be deleted to signal it has been used
                        if(cardOnHand.interactives != null){
                            for(Interactive inter : cardOnHand.interactives){
                                interactives.add(inter.clone());
                            }
                        }
                        newHand.add(new Card(cardOnHand.id,cardOnHand.name,cardOnHand.strength, cardOnHand.type, cardOnHand.bonuses, maluses, interactives));
                    }
                }

                ArrayList<Malus> malusesForTableCard = new ArrayList<>();
                ArrayList<Interactive> interactivesForTableCard = new ArrayList<>();
                if(cardFromDeck.maluses != null){
                    //System.out.println("Klonuji kartu " + cardFromDeck.name);
                    for(Malus m : cardFromDeck.maluses){
                        malusesForTableCard.add(m.clone());
                    }
                }
                // The same rule apply to interactives = they can be deleted to signal it has been used
                if(cardFromDeck.interactives != null){
                    for(Interactive inter : cardFromDeck.interactives){
                        interactivesForTableCard.add(inter.clone());
                    }
                }
                Card copiedCardFromTable = new Card(cardFromDeck.id,cardFromDeck.name,cardFromDeck.strength, cardFromDeck.type,
                        cardFromDeck.bonuses, malusesForTableCard, interactivesForTableCard);
                newHand.add(copiedCardFromTable);
                    // Count the resulting score, add the cardToChange to table for purposes of Necromancer unique ability
                    // TODO : Count the currentScore of the newHand array === IMPLEMENT PROPERLY sc.COUNTSCORE()
                    newTable = cardsOnTable;
                    newTable.add(cardToChange);
                    //System.out.println("Pocet karet na stole: " + newTable.size() + " pocet karet na ruce " + hand.size());
                    currentScore = sc.countScore(newHand, newTable);
                    while (currentScore < 0) {
                        // wait;
                    }
                newTable.remove(cardToChange);
                if(currentScore > currentMaxScore){
                    currentMaxScore = currentScore;
                    bestCardToDrop = cardToChange;
                }
            }
            if(bestCardToDrop == null){
                bestCardToDrop = cardFromDeck;
            }
                hand.add(cardFromDeck);
        }

        // Drop a card = if there is a best card to drop, drop it. If not, first card in hand will be dropped.
        if(bestCardToDrop == null){
            bestCardToDrop = hand.get(0);
        }
        hand.remove(bestCardToDrop);
        //System.out.print("Players cards on hand [score: " + currentMaxScore + "]: ");
        for(Card c: hand){
            //System.out.print(c.name + ", ");
        }
        //System.out.println();
        server.putCardOnTable(bestCardToDrop);
        //server.setNextPlayer();
    }

    @Override
    public void endGame(){
        countScore();
    }

    @Override
    public void sendNamesInOrder(String s) {
        System.out.println(s);
        String[] message = s.split("#");
        // 0 place is NAMES, start from 1 - which is this players name
        if (message[0].startsWith("$&$START$&$")) {
            try {
                performMove(server.cardsOnTable);
            } catch(CloneNotSupportedException notCloneableEx){
                //System.out.println("Not cloneable");
            }
        }
    }

    @Override
    public void countScore() {
        // Create different hands by interactives that AI has
        ScoreCounterForAI sc = new ScoreCounterForAI();
        score = sc.countScore(hand, server.cardsOnTable);

        while (score < 0) {
            // wait;
        }
        server.increaseCountedScoreNumber();
    }
}
