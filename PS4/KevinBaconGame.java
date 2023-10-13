import java.io.IOException;
import java.util.*;



/**
 * @author Cha Krupka and Paige Harris, Dartmouth CS10, Spring 2022
 * Creates a Kevin Bacon game, along with additional functionality
 */
public class KevinBaconGame {
    Graph<String, Set<String>> graphAM;
    Graph<String, Set<String>> currentPath;
    Set<String> missingVerts;
    double avgSep;
    String source;
    boolean quitGame;

    /**
     * Local class to hold data for use in calculating / sorting by average separation of degree
     */
    class Actor {
        private final String name;
        private double sep;
        private Set<String> coList;

        /**
         * @param name Actor's name
         * @param sep Average separation when they are center
         */
        private Actor(String name, double sep) {
            this.name = name;
            this.sep = sep;
        }

        /**
         * @param name Actor's name
         * @param coList list of costars
         */
        private Actor(String name, Set<String> coList) {
            this.name = name;
            this.coList = coList;
        }
    }

    /**
     * Runs the game
     * @throws IOException
     */
    public KevinBaconGame() throws IOException {
        source = "Kevin Bacon";
        mainGame();
        quitGame = false;
    }

    /**
     * Set the game to the inputted actor
     * @param s Centered actor
     * @throws IOException
     */
    public void gameSetter(String s) throws IOException {
        graphAM = CostarGraph.makeGraph();                                  // make the graph of costars
        currentPath = GraphLibrary.bfs(graphAM, s);                         // create the shortest path graph for actor
        missingVerts = GraphLibrary.missingVertices(graphAM, currentPath);  // find the missing vertices
        avgSep = GraphLibrary.averageSeparation(currentPath, s);            // calculate the average separation

        System.out.println(source+" is now the center of the acting universe, connected to "+currentPath.numVertices()+
                "/9235 actors with an average separation of "+avgSep);
    }

    /**
     * Display the commands available
     */
    public void listCommands() {
        System.out.println("c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, " +
                "sorted by average separation\n" +
                "d <low> <high>: list actors sorted by degree, with degree between low and high\n" +
                "n: number of actors in the connected universe\n" +
                "p <name>: find path from <name> to current center of the universe\n" +
                "u <name>: make <name> the center of the universe\n" +
                "q: quit game\n" +
                "h: display commands");
    }

    /**
     * Run the main game
     * @throws IOException
     */
    public void mainGame() throws IOException {
        Scanner in = new Scanner(System.in);                // create scanner to accept console input
        listCommands();                                     // display commands
        System.out.print("\n");
        gameSetter(source);                                 // set game to source

        while (!quitGame) {                                 // run through game commands
            System.out.print("\n"+source + " game >");
            String line = in.nextLine();
            if (checkChars(line)) {                         // make sure no special characters are entered
                if (line.charAt(0) == 'u') {                // run commands if char matches
                    optionU(line);

                } else if (line.charAt(0) == 'h') {
                    listCommands();

                } else if (line.charAt(0) == 'd') {
                    optionD(line);

                } else if (line.charAt(0) == 'n') {
                    optionN();

                } else if (line.charAt(0) == 'p') {
                    optionP(line);

                } else if (line.charAt(0) == 'c') {
                    optionC(line);

                } else if (line.charAt(0) == 'q') {
                    optionQ();
                }
            }
        }
    }

    /**
     * Checks characters to make sure no special characters are inputted
     * @param line
     * @return
     */
    private boolean checkChars(String line) {
        // basic list of common keyboard special characters
        ArrayList<String> specialChars = new ArrayList<>(Arrays.asList("!", "@", "#", "$", "%", "^", "&", "*",
            "(", ")", "_", "=", "{", "}", "[", "]", "|", "\\", ";", ":", "?", "/", ".", ",", "<", ">"));
        for (String sChar : specialChars) {
            if (line.contains(sChar)) {
                System.out.println("Please only enter valid characters");
                return false;
            }
        }
        return true;
    }

    /**
     * Quit game command
     */
    private void optionQ() {
        System.out.println("Quitting game...");
        quitGame = true;
    }

    /**
     * List top or bottom centers of universe by average separation
     * @param line
     */
    private void optionC(String line) {
        // parse line
        String lineInput = line.replaceFirst("c ", "");
        // try to get sorted list
        try {
            // create new list and get graph of only actors connected to Kevin Bacon
            List<Actor> sortedAvgSep = new ArrayList<>();
            currentPath = GraphLibrary.bfs(graphAM, "Kevin Bacon");

            // go through Kevin Bacon connected actors
            for (String actor : currentPath.vertices()) {

                // create a new path from a neighbor and calculate separation, adding to list
                Graph<String, Set<String>> currentPathTemp = GraphLibrary.bfs(graphAM, actor);
                avgSep = GraphLibrary.averageSeparation(currentPathTemp, actor);
                sortedAvgSep.add(new Actor(actor, avgSep));
            }

            // sort the list
            sortedAvgSep.sort(Comparator.comparingDouble((Actor a) -> a.sep));

            // display only the number of actors inputted
            int i = Integer.parseInt(lineInput);
            if (i > sortedAvgSep.size()-1) i = sortedAvgSep.size()-1; //make sure number is within bounds
            else if  ((i*-1) > sortedAvgSep.size()-1) i = (sortedAvgSep.size()-1)*(-1);

            // display order depending on positive or negative input
            if (i < 0) {
                for (int c = 0; c < (i*-1); c++) {
                    Actor curr = sortedAvgSep.get(c);
                    System.out.println(curr.name+"'s average separation: "+curr.sep);
                }
            }
            if (i >= 0) {
                for (int c = sortedAvgSep.size()-1; c >= (sortedAvgSep.size()-i-1); c--) {
                    Actor curr = sortedAvgSep.get(c);
                    System.out.println(curr.name+"'s average separation: "+curr.sep);
                }
            }
        }
        // if command is improperly inputted
        catch (NumberFormatException e) {
            System.out.println("Must enter only one number after 'c'");
        }
    }

    /**
     * Find the path between the source and the inputted actor
     * @param line
     */
    private void optionP(String line) {
        String lineInput = line.replaceFirst("p ", "");
        // check to see if the actor is in the game, or if there is a path to the actor
        if (!graphAM.hasVertex(lineInput)) {
            System.out.println(lineInput+" is not in the game");
        }
        else if (graphAM.hasVertex(lineInput) && !currentPath.hasVertex(lineInput)) {
            System.out.println("There is no path between "+lineInput+" and "+source);
        }
        // if there is, display the number and the movies that connect them
        else {
            List<String> path = GraphLibrary.getPath(currentPath, lineInput);
            System.out.println(lineInput+"'s number is "+(path.size()-1));

            if (!lineInput.equals(source)) {
                for (int i = 0; i < path.size()-1; i++) {
                    System.out.println(path.get(i)+" appeared in "+
                            graphAM.getLabel(path.get(i),path.get(i+1))+" with "+path.get(i+1));
                }
            }
        }
    }

    /**
     * Display number of actors in the connected universe
     */
    private void optionN() {
        System.out.println(currentPath.numVertices()+" actors in the connected universe of "+source);
    }

    /**
     * Display number of actors sorted by number of costars
     * @param line
     */
    private void optionD(String line) {
        String lineInput = line.replaceFirst("d ", "");
        String[] bounds = lineInput.split(" ");          // split input
        try {                                                  // try to get bounds
            int lowBound = Integer.parseInt(bounds[0]);
            int highBound = Integer.parseInt(bounds[1]);

            // make sure bounds are okay
            if (lowBound > highBound) System.out.println("Error: lower bound is greater than upper bound");
            else {
                // create a new list to allow sorting, then go through vertices in costar graph
                List<Actor> sortedByDegree = new ArrayList<>();
                for (String actor : graphAM.vertices()) {
                    // create new set of costars, add all costars for each actor movie
                    Set<String> coList = new HashSet<>();
                    for (String costar : graphAM.outNeighbors(actor)) coList.add(costar);
                    sortedByDegree.add(new Actor(actor, coList));
                }
                // sort list
                sortedByDegree.sort((Actor a1, Actor a2) -> a2.coList.size() - a1.coList.size());

                // display data if degree of actor is within bounds
                for (Actor actor : sortedByDegree) {
                    if (actor.coList.size() >= lowBound && actor.coList.size() <= highBound)
                        System.out.println(actor.name + "'s degree (number of costars) is " + actor.coList.size());
                }
            }
        }

        // catch if two numbers aren't entered
        catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Must enter two numerical bounds");
            }
    }

    /**
     * Change the center of the universe
     * @param line
     * @throws IOException
     */
    private void optionU(String line) throws IOException {
        String lineInput = line.replaceFirst("u ", "");
        // check to see if they are in the game
        if (!graphAM.hasVertex(lineInput)) {
            System.out.println(lineInput+" is not in the game");
        }
        // if they are, change the source, and reset the game
        else {
            source = lineInput;
            gameSetter(source);
        }
    }

    public static void main(String[] args) throws IOException {
        KevinBaconGame game = new KevinBaconGame();
    }
}
