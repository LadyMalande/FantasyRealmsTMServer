package util;

import interactive.Interactive;
import maluses.Malus;
import server.Card;

import java.util.ArrayList;

/**
 * Support class for cloning the cards in the hand so that the original hand is not affected.
 * @author Tereza Miklóšová
 */
public class HandCloner {

    /**
     * Clones all the cards on the table.
     * @param cardOnTable Cards to clone.
     * @return Cloned cards from the table.
     * @throws CloneNotSupportedException Thrown if anything goes wrong with cloning cards.
     */
    public static Card cloneCard(Card cardOnTable) throws CloneNotSupportedException {
        ArrayList<Malus> malusesForTableCard = new ArrayList<>();
        ArrayList<Interactive> interactivesForTableCard = new ArrayList<>();
        if (cardOnTable.getMaluses() != null) {
            for (Malus m : cardOnTable.getMaluses()) {
                malusesForTableCard.add(m.clone());
            }
        }
        // The same rule apply to interactives = they can be deleted to signal it has been used
        if (cardOnTable.getInteractives() != null) {
            for (Interactive inter : cardOnTable.getInteractives()) {
                interactivesForTableCard.add(inter.clone());
            }
        }
        return new Card(cardOnTable.getId(), cardOnTable.getName(), cardOnTable.getStrength(), cardOnTable.getType(),
                cardOnTable.getBonuses(), malusesForTableCard, interactivesForTableCard);
    }

    /**
     * Clones given hand except the card that will be changed.
     * @param cardToChange Don't clone this card.
     * @param hand Cards to clone.
     * @return Cloned cards in hand except the card that is not to be cloned.
     * @throws CloneNotSupportedException Thrown if anything goes wrong with cloning cards.
     */
    public static ArrayList<Card> cloneHand(Card cardToChange, ArrayList<Card> hand) throws CloneNotSupportedException {
        ArrayList<Card> newHand = new ArrayList<>();
        for(Card cardOnHand : hand){
            if(cardOnHand != cardToChange){
                newHand.add(cloneCard(cardOnHand));
            }
        }
        return newHand;
    }
}
