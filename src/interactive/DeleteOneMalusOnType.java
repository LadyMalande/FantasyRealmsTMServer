package interactive;

import artificialintelligence.ScoreCounterForAI;
import artificialintelligence.State;
import maluses.Malus;
import server.Card;
import server.ClientHandler;
import server.Server;
import server.Type;
import util.BigSwitches;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The interactive bonus that implements the possibility of deleting one malus on card of offered types.
 * @author Tereza Miklóšová
 */
public class DeleteOneMalusOnType extends Interactive {
    /**
     * Bonus is resolved after ChangeColor cards
     */
    public int priority = 5;
    /**
     * Types of cards that can have one of their maluses deleted.
     */
    public ArrayList<Type> types;
    /**
     * Id of the card containing this bonus.
     */
    private int thiscardid;

    /**
     * Constructor of this interactive bonus.
     * @param id Id of the card containing this bonus.
     * @param types Types of cards that can have one of their maluses deleted.
     */
    public DeleteOneMalusOnType(int id, ArrayList<Type> types) {
        this.types = types;
        this.thiscardid = id;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("interactive.CardInteractives",loc);
        ResourceBundle type = ResourceBundle.getBundle("server.CardTypes",loc);
        sb.append(rb.getString("deleteOneMalusOnType")).append(" ");
        sb.append(type.getString("one2" + BigSwitches.switchTypeForGender(types.get(0))));
        sb.append(" ");
        sb.append(giveListOfTypesWithSeparator(types, locale, 2));
        sb.append(".");
        return sb.toString();
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
                if(c.getType().equals(t)){
                    if(c.getMaluses() != null && !c.getMaluses().isEmpty()){
                        if(!firstTime){
                            str.append("%");
                        }

                        boolean firstTimeInMaluses = true;
                        for(Malus m: c.getMaluses()){
                            if(!firstTimeInMaluses){
                                str.append("%");
                            }
                            str.append(c.getNameLoc(client.locale.getLanguage())).append(": ");
                            str.append(m.getText(client.locale.getLanguage()));
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
    public void askPlayer(ClientHandler client) {
       String s = getAllMalusesForTypesToString(client);
        if(s.isEmpty()){
            synchronized(client.interactivesResolvedAtomicBoolean) {
                client.interactivesResolved.incrementAndGet();
                //client.futureTask.notify();
                client.interactivesResolvedAtomicBoolean.set(true);
            }
        }
        else{
            client.sendMessage("DeleteOneMalusOnType#" + thiscardid + "#" + getAllMalusesForTypesToString(client));
        }
    }

    @Override
    public int changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable, Server server,
                                         Integer idOfCardToChange, Card toChangeInto) throws CloneNotSupportedException {

        Card onWhichDeleteMalus = null;
        Malus whichMalusToDelete = null;

        // Remove this interactive from card to not get stuck in loop
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
        int bestScore = sc.countScore(newHandOldScore, cardsOnTable, true);

        //Try to delete every malus on hand and find out which one is the best to remove
        for(Card c : originalHand){
            if(this.types.contains(c.getType())) {
                if (c.getMaluses() != null) {
                    ArrayList<Malus> malusesCopyToIterate = new ArrayList<>(c.getMaluses());
                    for (Malus malus : malusesCopyToIterate){
                        c.getMaluses().remove(malus);


                        List<Card> newHand = new ArrayList<>();
                        for(Card copy : originalHand){
                            ArrayList<Malus> maluses = new ArrayList<>();
                            ArrayList<Interactive> interactives = new ArrayList<>();
                            // bonuses are never deleted, they can be the same objects
                            // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
                            if(copy.getMaluses() != null){
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
                            newHand.add(new Card(copy.getId(),copy.getName(),copy.getStrength(), copy.getType(), copy.getBonuses(), maluses, interactives));
                        }


                        int currentScore = sc.countScore(newHand, cardsOnTable, true);
                        if (currentScore > bestScore) {
                            bestScore = currentScore;
                            whichMalusToDelete = malus;
                            onWhichDeleteMalus = c;
                        }
                        c.getMaluses().add(malus);
                    }
                }
            }
        }
        if(onWhichDeleteMalus != null && whichMalusToDelete != null){
            onWhichDeleteMalus.getMaluses().remove(whichMalusToDelete);
            idOfCardToChange = onWhichDeleteMalus.getId();
            toChangeInto = onWhichDeleteMalus;
        }

        return bestScore;
    }

    @Override
    public Interactive clone() throws CloneNotSupportedException{
        DeleteOneMalusOnType newi = (DeleteOneMalusOnType)super.clone();
        newi.priority = this.priority;
        newi.types = new ArrayList<> (this.types);
        newi.thiscardid = this.thiscardid;
        return newi;
    }

    @Override
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }
}
