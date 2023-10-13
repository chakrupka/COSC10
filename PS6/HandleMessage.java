import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Handle messages between client and server by parsing data (sketch or shape) into a string, or vice versa
 *
 * @author Cha Krupka and Paige Harris, Dartmouth CS10, Spring 2022
 */
public class HandleMessage {
    /**
     * Takes information from a Sketch (the map of shapes and IDs) and converts the information into a String
     * @param sketchMap map of shapes and IDs from a Sketch class
     * @return Sketch information represented as a String
     */
    public static String sketchToStr(TreeMap<Integer, Shape> sketchMap) {
        StringBuilder stringMap = new StringBuilder(); // create new String (builder)
        for (Map.Entry<Integer, Shape> sMap : sketchMap.entrySet()) {   // go through Map
            stringMap.append(sMap.getKey()).append(":").append(sMap.getValue().toString()).append(";"); // append info
        }
        return stringMap.toString();
    }

    /**
     * Takes a String and converts the information into a map of shapes and IDs for a Sketch
     * @param stringMap String with information of shapes and IDs
     * @return Sketch information (the map of shapes and IDs)
     */
    public static Sketch strToSketch(String stringMap) {
        TreeMap<Integer, Shape> sketchMap = new TreeMap<>();    // create new Map
        String[] strShapes = stringMap.split(";");       // split the string into shapes

        for (String s : strShapes) {                            // go through string shapes
            String[] shapeInfo = s.split(":");            // split to get shape ID and shape
            sketchMap.put(Integer.parseInt(shapeInfo[0]), HandleMessage.strToShape(shapeInfo[1])); // add to map
        }

        return new Sketch(sketchMap.size(), sketchMap); // return the map
    }

    /**
     * Takes a string and converts it to a shape
     * @param shapeStr string of shape information
     * @return a Shape
     */
    public static Shape strToShape(String shapeStr) {
        String[] shapeInfo = shapeStr.split(" ");   // split up data in string
        int x1 = Integer.parseInt(shapeInfo[1]);
        int y1 = Integer.parseInt(shapeInfo[2]);
        int x2 = Integer.parseInt(shapeInfo[3]);
        int y2 = Integer.parseInt(shapeInfo[4]);
        Color color = new Color(Integer.parseInt(shapeInfo[5]));

        switch (shapeInfo[0]) {
            case "ellipse" -> {
                return new Ellipse(x1, y1, x2, y2, color);
            }
            case "rectangle" -> {
                return new Rectangle(x1, y1, x2, y2, color);
            }
            case "segment" -> {
                return new Segment(x1, y1, x2, y2, color);
            }
            case "polyline" -> {
                ArrayList<Point> points = new ArrayList<>();
                color = new Color(Integer.parseInt(shapeInfo[shapeInfo.length - 1]));
                for (int i = 1; i < shapeInfo.length - 2; i+=2) {
                    points.add(new Point(Integer.parseInt(shapeInfo[i]), Integer.parseInt(shapeInfo[i + 1])));
                }
                return new Polyline(points, color);
            }
        }
        return null;
    }
}
