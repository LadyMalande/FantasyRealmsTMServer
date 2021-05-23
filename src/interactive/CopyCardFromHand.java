package interactive;

import artificialintelligence.ScoreCounterForAI;
import artificialintelligence.State;
import maluses.Malus;
import server.Card;
import server.ClientHandler;
import server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The interactive bonus that implements the possibility to copy a name, strength, type and all maluses
 * from any other card in hand.
 * @author Tereza Miklóšová
 */
public class CopyCardFromHand extends Interactive  {
    /**
     * This bonus is counted before CopyNameAndType.
     */
    public int priority = 2;
    /**
     * Id of the card containing this bonus.
     */
    private int thiscardid;


    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("interactive.CardInteractives",loc);
        sb.append(rb.getString("copyNameColorStrengthMalusFromHand"));
        return sb.toString();
    }

    /**
     * Constructor for this interactive bonus.
     * @param id Id of the card containing this bonus.
     */
    public CopyCardFromHand(int id){
        this.thiscardid = id;
    }

    @Override
    public void askPlayer(ClientHandler client) {
        client.sendMessage("CopyCardFromHand#" + thiscardid);

    }

    @Override
    public int changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable, Server server,
                                         Integer idOfCardToChange, Card toChangeInto) throws CloneNotSupportedException {
        //System.out.println("Counting CopyNameColorStrengthMalus From hand");
        //Count the hand without this to not get wrong combination
        int bestScore;

        for(Card original : originalHand){
            if(thiscardid == original.getId()){
                original.getInteractives().remove(this);
            }
        }

        //Copy hand for computing the original score on Hand
        List<Card> newHandOldScore = new ArrayList<>();
        for(Card copy : originalHand){
            ArrayList<Malus> maluses = new ArrayList<>();
            ArrayList<Interactive> interactives = new ArrayList<>();
            // bonuses are never deleted, they can be the same objects
            // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
            if(copy.getMaluses() != null){
                //System.out.println("Klonuji kartu " + copy.name);
                for(Malus m : copy.getMaluses()){
                    maluses.add(m.clone());
                }
            }
            // The same rule apply to interactives = they can be deleted to signal it has been used
            if(copy.getInteractives() != null){
                for(Interactive inter : copy.getInteractives()){
                    interactives.add(inter.clone());
                }
            }
            newHandOldScore.add(new Card(copy.getId(),copy.getName(),copy.getStrength(), copy.getType(), copy.getBonuses(), maluses, interactives));
        }

        ScoreCounterForAI sc = new ScoreCounterForAI(server);
        bestScore = sc.countScore(newHandOldScore, cardsOnTable, true);


        Card whichCardToCopy = null;
        for(Card cardInHand : originalHand){
            ArrayList<Card> newHand = new ArrayList<>();
            //Make copy of this hand without this interactive and copy one card
            if(cardInHand.getId() != thiscardid){

                ArrayList<Malus> maluses2 = new ArrayList<>();
                for(Card copy : originalHand){
                    if(copy.getId() != thiscardid){
                        //copy the card to new hand
                        ArrayList<Malus> maluses = new ArrayList<>();

                            ArrayList<Interactive> interactives = new ArrayList<>();
                            // bonuses are never deleted, they can be the same objects
                            // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
                            if(copy.getMaluses() != null){
                                for(Malus m : copy.getMaluses()){
                                    maluses.add(m.clone());
                                    maluses2.add(m.clone());
                                }
                            }
                            // The same rule apply to interactives = they can be deleted to signal it has been used
                            if(copy.getInteractives() != null){
                                for(Interactive inter : copy.getInteractives()){
                                    interactives.add(inter.clone());
                                }
                            }
                            newHand.add(new Card(copy.getId(),copy.getName(),copy.getStrength(), copy.getType(), copy.getBonuses(), maluses, interactives));

                    }
                }
                if(cardInHand.getMaluses() != null){
                    for(Malus m : cardInHand.getMaluses()){
                        maluses2.add(m.clone());
                    }
                }
                // Add the changed Doppleganger to hand
                newHand.add(new Card(thiscardid, cardInHand.getName(), cardInHand.getStrength(), cardInHand.getType(),null, maluses2, null));
            }

            int currentScore = sc.countScore(newHand,cardsOnTable, true);
            if (currentScore > bestScore) {
                bestScore = currentScore;
                whichCardToCopy = cardInHand;

            }
        }
        if(whichCardToCopy != null){
            for(Card fromOriginalHand : originalHand){
                if(fromOriginalHand.getId() == thiscardid){
                    fromOriginalHand.setName(whichCardToCopy.getName());
                    fromOriginalHand.setStrength(whichCardToCopy.getStrength());
                    fromOriginalHand.setType(whichCardToCopy.getType());
                    ArrayList<Malus> newMaluses = new ArrayList<>();
                    if(fromOriginalHand.getMaluses() != null){
                        for(Malus m : fromOriginalHand.getMaluses()){
                            newMaluses.add(m.clone());
                        }
                    }
                    fromOriginalHand.setMaluses(newMaluses);
                    idOfCardToChange = thiscardid;
                    toChangeInto = fromOriginalHand;
                }
            }
        }
        return bestScore;
    }

    @Override
    public Interactive clone() throws CloneNotSupportedException{
        CopyCardFromHand newi = (CopyCardFromHand)super.clone();
        newi.priority = this.priority;
        newi.thiscardid = this.thiscardid;
        return newi;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }
}
