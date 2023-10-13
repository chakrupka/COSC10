import java.util.TreeMap;

/**
 * Sketch holds shapes with respective ids, allowing access and modifications to the list through synchronized methods
 *
 * @author Cha Krupka and Paige Harris, Dartmouth CS10, Spring 2022
 */
public class Sketch {
    private int id;                             // keeps track of the id of the incoming shape
    private TreeMap<Integer, Shape> shapeMap;   // map of shapes

    /**
     * Creates a new shapeMap and sets the id to 0 for the first shape
     */
    public Sketch() {
        id = 0;
        shapeMap = new TreeMap<>();
    }

    /**
     * Create a sketch with a sketchMap that has shapes in it
     * @param id Last shape's id
     * @param shapeMap Map of shapes
     */
    public Sketch(int id, TreeMap<Integer, Shape> shapeMap) {
        this.id = id;
        this.shapeMap = shapeMap;
    }

    /**
     * Add a shape to the sketch
     * @param shape Shape to be added
     */
    public synchronized void addShape(Shape shape) {
        shapeMap.put(id, shape);
        id++;
    }

    /**
     * Update a shape in the sketch
     * @param id ID of the shape to be updated
     * @param shape Shape to be updated
     */
    public synchronized void updateShape(int id, Shape shape) {
        shapeMap.put(id, shape);
    }

    /**
     * Remove a shape from the sketch
     * @param shapeID ID of the shape to be removed
     */
    public synchronized void removeShape(int shapeID) {
        shapeMap.remove(shapeID);
    }

    /**
     * Get the list / map of shapes in the sketch
     * @return a TreeMap with IDs and respective shapes
     */
    public synchronized TreeMap<Integer, Shape> getShapeMap() {
        return shapeMap;
    }

    /**
     * Get a specific shape
     * @param id ID of the shape to be returned
     * @return Shape or null if ID is not in map
     */
    public synchronized Shape getShape(int id) {
        return shapeMap.getOrDefault(id, null);
    }
}
