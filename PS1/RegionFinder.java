import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 * @author Paige Harris and Cha Krupka, Dartmouth CS10, Spring 2022
 * Finished findRegions(),  colorMatch(), largestRegion(), and recolorImage() methods
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;                // how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50;                // how many points in a region to be worth considering
	private Color trackColor=null;		 	// point-tracking target color

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;            // a region is a list of points
	// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Loops over pixels to find pixels within certain scope of color accuracy, then loops over neighboring
	 * pixels to create a region of correct color pixels, adding the region to the list of regions if the
	 * region meets the size criteria.
	 * @param targetColor color to be searched for in image
	 */
	public void findRegions(Color targetColor) {
		regions = new ArrayList<ArrayList<Point>>();
		BufferedImage visited = new BufferedImage
				(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < image.getHeight(); y++) {		// loop over all pixels
			for (int x = 0; x < image.getWidth(); x++) {
				if (visited.getRGB(x, y) == 0) {			// if pixel isn't visited, continue

					if (colorMatch(new Color(image.getRGB(x, y)), targetColor)) {	// compared color to targetColor
						ArrayList<Point> region = new ArrayList<Point>();	// create new region
						ArrayList<Point> toVisit = new ArrayList<Point>();	// create a list of pixels to visit

						toVisit.add(new Point(x, y));

						while (!toVisit.isEmpty()) {		// while there are pixels to visit, continue loop
							Point removePoint = toVisit.remove(0);	// remove current pixel

							if (visited.getRGB(removePoint.x, removePoint.y) != 1) {
								region.add(new Point(removePoint.x, removePoint.y)); // if not visited, add to region
								visited.setRGB(removePoint.x, removePoint.y, 1); // then set visited

								// loop over neighboring pixels
								for (int cy = Math.max(0, removePoint.y - 1); cy <= Math.min(image.getHeight()-1,
										removePoint.y + 1); cy++) {
									for (int cx = Math.max(0, removePoint.x - 1); cx <= Math.min(image.getWidth()-1,
											removePoint.x + 1); cx++) {
										// add neighboring pixels to toVisit if colors are correct
										if (colorMatch(new Color(image.getRGB(cx, cy)), targetColor)) {
											toVisit.add(new Point(cx, cy));
										}
									}
								}
							}
						}
						if (region.size() >= minRegion) {	// add region to regions if meets size requirements
							regions.add(region);
						}
					}
				}
			}
		}
	}

	/**
	 * Tests whether the two colors are "similar enough."
	 * Subject to the maxColorDiff threshold
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		int d = (c1.getRed() - c2.getRed()) * (c1.getRed() - c2.getRed())
				+ (c1.getGreen() - c2.getGreen()) * (c1.getGreen() - c2.getGreen())
				+ (c1.getBlue() - c2.getBlue()) * (c1.getBlue() - c2.getBlue());

		return Math.sqrt(d) <= maxColorDiff;
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		if (regions.size() > 0) {
			ArrayList<Point> currMax = regions.get(0);
			for (ArrayList<Point> region : regions) {
				if (region.size() > currMax.size()) currMax = region;
			}
			return currMax;
		}
		else return null;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null),
				image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		for (ArrayList<Point> region : regions) {
			int randColor = (int) (16777216 * Math.random());
			for (Point i : region) {
				recoloredImage.setRGB(i.x, i.y, randColor);
			}
		}
	}
}