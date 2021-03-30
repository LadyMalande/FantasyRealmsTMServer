package server;


import bonuses.Bonus;
import interactive.Interactive;
import maluses.Malus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;

public class Card implements Serializable, Cloneable, Comparable<Card>{
    public long serialVersionUID = 11;
    public int id;
    public String name;
    public int strength;
    public Type type;
    public ArrayList<Bonus> bonuses;
    public ArrayList<Malus> maluses;
    public ArrayList<Interactive> interactives;

    public Card(int id,String name, int strength, Type type, ArrayList<Bonus> bonuses, ArrayList<Malus> maluses, ArrayList<Interactive> in){
        this.id = id;
        this.name = name;
        this.strength = strength;
        this.type = type;
        this.bonuses = bonuses;
        this.maluses = maluses;
        this.interactives = in;
    }

    public Card clone() throws CloneNotSupportedException{
        Card card = (Card) super.clone();
        card.serialVersionUID = this.serialVersionUID;
        card.id = this.id;
        card.name = new String(this.name);
        card.strength = this.strength;
        card.type = this.type;
        ArrayList<Bonus> newb = new ArrayList<Bonus>();
        for(Bonus b: bonuses){
            newb.add((Bonus)b.clone());
        }
        ArrayList<Malus> newm = new ArrayList<Malus>();
        for(Malus m: maluses){
            newm.add((Malus)m.clone());

        }
        ArrayList<Interactive> newi = new ArrayList<Interactive>();
        for(Interactive i: interactives){
            newi.add((Interactive)i.clone());
        }
        return card;
    }

    public Type getType(){
        return this.type;
    }

    public ArrayList<Malus> getMaluses(){ return this.maluses;}

    public String getTypeName(String locale){
        Locale loc = new Locale(locale);
        ResourceBundle rb = ResourceBundle.getBundle("server.CardTypes",loc);
        return rb.getString(BigSwitches.switchTypeForName(this.type, locale).toLowerCase());
    }

    public int getStrength(){
        return this.strength;
    }

    public String getName(){
        return this.name;
    }

    public String getNameLoc(String locale){
        return BigSwitches.switchIdForName(id, locale);
    }

    @Override
    public int compareTo(Card card){
        return Comparator.comparing(Card::getType)
                .thenComparingInt(Card::getStrength)
                .thenComparing(Card::getName)
                .compare(this, card);
    }
}
