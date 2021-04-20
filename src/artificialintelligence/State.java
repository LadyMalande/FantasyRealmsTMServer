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

    public void setProbablyInDeck(ArrayList<Card> probablyInDeck) {
        this.probablyInDeck = probablyInDeck;
    }

    public int getNumberOfEnemies() {
        return numberOfEnemies;
    }

    public void setNumberOfEnemies(int numberOfEnemies) {
        this.numberOfEnemies = numberOfEnemies;
    }

    private int shapeshifter;

    public int[] getCardsIDs() {
        return cardsIDs;
    }

    public void setCardsIDs(int[] cardsIDs) {
        this.cardsIDs = cardsIDs;
    }

    public int getNumARMY() {
        return numARMY;
    }

    public void setNumARMY(int numARMY) {
        this.numARMY = numARMY;
    }

    public int getNumARTIFACT() {
        return numARTIFACT;
    }

    public void setNumARTIFACT(int numARTIFACT) {
        this.numARTIFACT = numARTIFACT;
    }

    public int getNumBEAST() {
        return numBEAST;
    }

    public void setNumBEAST(int numBEAST) {
        this.numBEAST = numBEAST;
    }

    public int getNumFLAME() {
        return numFLAME;
    }

    public void setNumFLAME(int numFLAME) {
        this.numFLAME = numFLAME;
    }

    public int getNumFLOOD() {
        return numFLOOD;
    }

    public void setNumFLOOD(int numFLOOD) {
        this.numFLOOD = numFLOOD;
    }

    public int getNumLAND() {
        return numLAND;
    }

    public void setNumLAND(int numLAND) {
        this.numLAND = numLAND;
    }

    public int getNumLEADER() {
        return numLEADER;
    }

    public void setNumLEADER(int numLEADER) {
        this.numLEADER = numLEADER;
    }

    public int getNumWEAPON() {
        return numWEAPON;
    }

    public void setNumWEAPON(int numWEAPON) {
        this.numWEAPON = numWEAPON;
    }

    public int getNumWEATHER() {
        return numWEATHER;
    }

    public void setNumWEATHER(int numWEATHER) {
        this.numWEATHER = numWEATHER;
    }

    public int getNumWILD() {
        return numWILD;
    }

    public void setNumWILD(int numWILD) {
        this.numWILD = numWILD;
    }

    public int getNumWIZARD() {
        return numWIZARD;
    }

    public void setNumWIZARD(int numWIZARD) {
        this.numWIZARD = numWIZARD;
    }

    public int getMostFromOneType() {
        return mostFromOneType;
    }

    public void setMostFromOneType(int mostFromOneType) {
        this.mostFromOneType = mostFromOneType;
    }

    public int getNumTypes() {
        return numTypes;
    }

    public void setNumTypes(int numTypes) {
        this.numTypes = numTypes;
    }

    public int getNumPointsLost() {
        return numPointsLost;
    }

    public void setNumPointsLost(int numPointsLost) {
        this.numPointsLost = numPointsLost;
    }

    public int getNumOdd() {
        return numOdd;
    }

    public void setNumOdd(int numOdd) {
        this.numOdd = numOdd;
    }

    public int getShapeshifter() {
        return shapeshifter;
    }

    public void setShapeshifter(int shapeshifter) {
        this.shapeshifter = shapeshifter;
    }

    public boolean[] getKingdom() {
        return kingdom;
    }

    public void setKingdom(boolean[] kingdom) {
        this.kingdom = kingdom;
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

    public boolean[] getArmory() {
        return armory;
    }

    public void setArmory(boolean[] armory) {
        this.armory = armory;
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
