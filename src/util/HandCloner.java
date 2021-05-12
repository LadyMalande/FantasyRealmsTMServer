package util;

import interactive.Interactive;
import maluses.Malus;
import server.Card;

import java.util.ArrayList;

public class HandCloner {
    public HandCloner(){

    }
    public ArrayList<Card> cloneHand(Card cardToChange, ArrayList<Card> hand) throws CloneNotSupportedException {
        ArrayList<Card> newHand = new ArrayList<>();
        for(Card cardOnHand : hand){
            if(cardOnHand != cardToChange){
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
        return newHand;
    }
}
