import java.io.IOException;
import java.util.*;

/**
 * @author Cha Krupka and Paige Harris, Dartmouth CS10, Spring 2022
 * Allows for certain graph functions such as Breadth-First Search, getting a path to a vertex, and more
 * @param <V> Vertex type
 * @param <E> Edge type
 */
public class GraphLibrary<V,E> {

    /**
     * @param g graph to search
     * @param source vertex to start at
     * @return shortest-path tree (graph)
     * @param <V> vertex type
     * @param <E> edge type
     */
    public static <V,E> Graph<V,E> bfs(Graph<V, E> g, V source) {
        Graph<V,E> pathTree = new AdjacencyMapGraph<V,E>(); //initialize backTrack
        pathTree.insertVertex(source);
        Set<V> visited = new HashSet<V>(); //Set to track which vertices have already been visited
        Queue<V> queue = new LinkedList<V>(); //queue to implement BFS

        queue.add(source); //enqueue start vertex
        visited.add(source); //add start to visited Set
        while (!queue.isEmpty()) { //loop until no more vertices
            V u = queue.remove(); //dequeue
            pathTree.insertVertex(u);
            for (V v : g.outNeighbors(u)) { //loop over out neighbors
                if (!visited.contains(v)) { //if neighbor not visited, then neighbor is discovered from this vertex
                    visited.add(v); //add neighbor to visited Set
                    queue.add(v); //enqueue neighbor
                    pathTree.insertVertex(v);
                    pathTree.insertDirected(v, u, g.getLabel(v, u)); //save that this vertex was discovered from prior vertex
                }
            }
        }
        return pathTree;
    }

    /**
     * Gets a path from a shortest-path tree to the inputted vertex
     * @param tree tree to search
     * @param v vertex to search for
     * @return path
     * @param <V> vertex type
     * @param <E> edge type
     */
    public static <V,E> List<V> getPath(Graph<V, E> tree, V v) {
        // create the paths
        List<V> path = new ArrayList<>();
        Queue<V> queue = new LinkedList<V>();

        // check if the vertex is in the path
        if (!tree.hasVertex(v)) {
            return path;
        }

        // queue the vertex, while the queue isn't empty, go through neighbors appropriately
        // until the source is reached
        path.add(v);
        queue.add(v);
        while (!queue.isEmpty()) {
            V u = queue.remove();
                for (V neighbor : tree.outNeighbors(u)) {
                    path.add(neighbor);
                    queue.add(neighbor);
                }
        }
        return path;
    }

    /**
     * Creates a set of vertices that are in the main graph but not in the second graph
     * @param graph Main graph
     * @param subgraph shortest-path graph
     * @return set of missing vertices
     * @param <V> vertex type
     * @param <E> edge type
     */
    public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph) {
        Set<V> missingVerts = new HashSet<>();                           // create new set
        Set<V> verts = new HashSet<>();                                  // create set of main graph vertices
        for (V subVert : subgraph.vertices()) verts.add(subVert);
        for (V vert : graph.vertices()) {                                // if missing, add to set
            if (!verts.contains(vert)) {
                missingVerts.add(vert);
            }
        }
        return missingVerts;
    }

    /**
     * Calculate the average separation of node in a tree
     * @param tree graph
     * @param root starting point
     * @return average separation (double)
     * @param <V> vertex type
     * @param <E> edge type
     */
    public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {
        // make a list to keep track of values and how many
        double totalSum = 0;
        ArrayList<Integer> sums = new ArrayList<>();
        avgSepHelper(tree, root, sums, 0); // call on helped function for recursion

        // add up totals
        for (int i : sums) {
            totalSum += i;
        }
        if (sums.isEmpty()) return 0;
        return totalSum / (sums.size()); // return average
    }

    /**
     * Helper function for averageSeparation for recursive moving through the tree
     * @param tree graph
     * @param root starting node
     * @param sumList list of lengths
     * @param currSum current sum of length
     * @param <V> vertex type
     * @param <E> edge type
     */
    public static <V,E> void avgSepHelper(Graph<V,E> tree, V root, ArrayList<Integer> sumList, int currSum) {
        // check if there are any more nodes to reach, else recurse through the neighbor and add to currSUm
        if (tree.inDegree(root)==0) sumList.add(currSum);
        else {
            sumList.add(currSum);
            for (V vert : tree.inNeighbors(root)) {
//                System.out.println("Root: "+root+" | currSum: "+currSum+" | Next: "+vert);
                avgSepHelper(tree, vert, sumList, currSum+1);
            }
        }
    }
}
