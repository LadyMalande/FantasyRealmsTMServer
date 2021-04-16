package artificialintelligence;

import java.util.Arrays;

public class State {
    int ID;
    private int[] cardsIDs;

    public State(int id, int[] arr){
        this.ID = id;
        this.cardsIDs = Arrays.copyOf(arr, arr.length);
    }

    public boolean equals(int[] other){
        Arrays.sort(other);
        return Arrays.equals(cardsIDs, other);
    }

}
