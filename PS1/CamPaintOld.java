//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.util.ArrayList;
//
//import javax.swing.*;
//
///**
// * Webcam-based drawing
// * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
// *
// * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
// */
//public class CamPaintOld extends Webcam {
//	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
//	private RegionFinder finder;			// handles the finding
//	private Color targetColor;          	// color of regions of interest (set by mouse press)
//	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
//	private BufferedImage painting;			// the resulting masterpiece
//	private BufferedImage recolored;		// test
//
//
//	/**
//	 * Initializes the region finder and the drawing
//	 */
//	public CamPaintOld() {
//		finder = new RegionFinder();
//		clearPainting();
//	}
//
//	/**
//	 * Resets the painting to a blank image
//	 */
//	protected void clearPainting() {
//		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//	}
//
//	/**
//	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting,
//	 * depending on display variable ('w', 'r', or 'p')
//	 */
//	@Override
//	public void draw(Graphics g) {
//		// TODO: YOUR CODE HERE
//		if (displayMode == 'w') {
//			g.drawImage(image, 0, 0, null);
//		}
//		if (displayMode == 'p') {		// maybe change this to draw finder.getImage()
//			g.drawImage(painting, 0, 0, null);
//		}
////		if (displayMode == 'r') {
////			finder.setImage(loadImage("pictures/baker.jpg"));
////			finder.findRegions(targetColor);
////			finder.recolorImage();
////			recolored = finder.getRecoloredImage();
////			g.drawImage(recolored, 0, 0, null);
////		}
//	}
//
//	/**
//	 * Webcam method, here finding regions and updating the painting.
//	 */
//	@Override
//	public void processImage() {
//		// TODO: YOUR CODE HERE
//		if (targetColor != null) {
//			finder.setImage(image);
//			finder.findRegions(targetColor);
//			painting = finder.getImage();
//			ArrayList<Point> paint = new ArrayList<>();
//			if (finder.largestRegion() != null) {
//				for (Point i : finder.largestRegion()) {
//					if (!paint.contains(i)) {
//						paint.add(i);
//					}
//				}
//				for (Point i : paint) {
//					painting.setRGB(i.x, i.y, paintColor.getRGB());
//				}
//			}
//		}
//	}
//
//	/**
//	 * Overrides the DrawingGUI method to set the track color.
//	 */
//	@Override
//	public void handleMousePress(int x, int y) {
//		// TODO: YOUR CODE HERE
//		if (displayMode == 'w') {
//			targetColor = new Color(image.getRGB(x, y));
//			System.out.println("tracking " + targetColor);
//		}
//		if (displayMode == 'p') {
//			targetColor = new Color(painting.getRGB(x, y));
//			System.out.println("tracking " + targetColor);
//		}
//		if (displayMode == 'r') {
//			targetColor = new Color(recolored.getRGB(x, y));
//			System.out.println("tracking " + targetColor);
//		}
//	}
//
//	/**
//	 * DrawingGUI method, here doing various drawing commands
//	 */
//	@Override
//	public void handleKeyPress(char k) {
//		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
//			displayMode = k;
//		}
//		else if (k == 'c') { // clear
//			clearPainting();
//		}
//		else if (k == 'o') { // save the recolored image
//			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
//		}
//		else if (k == 's') { // save the painting
//			saveImage(painting, "pictures/painting.png", "png");
//		}
//		else if (k == 't') { // save the recolored
//			saveImage(image, "pictures/webcamimage.png", "png");
//		}
//		else {
//			System.out.println("unexpected key "+k);
//		}
//	}
//
//	public static void main(String[] args) {
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				new CamPaintOld();
//			}
//		});
//	}
//}
