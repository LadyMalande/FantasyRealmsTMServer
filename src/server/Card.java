package server;


import bonuses.Bonus;
import interactive.Interactive;
import maluses.Malus;
import util.BigSwitches;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Object having every needed attribute to represent the real card from the game.
 * @author Tereza Miklóšová
 */
public class Card implements Serializable, Cloneable, Comparable<Card>{

    /**
     * Unique identified of the card.
     */
    private int id;
    /**
     * Name of the card.
     */
    private String name;
    /**
     * Basic strength of the card that it gives to the final score.
     */
    private int strength;
    /**
     * Type (color) of the card.
     */
    private Type type;
    /**
     * List of bonuses if the card has any.
     */
    private ArrayList<Bonus> bonuses;
    /**
     * List of penalties if the card has any.
     */
    private ArrayList<Malus> maluses;
    /**
     * List of interactive bonuses if the card has any.
     */
    private ArrayList<Interactive> interactives;

    /**
     * Constructor for the Card object. Requires all the information about the card, no default one is plausible.
     * @param id Unique identified of the card.
     * @param name Name of the card.
     * @param strength Basic strength of the card that it gives to the final score.
     * @param type Type (color) of the card.
     * @param bonuses List of bonuses if the card has any.
     * @param maluses List of penalties if the card has any.
     * @param in List of interactive bonuses if the card has any.
     */
    public Card(int id,String name, int strength, Type type, ArrayList<Bonus> bonuses, ArrayList<Malus> maluses, ArrayList<Interactive> in){
        this.id = id;
        this.name = name;
        this.strength = strength;
        this.type = type;
        this.bonuses = bonuses;
        this.maluses = maluses;
        this.interactives = in;
    }

    /**
     * Clone the card object.
     * @return Cloned Card object.
     * @throws CloneNotSupportedException Thrown if there were some issues with cloning any part of the card.
     */
    public Card clone() throws CloneNotSupportedException{
        Card card = (Card) super.clone();
        card.id = this.id;
        card.name = this.name;
        card.strength = this.strength;
        card.type = this.type;
        ArrayList<Bonus> newb = new ArrayList<>();
        for(Bonus b: bonuses){
            newb.add(b.clone());
        }
        card.bonuses = newb;
        ArrayList<Malus> newm = new ArrayList<>();
        for(Malus m: maluses){
            newm.add(m.clone());

        }
        card.maluses = newm;
        ArrayList<Interactive> newi = new ArrayList<>();
        for(Interactive i: interactives){
            newi.add(i.clone());
        }
        card.interactives = newi;
        return card;
    }

    /**
     * Get {@link Card#id}.
     * @return {@link Card#id}
     */
    public int getId(){ return this.id;}

    /**
     * Get {@link Card#type}.
     * @return {@link Card#type}
     */
    public Type getType(){
        return this.type;
    }

    /**
     * Get {@link Card#maluses}.
     * @return {@link Card#maluses}
     */
    public ArrayList<Malus> getMaluses(){ return this.maluses;}

    /**
     * Get {@link Card#strength}.
     * @return {@link Card#strength}
     */
    public int getStrength(){
        return this.strength;
    }

    /**
     * Get {@link Card#name}.
     * @return {@link Card#name}
     */
    public String getName(){
        return this.name;
    }

    /**
     * Get name of the card in target language.
     * @param locale Target language.
     * @return Name of the card in target language.
     */
    public String getNameLoc(String locale){
        return BigSwitches.switchIdForName(id, locale);
    }

    /**
     * Set {@link Card#name}.
     * @param name {@link Card#name}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set {@link Card#strength}.
     * @param strength {@link Card#strength}
     */
    public void setStrength(int strength) {
        this.strength = strength;
    }

    /**
     * Set {@link Card#type}.
     * @param type {@link Card#type}
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     *  Get {@link Card#bonuses}.
     * @return {@link Card#bonuses}
     */
    public ArrayList<Bonus> getBonuses() {
        return bonuses;
    }

    /**
     *  Get {@link Card#interactives}.
     * @return {@link Card#interactives}
     */
    public ArrayList<Interactive> getInteractives() {
        return interactives;
    }

    /**
     * Tells whether the card has odd strength.
     * @return True if the strength of the card is odd. False otherwise.
     */
    public boolean isOdd(){return strength % 2 != 0;}

    /**
     * Compares the card to another card by type, then by strength, finally by name.
     * @param card Card to compare this card to.
     * @return The Order of the cards.
     */
    @Override
    public int compareTo(Card card){
        return Comparator.comparing(Card::getType)
                .thenComparingInt(Card::getStrength)
                .thenComparing(Card::getName)
                .compare(this, card);
    }

    /**
     * Sets {@link Card#maluses}.
     * @param maluses {@link Card#maluses}
     */
    public void setMaluses(ArrayList<Malus> maluses) {
        this.maluses = maluses;
    }

    /**
     * Sets {@link Card#interactives}.
     * @param interactives {@link Card#interactives}
     */
    public void setInteractives(ArrayList<Interactive> interactives) {
        this.interactives = interactives;
    }
}
