package artificialintelligence;

import server.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Used for putting and getting values of card combinations to save time on computing them over and over again.
 * @author Tereza Miklóšová
 */
public class CacheMap {
    /**
     * The cache for hand full of ids of cards and their score.
     */
    Map<List<Integer>,Integer> cache;

    /**
     * Constructor for the cache.
     */
    public CacheMap(){
        cache = new HashMap<>();
    }

    /**
     * Clears the cache. Used before playing a new game if in loop of experiments.
     */
    public void clear(){
        this.cache.clear();
    }

    /**
     * Get the size of the cache.
     * @return The size of the cache.
     */
    public int size(){
        return this.cache.size();
    }

    /**
     * Puts a key and its value to the map.
     * @param hand The ids of cards on hand in List.
     * @param score Score of the hand.
     */
    public void putValue(List<Card> hand, Integer score){
        List<Integer> ids = hand.stream().map(Card::getId).sorted().collect(Collectors.toList());
        cache.put(ids, score);
        manageCache();
    }

    /**
     * Provides the score for given hand.
     * @param hand Ids of the cards in hand.
     * @return Returns score if the hand is known. If not, returns -999.
     */
    public int getValue(List<Card> hand){
        List<Integer> ids = hand.stream().map(Card::getId).sorted().collect(Collectors.toList());
        return cache.getOrDefault(ids, -999);
    }

    /**
     * Deletes the cached values that are not needed anymore. The exceptions are the cards that take LONG time to compute.
     * Those are kept in case the value is needed in the future.
     */
    private void manageCache(){
        ArrayList<List<Integer>> toRemove = new ArrayList<>();
        if(cache.size() > 500){
            for(Map.Entry<List<Integer>,Integer> entry : cache.entrySet()){
                if(!entry.getKey().contains(31) || !entry.getKey().contains(52) || !entry.getKey().contains(53)){
                    toRemove.add(entry.getKey());
                }
            }
            cache.keySet().removeAll(toRemove);
        }
    }

    private void manageCache(ArrayList<Card> cardsOnTable, Card toBePutOnTable, ArrayList<Card> hand){
        ArrayList<List<Integer>> toRemove = new ArrayList<>();
        for(Map.Entry<List<Integer>, Integer> entry : cache.entrySet()){
            List<Integer> key = entry.getKey();
            for(Integer id : key){
                if(id == 31) {
                    if (hand.stream().noneMatch(card -> id.equals(card.getId())) &&
                            cardsOnTable.stream().noneMatch(card -> id.equals(card.getId())) && !id.equals(toBePutOnTable.getId())) {
                        toRemove.add(key);
                        break;
                    }
                    toRemove.remove(key);
                    break;
                }
            }
        }
        cache.keySet().removeAll(toRemove);
    }

    /**
     * The keys containing necromancer card are deleted from the cache because the necromancer card needs to be computed
     * again every round as the table has changed.
     */
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
