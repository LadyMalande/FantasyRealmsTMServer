package bonuses;

import artificialintelligence.State;
import maluses.Malus;
import server.Card;
import server.Type;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the bonuses.
 * @author Tereza Miklóšová
 */
public class Bonus implements ScoringInterface, Serializable, Cloneable {

    /**
     * Default priority of the bonus. Bonuses that do not have priority specified get this one.
     */
    private int priority = 8;

    /**
     * Get text of the bonus in requested language.
     * @param locale The language in which we want the text.
     * @return Returns text of the bonus in the requested language.
     */
    public String getText(String locale){
        return null;
    }

    /**
     * Get {@link Bonus#priority}
     * @return {@link Bonus#priority}
     */
    public int getPriority(){
        return this.priority;
    }

    /**
     * Counts the score of the given hand.
     * @param hand The cards to count score for.
     * @return Score of the hand.
     */
    @Override
    public int count(ArrayList<Card> hand) {
        return 0;
    }

    /**
     * Get the types with which the bonus is reacting.
     * @param hand The hand of the agent.
     * @return Types which react with nonzero effect with this card.
     */
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {
        return null;
    }

    /**
     * If the bonus satisfies the condition with only one card out of one, it returns the card that
     * satisfies the condition.
     * @param hand Cards in the hand of the agent.
     * @return Card that can't be changed.
     */
    public Card satisfiesCondition(ArrayList<Card> hand) {
        //Says ids of cards that cant be recolored if the size of this array is only 1
        return null;
    }

    /**
     * Get how much this bonus contributes to the final scoring.
     * @param hand The cards in the agent's hand.
     * @return How many points this bonus gives to the final score.
     */
    public int getHowMuch(ArrayList<Card> hand) {
        return 0;
    }

    /**
     * Computes the potential of the bonus.
     * @param hand The cards in the hand of the agent.
     * @param table The cards on the table.
     * @param deckSize The size of the remaining cards in deck.
     * @param unknownCards Number of cards we don't know that are ingame.
     * @param state The state of the agent.
     * @return Potential score gain.
     */
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        return 0.0;
    }

    /**
     * Clones the Bonus.
     * @return Cloned bonus object.
     * @throws CloneNotSupportedException Throws if the clone is not fully supported.
     */
    public Bonus clone() throws CloneNotSupportedException{
        Bonus newb = (Bonus)super.clone();
        newb.priority = this.priority;
        return newb;
    }

    /**
     * Counts the value of the removed cards.
     * @param hand Cards in the hand.
     * @param whatToRemove Cards that would be removed.
     * @return The sum of the value of the removed cards in given hand.
     */
    public static double giveValue(ArrayList<Card> hand, ArrayList<Card> whatToRemove){
        double sum = 0.0;
        for(Card removed : whatToRemove){
            sum += removed.getStrength();
            for(Bonus b : removed.getBonuses()){
                sum += b.count(hand);
            }
            for(Malus m : removed.getMaluses()){
                sum += m.count(hand);
            }
        }
        return sum;
    }

    /**
     * Tells if the bonus reacts with given types.
     * @param types Types we want to know if react with this bonus.
     * @return True if any of the types reacts with the bonus. False otherwise.
     */
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }

    /**
     * Returns how much the given type reacts in given hand.
     * @param t Type we want to know how many points it gives to total score.
     * @param hand Cards in the hand.
     * @return Points that the given type gives in given hand.
     */
    public int getReaction(Type t, ArrayList<Card> hand){
        return 0;
    }
}
