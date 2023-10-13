import java.awt.*;
import java.util.ArrayList;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 * @author Cha Krupka and Paige Harris, Dartmouth CS10, Spring 2022, Finished method implementations.
 */
public class Polyline implements Shape {
	private ArrayList<Point> points = new ArrayList<>();
	private Color color;

	// initial polyline
	public Polyline(Point p1, Point p2, Color color) {
		this.color = color;
		points.add(p1);
		points.add(p2);
	}

	// polyline with a list of points
	public Polyline(ArrayList<Point> points, Color color) {
		this.color = color;
		this.points = points;
	}

	// run through list of points and move them to move polyline
	@Override
	public void moveBy(int dx, int dy) {
		for (int i = 0; i < points.size(); i++) {
			points.set(i, new Point(points.get(i).x+dx, points.get(i).y+dy));
		}
	}

	// get color
	@Override
	public Color getColor() {
		return color;
	}

	// set color
	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	// run through segments of points and check to see if clicked on
	@Override
	public boolean contains(int x, int y) {
		for (int i = 0; i < points.size()-1; i++) {
			if (Segment.pointToSegmentDistance(x, y, points.get(i).x, points.get(i).y,
					points.get(i+1).x, points.get(i+1).y) < 5) return true;
		}
		return false;
	}

	// draw lines between all points in polyine to draw shape
	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		for (int i = 0; i < points.size()-1; i++) {
			g.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y);
		}
	}

	// add all points to one long string
	@Override
	public String toString() {
		StringBuilder pointsStr = new StringBuilder();
		for (Point p : points) {
			pointsStr.append(p.x).append(" ").append(p.y).append(" ");
		}
		return "polyline "+pointsStr+color.getRGB();
	}
}
