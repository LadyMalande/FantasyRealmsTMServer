package artificialintelligence;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Action {
    int beginningStateID;
    int endingStateID;

    public Action(int beg, int end){
        this.beginningStateID = beg;
        this.endingStateID = end;
    }

    public int getBeginningStateID() {
        return beginningStateID;
    }

    public int getEndingStateID() {
        return endingStateID;
    }

    public int getWhichIdToTake(Map<Integer,int[]> stateMap){
            Set<Integer> hand = IntStream.of(stateMap.get(beginningStateID)).boxed().collect(Collectors.toCollection(HashSet::new));
            int[] rest = IntStream.of(stateMap.get(endingStateID)).filter(value -> !hand.contains(value)).toArray();
            if(rest.length == 0){
                return -1;
            } else{
                return rest[0];
            }
    }

    public int getWhichIdToDrop(Map<Integer,int[]> stateMap){
        Set<Integer> handAtEnd = IntStream.of(stateMap.get(endingStateID)).boxed().collect(Collectors.toCollection(HashSet::new));
        int[] rest = IntStream.of(stateMap.get(beginningStateID)).filter(value -> !handAtEnd.contains(value)).toArray();
        if(rest.length == 0){
            return -1;
        } else{
            return rest[0];
        }
    }
}
