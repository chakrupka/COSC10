import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 * @author Paige Harris, Cha Krupka, Dartmouth CS10, April 2022
 * Finished insert(), size(), allPoints(), and findInCircle(). Added findInCircleHelper() and allPointsHelper()
 */
public class PointQuadtree<E extends Point2D> {

	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters

	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 * @param p2 point to be added
	 */
	public void insert(E p2) {
		// if point is in quadrant 1
		if (p2.getX() >= getPoint().getX() && p2.getY() <= getPoint().getY()) {
			if (hasChild(1)) {
				c1.insert(p2);
			}
			else {
			c1 = new PointQuadtree<>(p2, (int) getPoint().getX(), getY1(), getX2(), (int) getPoint().getY());
			}
		}
		// quadrant 2
		else if (p2.getX() < getPoint().getX() && p2.getY() < getPoint().getY()) {
			if (hasChild(2)) {
				c2.insert(p2);
			}
			else {
				c2 = new PointQuadtree<>(p2, getX1(), getY1(), (int) getPoint().getX(), (int) getPoint().getY());
			}
		}
		// quadrant 3
		else if (p2.getX() <= getPoint().getX() && p2.getY() >= getPoint().getY()) {
			if (hasChild(3)) {
				c3.insert(p2);
			}
			else {
				c3 = new PointQuadtree<>(p2, getX1(), (int) getPoint().getY(), (int) getPoint().getX(), getY2());
			}
		}
		// quadrant 4
		else if (p2.getX() > getPoint().getX() && p2.getY() > getPoint().getY()) {
			if (hasChild(4)) {
				c4.insert(p2);
			}
			else {
				c4 = new PointQuadtree<>(p2, (int) getPoint().getX(), (int) getPoint().getY(), getX2(), getY2());
			}
		}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 * @return number of points
	 */
	public int size() {
		int num = 1;
		if (hasChild(1)) num += c1.size();
		if (hasChild(2)) num += c2.size();
		if (hasChild(3)) num += c3.size();
		if (hasChild(4)) num += c4.size();
		return num;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 * @return list
	 */
	public List<E> allPoints() {
		List<E> pointList = new ArrayList<E>();
		allPointsHelper(pointList);

		return pointList;
	}	

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		List<E> circlePoints = new ArrayList<>();
		findInCircleHelper(cx, cy, cr, circlePoints);

		return circlePoints;
	}

	/**
	 * Helper method for allPoints
	 * @param list list of points
	 */
	private void allPointsHelper(List<E> list) {
		if (!(hasChild(1)) && !(hasChild(2)) && !(hasChild(3)) && !(hasChild(4))) {
			list.add(point);
		}
		else {
			if (hasChild(1)) c1.allPointsHelper(list);
			if (hasChild(2)) c2.allPointsHelper(list);
			if (hasChild(3)) c3.allPointsHelper(list);
			if (hasChild(4)) c4.allPointsHelper(list);
			list.add(point);
		}
	}

	/**
	 * Helper method for findInCircle
	 * @param cx circle center x
	 * @param cy circle center y
	 * @param cr circle radius
	 * @param list list of points in circle
	 */
	public void findInCircleHelper(double cx, double cy, double cr, List<E> list) {
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, getX1(), getY1(), getX2(), getY2())) {
			if (Geometry.pointInCircle(getPoint().getX(), getPoint().getY(), cx, cy, cr)) {
				list.add(point);
			}
			if (hasChild(1)) c1.findInCircleHelper(cx, cy, cr, list);
			if (hasChild(2)) c2.findInCircleHelper(cx, cy, cr, list);
			if (hasChild(3)) c3.findInCircleHelper(cx, cy, cr, list);
			if (hasChild(4)) c4.findInCircleHelper(cx, cy, cr, list);
		}
	}
}