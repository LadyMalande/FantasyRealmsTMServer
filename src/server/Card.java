package server;


import bonuses.Bonus;
import interactive.Interactive;
import maluses.Malus;

import java.io.Serializable;
import java.util.ArrayList;

public class Card implements Serializable {
    public long serialVersionUID = 11;
    public int id;
    public String name;
    public int strength;
    public Type type;
    public ArrayList<Bonus> bonuses;
    public ArrayList<Malus> maluses;
    public ArrayList<Interactive> interactives;

    Card(int id,String name, int strength, Type type, ArrayList<Bonus> bonuses, ArrayList<Malus> maluses, ArrayList<Interactive> in){
        this.id = id;
        this.name = name;
        this.strength = strength;
        this.type = type;
        this.bonuses = bonuses;
        this.maluses = maluses;
        this.interactives = in;
    }
}
