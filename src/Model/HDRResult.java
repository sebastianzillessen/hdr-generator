package Model;


import Maths.Vector;

/**
 * This represents a (temp.) result of the calculation of an HDR Picture.
 * It contains the radiance map E and the response curve g.
 * It is used to exchange this information asynchronously between the calculator and the GUI.
 *
 * @author sebastianzillessen
 */
public class HDRResult {

    private final Vector E;
    private final Vector g;
    private final int height;
    private final int width;


    /**
     * Default constructor
     *
     * @param E      the radiance map
     * @param g      the camera response curve
     * @param width  the width of the picture
     * @param height the height of the picture
     */
    public HDRResult(Vector E, Vector g, int width, int height) {
        if (g.length() != 256)
            throw new IndexOutOfBoundsException("G should be a vector of 256 elements!");
        this.E = E;
        this.g = g;
        this.width = width;
        this.height = height;
    }

    /**
     * returns the camera response curve
     *
     * @return currently estimated camera response curve
     */
    public Vector getG() {
        return g;
    }

    /**
     * returns radiance map.
     *
     * @return the currently calculated radiance map
     */
    public Vector getE() {
        return E;
    }

    /**
     * Width of the HDRI
     *
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Height of the HDRI
     *
     * @return height
     */
    public int getHeight() {
        return height;
    }
}
