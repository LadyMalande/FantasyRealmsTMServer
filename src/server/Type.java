package server;

import java.io.Serializable;

/**
 * This enum specifies all necessary colors of cards that are used in the game.
 * @author Tereza Miklóšová
 */
public enum Type implements Serializable{
    FLOOD, FLAME, LAND, WEATHER, ARMY, WEAPON, ARTIFACT, WIZARD, LEADER, BEAST, WILD;
}
