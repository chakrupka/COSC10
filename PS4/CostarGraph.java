import java.io.IOException;
import java.util.*;

/**
 * @author Cha Krupka and Paige Harris, Dartmouth CS10, Spring 2022
 * Creates a graph of costars
 */
public class CostarGraph {

    /**
     * Creates a graph of costars based on maps provided by MapMaker.java
     * @return costar graph
     * @throws IOException
     */
    public static Graph<String, Set<String>> makeGraph() throws IOException {
        // get maps from MapMaker
        MapMaker maps = new MapMaker();
        Map<String, Set<String>> mapAM = maps.getActorMovieMap();
        Map<String, Set<String>> mapMA = maps.getMovieActorMap();
        Map<String, String> actorNameMap = maps.getActorMap();
        Map<String, String> movieNameMap = maps.getMovieMap();

        // create costar graph
        AdjacencyMapGraph<String, Set<String>> graph = new AdjacencyMapGraph<>();

        // go through maps
        for (Map.Entry<String, Set<String>> mapAct : mapAM.entrySet()) {
            graph.insertVertex(actorNameMap.get(mapAct.getKey()));          // insert actor into costar map
            for (String movieID : mapAct.getValue()) {                      // go through actor's movies
                for (String actorID : mapMA.get(movieID)) {                 // go through movie's actors
                    Set<String> costarMovieSet = new HashSet<>();           // create set of costar movies
                    if (!mapAct.getKey().equals(actorID)) {
                        for (String costarMovie : mapAM.get(actorID)) {     // get movie, add it to set if shared
                            if (mapAct.getValue().contains(costarMovie)) {
                                costarMovieSet.add(movieNameMap.get(costarMovie));
                            }
                        }
                        graph.insertVertex(actorNameMap.get(actorID));      // insert costar, then insert shared movies
                        graph.insertUndirected(actorNameMap.get(mapAct.getKey()), actorNameMap.get(actorID), // as edge
                                costarMovieSet);
                    }
                }
            }
        }
        return graph;
    }
}
