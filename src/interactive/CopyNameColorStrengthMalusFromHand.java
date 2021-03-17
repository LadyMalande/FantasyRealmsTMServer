package interactive;

import artificialintelligence.ScoreCounterForAI;
import maluses.Malus;
import server.Card;
import server.ClientHandler;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class CopyNameColorStrengthMalusFromHand extends Interactive  {
    public int priority = 2;
    public String text = "Copy name, type, strength and malus of any card in your hand";
    private int thiscardid;


    @Override
    public String getText(){
        return this.text;
    }

    @Override
    public String getText(String locale){
        StringBuilder sb = new StringBuilder();
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("interactive.CardInteractives",loc);
        sb.append(rb.getString("copyNameColorStrengthMalusFromHand"));
        return sb.toString();
    }

    public CopyNameColorStrengthMalusFromHand(int id){
        this.thiscardid = id;
        //System.out.println("Card INIT: Text: " + getText());
        //System.out.println("Card INIT: Text: " + getText("en"));
        //System.out.println("Card INIT: Text: " + getText("cs"));
    }

    @Override
    public boolean askPlayer(ClientHandler client) {
        return client.sendInteractive("CopyCardFromHand#" + thiscardid);

    }

    @Override
    public void changeHandWithInteractive(ArrayList<Card> originalHand, ArrayList<Card> cardsOnTable) throws CloneNotSupportedException {

        //Count the hand without this to not get wrong combination
        int bestScore = 0;

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

        ScoreCounterForAI sc = new ScoreCounterForAI();
        bestScore = sc.countScore(newHandOldScore, cardsOnTable);


        Card whichCardToCopy = null;
        for(Card cardInHand : originalHand){
            ArrayList<Card> newHand = new ArrayList<>();
            //Make copy of this hand without this interactive and copy one card
            if(cardInHand.id != thiscardid){

                ArrayList<Malus> maluses2 = new ArrayList<>();
                for(Card copy : originalHand){
                    if(copy.id != thiscardid){
                        //copy the card to new hand
                        ArrayList<Malus> maluses = new ArrayList<>();

                            ArrayList<Interactive> interactives = new ArrayList<>();
                            // bonuses are never deleted, they can be the same objects
                            // Maluses can be deleted so they need to be cloned to form new objects so the reference doesnt vanish from old card
                            if(copy.maluses != null){
                                //System.out.println("Klonuji kartu " + copy.name);
                                for(Malus m : copy.maluses){
                                    maluses.add(m.clone());
                                    maluses2.add(m.clone());
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
                }
                // Add the changed Doppleganger to hand
                newHand.add(new Card(thiscardid, cardInHand.name, cardInHand.strength, cardInHand.type,null, maluses2, null));
        }

            int currentScore = sc.countScore(newHand,cardsOnTable);
            if (currentScore > bestScore) {
                bestScore = currentScore;
                whichCardToCopy = cardInHand;

            }

        }
        if(whichCardToCopy != null){
            for(Card fromOriginalHand : originalHand){
                if(fromOriginalHand.id == thiscardid){
                    fromOriginalHand.name = whichCardToCopy.name;
                    fromOriginalHand.strength = whichCardToCopy.strength;
                    fromOriginalHand.type = whichCardToCopy.type;
                    ArrayList<Malus> newMaluses = new ArrayList<>();
                    if(fromOriginalHand.maluses != null){
                        for(Malus m : fromOriginalHand.maluses){
                            newMaluses.add(m.clone());
                        }
                    }
                    fromOriginalHand.maluses = newMaluses;
                }
            }
            //System.out.println("Skinchanger changed into: " + whichCardToCopy.name);
        }



    }

    @Override
    public Interactive clone() throws CloneNotSupportedException{
        CopyNameColorStrengthMalusFromHand newi = (CopyNameColorStrengthMalusFromHand)super.clone();
        newi.priority = this.priority;
        newi.text = this.text;
        newi.thiscardid = this.thiscardid;
        return newi;
    }
}
