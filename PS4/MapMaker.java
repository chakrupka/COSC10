import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author Cha Krupka and Paige Harris, Dartmouth CS10, Spring 2022
 * Creates maps of actors/ids, movies/ids, actors/movies and movies/actors from input files
 */
public class MapMaker {
    private Map<String, String> actorMap;
    private Map<String, String> movieMap;
    private Map<String, Set<String>> actorMovieMap;
    private Map<String, Set<String>> movieActorMap;

    /**
     * Calls makeMaps() to create the maps
     * @throws IOException
     */
    public MapMaker() throws IOException {
        makeMaps();
    }

    /**
     * Creates the respective maps
     * @throws IOException
     */
    public void makeMaps() throws IOException {
        actorMap = makeIDMap("inputs/actors.txt");
        movieMap = makeIDMap("inputs/movies.txt");
        movieActorMap = makeIDtoIDMaps("inputs/movie-actors.txt").get(0);
        actorMovieMap = makeIDtoIDMaps("inputs/movie-actors.txt").get(1);
    }

    /**
     * Parses data from input file to create a map that holds an id value and the name of an actor or a movie
     * @param file input file
     * @return IDMap
     * @throws IOException
     */
    public Map<String, String> makeIDMap(String file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file)); // open the file and create a new map
        Map<String, String> IDMap = new TreeMap<>();

        String line;
        while ((line = in.readLine()) != null) {           // loop over words in line, adding to map
            String[] words = line.split("\\|");      // split the line
            IDMap.put(words[0], words[1]);
        }

        in.close();         // close file and return map
        return IDMap;
    }

    /**
     * Maps a movie's ID to its actors ID, or an actor's ID to their movies' IDs and puts the two maps in a list
     * @param file input file
     * @return a list containing two maps
     * @throws IOException
     */
    public List<Map<String, Set<String>>> makeIDtoIDMaps(String file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file)); // open the file
        List<Map<String, Set<String>>> IDList = new ArrayList<>();    // create a new list for the two maps
        Map<String, Set<String>> movieToActorMap = new TreeMap<>();   // create both maps
        Map<String, Set<String>> actorToMovieMap = new TreeMap<>();

        String line;  // parse through the file data line by line
        while ((line = in.readLine()) != null) {
            String[] words = line.split("\\|"); // split the line, then add to the two maps with the data flipped
            if (!actorToMovieMap.containsKey(words[1])) actorToMovieMap.put(words[1], new HashSet<>());
            actorToMovieMap.get(words[1]).add(words[0]);

            if (!movieToActorMap.containsKey(words[0])) movieToActorMap.put(words[0], new HashSet<>());
            movieToActorMap.get(words[0]).add(words[1]);
        }
        in.close(); // close the file

        // add the two movies to the list
        IDList.add(movieToActorMap);
        IDList.add(actorToMovieMap);
        return IDList;
    }

    public Map<String, String> getActorMap() {
        return actorMap;
    }

    public Map<String, String> getMovieMap() {
        return movieMap;
    }

    public Map<String, Set<String>> getActorMovieMap() {
        return actorMovieMap;
    }

    public Map<String, Set<String>> getMovieActorMap() {
        return movieActorMap;
    }
}
