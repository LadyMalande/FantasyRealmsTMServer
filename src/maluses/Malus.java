package maluses;

import artificialintelligence.State;
import bonuses.ScoringInterface;
import server.Card;
import server.Type;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class represents the basic penalty.
 * @author Tereza Miklóšová
 */
public class Malus implements ScoringInterface , Serializable, Cloneable  {
    /**
     * This card is resolved after interactive bonuses and bonuses removing parts or whole maluses.
     */
    public int priority = 6;
    /**
     * Types that are affected by the malus.
     */
    public ArrayList<Type> types;
    /**
     * Cards that are not affected by the malus.
     */
    public ArrayList<Integer> cards;

    /**
     * Get localized text of the malus.
     * @param locale Target language.
     * @return Localized text of the malus.
     */
    public String getText(String locale){  return null; }

    /**
     * Get {@link Malus#priority}.
     * @return {@link Malus#priority}
     */
    public int getPriority(){
        return this.priority;
    }

    /**
     * Get {@link Malus#types}.
     * @return {@link Malus#types}
     */
    public  ArrayList<Type> getTypes(){
        return this.types;
    }

    /**
     * Get {@link Malus#cards}.
     * @return {@link Malus#cards}
     */
    public ArrayList<Integer> getCards(){ return null;}

    @Override
    public int count(ArrayList<Card> hand){
        return 0;
    }

    /**
     * Different kind of count that doesn't exactly count the score but takes track of which cards
     * are deleted from the hand.
     * @param hand Cards in the hand.
     * @param whatToRemove Cards that will be removed from hand.
     * @return The important part of the method is the one given in parameter by reference.
     */
    public int count(ArrayList<Card> hand, ArrayList<Card> whatToRemove){return 0;}


    /**
     * Give types that react with the given malus for given hand.
     * @param hand Cards in hand.
     * @return Types that react with the malus.
     */
    public ArrayList<Type> getTypesAvailable(ArrayList<Card> hand) {

        return types;
    }

    /**
     * Counts whether the malus can be removed with adding one more card of needed type.
     * @param hand Cards in hand.
     * @return Card that can and should be recolored to not be removed or not affect other cards badly.
     */
    public Card satisfiesCondition(ArrayList<Card> hand)
    {
        //Says ids of cards that cant be recolored if the size of this array is only 1
        return null;
    }

    /**
     * Counts how many points will be deducted in given hand.
     * @param hand Cards in hand.
     * @return Points that will be deducted from given hand.
     */
    public int getHowMuch(ArrayList<Card> hand) {
        return 0;
    }

    /**
     * Clones the Malus.
     * @return Cloned malus object.
     * @throws CloneNotSupportedException Throws if the clone is not fully supported.
     */
    public Malus clone() throws CloneNotSupportedException{
        return (Malus)super.clone();
    }

    /**
     * Computes the potential of the malus.
     * @param hand The cards in the hand of the agent.
     * @param table The cards on the table.
     * @param deckSize The size of the remaining cards in deck.
     * @param unknownCards Number of cards we don't know that are ingame.
     * @param state The state of the agent.
     * @return Potential score gain.
     */
    public double getPotential(ArrayList<Card> hand, ArrayList<Card> table, int deckSize, int unknownCards, State state){
        // TODO
        return 0.0;
    }

    /**
     * Tells if the malus reacts with given types.
     * @param types Types we want to know if react with this bonus.
     * @return True if any of the types reacts with the malus. False otherwise.
     */
    public boolean reactsWithTypes(ArrayList<Type> types){
        return false;
    }

    /**
     * Returns how much the given type reacts in given hand.
     * @param t Type we want to know how many points it deducts from total score.
     * @param hand Cards in the hand.
     * @return Points that the given type deducts from given hand.
     */
    public int getReaction(Type t, ArrayList<Card> hand) {
        return 0;
    }

    /**
     * Computes which cards will be deleted by this malus.
     * @return List of ids of cards that will be deleted by this malus.
     */
    public ArrayList<Integer> returnWillBeDeleted(){
        return null;
    }
}
