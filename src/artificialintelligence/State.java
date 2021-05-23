package artificialintelligence;

import server.Card;
import server.Type;

import java.util.ArrayList;
import java.util.Arrays;

public class State {

    private int[] cardsIDs;
    private int numARMY,numARTIFACT,numBEAST,numFLAME,numFLOOD, numLAND, numLEADER,numWEAPON,numWEATHER,numWILD,numWIZARD = 0;
    private int mostFromOneType, numTypes = 0;
    private int numPointsLost = 0;
    private int numOdd = 0;
    private int numberOfEnemies = 0;
    ArrayList<Card> probablyInDeck;

    public ArrayList<Type> getHaveTheseTypes() {
        return haveTheseTypes;
    }

    public void setHaveTheseTypes(ArrayList<Type> haveTheseTypes) {
        this.haveTheseTypes = haveTheseTypes;
    }

    ArrayList<Type> whichTypeIsMost;
    ArrayList<Type> haveTheseTypes;

    public ArrayList<Type> getWhichTypeIsMost() {
        return whichTypeIsMost;
    }

    public void setWhichTypeIsMost(ArrayList<Type> whichTypeIsMost) {
        this.whichTypeIsMost = whichTypeIsMost;
    }

    public ArrayList<Card> getProbablyInDeck() {
        return probablyInDeck;
    }

    public int getNumOfType(Type type){
        switch (type){
            case ARTIFACT: return numARTIFACT;
            case WEATHER: return numWEATHER;
            case WIZARD: return numWIZARD;
            case WEAPON: return numWEAPON;
            case LEADER: return numLEADER;
            case FLOOD: return numFLOOD;
            case WILD:return numWILD;
            case ARMY: return numARMY;
            case LAND: return numLAND;
            case FLAME: return numFLAME;
            case BEAST: return numBEAST;
        }
        return 0;
    }

    public int getNumberOfEnemies() {
        return numberOfEnemies;
    }

    private int shapeshifter;

    public int getMostFromOneType() {
        return mostFromOneType;
    }


    public int getNumTypes() {
        return numTypes;
    }


    public int getNumPointsLost() {
        return numPointsLost;
    }


    public int getNumOdd() {
        return numOdd;
    }


    public int getShapeshifter() {
        return shapeshifter;
    }

    public void setShapeshifter(int shapeshifter) {
        this.shapeshifter = shapeshifter;
    }

    public boolean[] getCandle() {
        return candle;
    }

    public void setCandle(boolean[] candle) {
        this.candle = candle;
    }

    public boolean[] getMountain() {
        return mountain;
    }

    public void setMountain(boolean[] mountain) {
        this.mountain = mountain;
    }

    public boolean[] getWhirlwind() {
        return whirlwind;
    }

    public void setWhirlwind(boolean[] whirlwind) {
        this.whirlwind = whirlwind;
    }


    public int getMirage() {
        return mirage;
    }

    public void setMirage(int mirage) {
        this.mirage = mirage;
    }

    private int mirage = -1;
    private boolean[] kingdom = new boolean[3];
    private boolean[] candle = new boolean[4];
    private boolean[] mountain = new boolean[4];
    private boolean[] whirlwind = new boolean[3];
    private boolean[] armory = new boolean[4];
    public State(int[] arr){
        this.cardsIDs = Arrays.copyOf(arr, arr.length);
    }

    public boolean equals(int[] other){
        Arrays.sort(other);
        return Arrays.equals(cardsIDs, other);
    }

}

