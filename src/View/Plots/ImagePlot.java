package View.Plots;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Plot to display an image.
 *
 * @author sebastianzillessen
 * @see Model.Image
 */
public class ImagePlot extends Plot {

    private final int height;
    private final int width;
    private Model.Image image;
    BufferedImage bi = null;

    Thread generating;
    private Runnable runner = new Runnable() {
        @Override
        public void run() {
            bi = image.getBufferedImage();
        }
    };


    /**
     * Creates a new image plot with given image. Starts generation in background
     *
     * @param image the image to display.
     */
    public ImagePlot(Model.Image image) {
        super();
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        startImageCalculation();
    }

    /**
     * updates the image of the plot
     *
     * @param image
     */
    public void setImage(Model.Image image) {
        this.image = image;
        startImageCalculation();
    }


    /**
     * Stores the current displayed image
     *
     * @param filename
     * @return true if saved
     */
    @Override
    public boolean saveGraphic(String filename) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(bi.getScaledInstance(image.getWidth(), image.getHeight(), Image.SCALE_REPLICATE), 0, 0, null);
        return saveGraphic(filename, bufferedImage);
    }


    @Override
    protected void redraw() {
        bi = null;
        startImageCalculation();
        super.redraw();
    }

    @Override
    protected void paintPlot(Graphics g) {
        if (bi == null) {
            g.drawString("Image processing in Progress", 100, 100);
            if (bi == null) {
                this.startImageCalculation();
                try {
                    generating.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            g.clearRect(100, 100, 300, 50);
        }
        double w = getWidth() * 1.0 / image.getWidth();
        double h = getHeight() * 1.0 / image.getHeight();
        //if (w < 1 || h < 1) {
        double d = Math.min(Math.min(w, h), 1);
        g.drawImage(bi.getScaledInstance((int) (d * image.getWidth()), (int) (d * image.getHeight()), Image.SCALE_REPLICATE), 0, 0, null);
    }


    private void startImageCalculation() {
        if (generating == null || !generating.isAlive()) {
            generating = new Thread(runner);
            generating.start();
        }
    }

}
