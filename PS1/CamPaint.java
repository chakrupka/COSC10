import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 *
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 * @author Cha Krupka and Paige Harris, Dartmouth CS10, Spring 2022:
 * Finished draw(), processImage(), and handleMousePress() methods to allow for Webcam-based drawing
 */
public class CamPaint extends Webcam {
    private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
    private RegionFinder finder;			// handles the finding
    private Color targetColor;          	// color of regions of interest (set by mouse press)
    private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
    private BufferedImage painting;			// the resulting masterpiece
    private BufferedImage recolored;		// recolored regions over webcam
    private ArrayList<Point> paint;         // holds painted pixels for painting


    /**
     * Initializes the region finder and the drawing
     */
    public CamPaint() {
        finder = new RegionFinder();
        clearPainting();
    }

    /**
     * Resets the painting to a blank image
     */
    protected void clearPainting() {
        paint = new ArrayList<Point>(); // reset painted region
        targetColor = null;             // reset targetColor
        painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);   // reset painting image
    }

    /**
     * DrawingGUI method, here drawing one of live webcam, recolored image, or painting,
     * depending on display variable ('w', 'r', or 'p')
     */
    @Override
    public void draw(Graphics g) {
        if (displayMode == 'w') {                               // display standard webcam mode, no painting
            g.drawImage(image, 0, 0, null);
        }
        if (displayMode == 'p') {
            g.drawImage(painting, 0, 0, null);   // display painting
        }
		if (displayMode == 'r') {
			g.drawImage(recolored, 0, 0, null);  // display recolored regions over webcam image
		}
    }

    /**
     * Webcam method, here finding regions and updating the painting.
     */
    @Override
    public void processImage() {
        if (paint == null) paint = new ArrayList<>();  // create new ArrayList for painted region

        if (displayMode == 'p') {  // if painting mode
            finder.setImage(image);

            if (targetColor != null) {
                finder.findRegions(targetColor);

                if (finder.largestRegion() != null) {
                    for (Point i : finder.largestRegion()) {  // add non-duped pixels to be painted from paintbrush
                        if (!paint.contains(i)) {
                            paint.add(i);
                        }
                    }
                }
            }
            for (Point i : paint) {
                painting.setRGB(i.x, i.y, paintColor.getRGB());  // paint pixels on painting
            }
        }
        if (displayMode == 'w') {  // if webcam display selected
            finder.setImage(image);

            if (targetColor != null) {
                finder.findRegions(targetColor);

                if (finder.largestRegion() != null) {
                    for (Point i : finder.largestRegion()) {            // track and make paintbrush paintColor
                        image.setRGB(i.x, i.y, paintColor.getRGB());
                    }
                }
            }
        }
        if (displayMode == 'r') {   // if recolored image selected
            recolored = image;
            finder.setImage(image);

            if (targetColor != null) {
                finder.findRegions(targetColor);
                finder.recolorImage();

                recolored = finder.getRecoloredImage();    // display region growing regions in random colors
            }
        }
    }

    /**
     * Overrides the DrawingGUI method to set the track color.
     * 'w' for standard webcam with tracked brush
     * 'p' for painting
     * 'r' for recolored image
     */
    @Override
    public void handleMousePress(int x, int y) {
        if (displayMode == 'w') {
            targetColor = new Color(image.getRGB(x, y));
            System.out.println("tracking " + targetColor);
        }
        if (displayMode == 'p') {
            targetColor = new Color(painting.getRGB(x, y));
            System.out.println("tracking " + targetColor);
        }
        if (displayMode == 'r') {
            targetColor = new Color(recolored.getRGB(x, y));
            System.out.println("tracking " + targetColor);
        }
    }

    /**
     * DrawingGUI method, here doing various drawing commands
     */
    @Override
    public void handleKeyPress(char k) {
        if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
            displayMode = k;
        }
        else if (k == 'c') { // clear
            clearPainting();
        }
        else if (k == 'o') { // save the recolored image
            saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
        }
        else if (k == 's') { // save the painting
            saveImage(painting, "pictures/painting.png", "png");
        }
        else {
            System.out.println("unexpected key "+k);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CamPaint();
            }
        });
    }
}
