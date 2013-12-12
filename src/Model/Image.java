package Model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Class which is used to store an image and to read it from a file resource.
 * <p/>
 * It supports only grey scale images.
 *
 * @author sebastianzillessen
 */
public class Image {
    protected String fileName = "";
    protected double exposureTime = -1;
    protected BufferedImage grayscale;
    protected int[] data;
    protected int[] histogram = new int[256];
    protected int w;
    protected int h;

    private int min = -1;
    private int max = -1;


    /**
     * Constructor to create an image which has no pixels set.
     *
     * @param w width of the image
     * @param h height of the image
     */
    public Image(int w, int h) {
        this.w = w;
        this.h = h;
        this.data = new int[w * h];
    }


    /**
     * Constructor to import a image from a file and specify a exposure time.
     *
     * @param fileName     file name
     * @param exposureTime exposure time
     * @throws Exception if the file could not be read an exception will be thrown
     */
    public Image(String fileName, double exposureTime) throws Exception {
        this.fileName = fileName;
        this.exposureTime = exposureTime;
        if (!readFile()) {
            throw new Exception("File not found");
        }
    }


    /**
     * Reads the image with the given filename.
     *
     * @return true if import succeeded
     */
    private boolean readFile() {
        try {
            BufferedImage img = ImageIO.read(new File(this.fileName));
            w = img.getWidth();
            h = img.getHeight();
            grayscale = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = grayscale.getGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            readLuminance();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * Saves an image to a file with the given file name
     *
     * @param filename the filename where to store it.
     * @return true if the image was saved.
     */
    public boolean save(String filename) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int c = data[x + y * w];
                img.setRGB(x, y, new Color(c, c, c).getRGB());
            }
        }
        if (!filename.endsWith(".png"))
            filename += ".png";
        try {
            ImageIO.write(img, "png", new File(filename));
            return true;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }
    }

    private void readLuminance() {
        data = new int[w * h];
        byte[] pixels = (byte[]) grayscale.getData().getDataElements(0, 0, w,
                h, null);
        for (int i = 0; i < w * h; i++) {
            data[i] = (int) (pixels[i] & 0xff);
        }
        updateHistogram();
    }


    /**
     * Gets the histogram of this image.
     *
     * @return histogram of the grey values.
     */
    public int[] getHistogram() {
        updateHistogram();
        return this.histogram;
    }

    /**
     * gets the exposure time of a picture.
     *
     * @return expposure time (set in constructor)
     */
    public double getExposureTime() {
        return exposureTime;
    }

    /**
     * returns grey value of this picture in a given index.
     *
     * @param i
     * @return
     */
    public int getValue(int i) {
        if (data != null && i >= 0 && i < w * h) {
            return data[i];
        } else {
            return -1;
        }
    }

    /**
     * @return string representation of this image
     */
    public String toString() {
        return String.format("Picture '%s' (%4d,%4d) t: %.6fs Max: %3d Min: %3d Median: %3.0f", fileName, w, h, exposureTime, max, min, getMedian());
    }

    /**
     * gets the median of the image.
     *
     * @return Median of the image
     */
    public double getMedian() {
        int[] sortedArray = Arrays.copyOf(data, data.length);
        Arrays.sort(sortedArray);
        double median;
        if (sortedArray.length % 2 == 0)
            median = ((double) sortedArray[sortedArray.length / 2 - 1] + (double) sortedArray[sortedArray.length / 2]) / 2;
        else
            median = (double) sortedArray[sortedArray.length / 2];
        return median;
    }

    /**
     * @return image size (width*height)
     */
    public int getImageSize() {
        return data.length;
    }

    /**
     * @return height of this image
     */
    public int getHeight() {
        return h;
    }

    /**
     * @return width of this image
     */
    public int getWidth() {
        return w;
    }

    /**
     * Copies an image
     *
     * @return exact copy of this image
     */
    public Image copy() {
        Image r = new Image(w, h);
        r.data = this.data.clone();
        r.updateHistogram();
        return r;
    }

    /**
     * sets a pixel to a given grey value
     *
     * @param x     x coordinate
     * @param y     y coordinate
     * @param value grey value
     */
    public void set(int x, int y, int value) {
        data[x + y * w] = value;
    }


    /**
     * returns a pixel value at a given point
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return greyvalue at this position
     */
    public int get(int x, int y) {
        return getValue(x + y * w);
    }

    /**
     * Recalculates the histogram
     */
    private void updateHistogram() {
        histogram = new int[256];
        if (data.length > 0) {
            max = data[0];
            min = data[0];
            for (int i = 0; i < data.length; i++) {
                histogram[data[i]] += 1;
                max = (int) Math.max(data[i], max);
                min = (int) Math.min(data[i], min);
            }
        }
    }


    /**
     * Adds salt and pepper noise in percentage pixels.
     *
     * @param percentage the percentage of pixels where to add salt and pepper noise (1 is 100%)
     */
    public void addSaltAndPepper(double percentage) {
        for (int i = 0; i < data.length; i++) {
            // should we add nois?
            if (Math.random() <= percentage) {
                if (Math.random() >= .5)
                    data[i] = 255;
                else
                    data[i] = 0;
            }
        }
        updateHistogram();
    }

    /**
     * Gets this image as Buffered image
     *
     * @return buffered image of this image
     */
    public BufferedImage getBufferedImage() {
        BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                short c = (short) get(x, y);
                bi.setRGB(x, y, new Color(c, c, c).getRGB());
            }
        }
        return bi;
    }

    /**
     * Adds additive gaussian noise
     *
     * @param devStd gauss derivate.
     */
    public void addGaussian(double devStd) {
        Random r = new Random();
        for (int i = 0; i < data.length; i++) {
            int src = data[i];
            int c = (int) (src + (devStd * (r.nextGaussian())));
            if (c < 0)
                c = 0;
            if (c > 255)
                c = 255;
            data[i] = c;
        }
        updateHistogram();
    }


    /**
     * checks if two images are exactly the same.
     *
     * @param o other image
     * @return true if the images are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || o instanceof Image) {
            Image i = (Image) o;
            if (i.getWidth() != getWidth() || i.getHeight() != getHeight() || i.getExposureTime() != getExposureTime())
                return false;
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    if (i.get(x, y) != get(x, y))
                        return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}