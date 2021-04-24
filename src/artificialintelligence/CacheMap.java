package artificialintelligence;

import server.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CacheMap {
    Map<List<Integer>,Integer> cache;
    public CacheMap(){
        cache = new HashMap<>();
    }

    public void putValue(List<Card> hand, Integer score){
        List<Integer> ids = hand.stream().map(Card::getId).sorted().collect(Collectors.toList());
        cache.put(ids, score);
        manageCache();
    }

    public int getValue(List<Card> hand){
        List<Integer> ids = hand.stream().map(Card::getId).sorted().collect(Collectors.toList());
        return cache.getOrDefault(ids, -999);
    }

    private void manageCache(){
        ArrayList<List<Integer>> toRemove = new ArrayList<>();
        if(cache.size() > 30000000){
            for(Map.Entry<List<Integer>,Integer> entry : cache.entrySet()){
                if(!entry.getKey().contains(31) || !entry.getKey().contains(52) || !entry.getKey().contains(53)){
                    toRemove.add(entry.getKey());
                }
            }
            cache.keySet().removeAll(toRemove);
        }
    }

    public void manageNecromancer(){
        ArrayList<List<Integer>> toRemove = new ArrayList<>();
        for(Map.Entry<List<Integer>,Integer> entry : cache.entrySet()){
            if(entry.getKey().contains(13)){
                toRemove.add(entry.getKey());
            }
        }
        cache.keySet().removeAll(toRemove);
    }
}
