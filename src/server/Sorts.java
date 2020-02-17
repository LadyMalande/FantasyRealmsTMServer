package server;

import maluses.Malus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;



public class Sorts {

    static ArrayList<Triade<Type, Malus, Integer>> list_of_nodes = new ArrayList<>();
    static ArrayList<Malus> sorted = new ArrayList<>();
    private static ArrayList<Malus> getDeleting(ClientHandler client){
        ArrayList<Malus> deleting = new ArrayList<>();
        for(Card c: client.hand){
            for(Malus m: c.maluses){
                if(m.getPriority() == 3){
                    deleting.add(m);
                }
            }
        }
        return deleting;
    }

    private static void visit(Triade<Type, Malus, Integer> triade) throws NotDAGException{
        if(triade.z.equals(2)){
            return;
        }
        if(triade.z.equals(1)){
            throw new NotDAGException(triade.x, triade.y);
        }

        triade.z = 1;
        if(triade.y.types != null) {
            for (Type t : triade.y.types) {
                for (Triade<Type, Malus, Integer> tri : list_of_nodes) {
                    if (tri.x.equals(t)) {
                        visit(tri);
                    }
                }
            }
        }
        triade.z = 2;
        sorted.add(0, triade.y);
    }

    //  algorithm implemented with help of: https://en.m.wikipedia.org/wiki/Topological_sorting Depth-first search
    public static ArrayList<Malus> topologicalSort(ClientHandler client, HashMap<Type, Malus> tm){
        sorted = new ArrayList<>();
        // Integer in triade: 0 = unmarked node, 1 temporalily marked node, 2 = permanetly marked node

        for(HashMap.Entry<Type, Malus> entry : tm.entrySet()){

            list_of_nodes.add(new Triade(entry.getKey(), entry.getValue(), 0));
        }
        while(list_of_nodes.stream().anyMatch(triade -> (triade.z.equals(0) || triade.z.equals(1)))){
            Optional<Triade<Type, Malus, Integer>> selected_node = list_of_nodes.stream().filter(tri -> tri.z.equals(0)).findAny();
            Triade<Type, Malus, Integer> selected_triade = selected_node.get();
            try {
                visit(selected_triade);
            } catch(NotDAGException ex){
                System.out.println(ex);
            }
        }
        return sorted;

    }

    public static ArrayList<Malus> getIndependentDeleting(ClientHandler client, HashMap<Type, Malus> tm){
        HashMap<Type, Boolean> typeisdeleted = new HashMap<>();
        ArrayList<Malus> deleting = getDeleting(client);
        for(HashMap.Entry<Type, Malus> entry :tm.entrySet()){
            if(!typeisdeleted.containsKey(entry.getKey())) {
                typeisdeleted.put(entry.getKey(), false);
            }
        }
        for(HashMap.Entry<Type, Malus> entry :tm.entrySet()){
            if(entry.getValue().getClass().getName().equals("DeletesAllType") || entry.getValue().getClass().getName().equals("DeletesAllTypeExceptCard")){
                for(HashMap.Entry<Type, Boolean> t :typeisdeleted.entrySet()){
                    if(entry.getValue().types.contains(t.getKey())){
                        t.setValue(true);
                    }
                }
            }
            if(entry.getValue().getClass().getName().equals("DeletesAllExceptTypeOrCard")){
                for(HashMap.Entry<Type, Boolean> t :typeisdeleted.entrySet()){
                    if(!entry.getValue().types.contains(t.getKey())){
                        t.setValue(true);
                    }
                }
            }
        }

        // Delete the cards deleted by first in topological order
        for(HashMap.Entry<Type, Boolean> t :typeisdeleted.entrySet()){
            if( t.getValue() == true && tm.containsKey(t.getKey())){
                tm.remove(t.getKey());
            }
        }
        //Which card to delete
        for(HashMap.Entry<Type, Malus> entry : tm.entrySet()){

        }

        return deleting;
    }
}
