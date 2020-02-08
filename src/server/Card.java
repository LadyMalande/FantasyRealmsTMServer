package server;


import bonuses.Bonus;
import interactive.Interactive;
import maluses.Malus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public int giveMinPriority(){
        ArrayList<Integer> priorities = new ArrayList<Integer>();
        if(bonuses != null)
            for(Bonus b: bonuses){
                priorities.add(b.priority);
            }
        if(maluses != null)
            for(Malus m: maluses){
                priorities.add(m.priority);
            }
        if(interactives != null)
            for(Interactive in: interactives){
                priorities.add(in.priority);
            }
        Collections.sort(priorities);
        return priorities.get(0);
    }
}
