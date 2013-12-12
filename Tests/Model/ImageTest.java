package Model;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertArrayEquals;

/**
 * Created by sebastianzillessen on 30.11.13.
 */
public class ImageTest {
    @Test
    public void testSave() throws Exception {
        Image im = new Image(2, 2);
        im.set(1, 1, 200);
        im.set(0, 1, 100);
        im.set(0, 0, 50);
        im.set(1, 0, 0);
        assertTrue(im.save("test"));
    }

    @Test
    public void testGetHistogram() throws Exception {
        Image im = new Image(2, 2);
        im.set(1, 1, 5);
        im.set(0, 1, 3);
        im.set(0, 0, 4);
        im.set(1, 0, 2);
        int[] h = new int[256];
        h[5] = 1;
        h[3] = 1;
        h[4] = 1;
        h[2] = 1;
        assertArrayEquals(h, im.getHistogram());
    }

    @Test
    public void testGetExposureTime() throws Exception {
        testSave();
        Image im = new Image("test.png", 0.01);
        assertEquals(0.01, im.getExposureTime());
    }

    @Test
    public void testGetValue() throws Exception {
        Image im = new Image(2, 2);
        im.set(1, 1, 5);
        assertEquals(0, im.get(0, 0));
        assertEquals(0, im.get(0, 1));
        assertEquals(0, im.get(1, 0));
        assertEquals(5, im.get(1, 1));
    }

    @Test
    public void testToString() throws Exception {
        Image im = new Image(2, 2);
        assertEquals("Picture '' (   2,   2) t: -1,000000s Max:  -1 Min:  -1 Median:   0",
                im.toString());
    }

    @Test
    public void testGetMedian() throws Exception {
        Image im = new Image(2, 2);
        assertEquals(0.0, im.getMedian(), 0);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                im.set(i, j, 2);
            }
        }
        assertEquals(2.0, im.getMedian());
        im.set(0, 0, 5);
        assertEquals(2.0, im.getMedian());
        im.set(0, 1, 4);
        assertEquals(3.0, im.getMedian());
        im.set(1, 1, 6);
        assertEquals(4.5, im.getMedian());
    }

    @Test
    public void testGetImageSize() throws Exception {
        Image i = new Image(10, 20);
        assertEquals(200, i.getImageSize());
    }


    @Test
    public void testGetHeight() throws Exception {
        Image i = new Image(10, 20);
        assertEquals(10, i.getWidth());
    }

    @Test
    public void testGetWidth() throws Exception {
        Image i = new Image(10, 20);
        assertEquals(20, i.getHeight());
    }


    @Test
    public void testCopy() throws Exception {
        Image i = new Image(10, 20);
        i.set(1, 1, 100);
        Image copy = i.copy();
        assertEquals(200, copy.getImageSize());
        assertEquals(100, copy.get(1, 1));
    }

    @Test
    public void testSet() throws Exception {
        Image i = new Image(10, 20);
        i.set(1, 1, 100);
        assertEquals(100, i.get(1, 1));
    }


    @Test
    public void testAddSaltAndPepper() throws Exception {
        Image i = new Image(10, 20);
        for (int x = 0; x < i.getWidth(); x++) {
            for (int y = 0; y < i.getHeight(); y++) {
                i.set(x, y, (int) (Math.random() * 255));
            }
        }
        i.addSaltAndPepper(1.0);
        for (int x = 0; x < i.getWidth(); x++) {
            for (int y = 0; y < i.getHeight(); y++) {
                assertTrue(i.get(x, y) == 255 || i.get(x, y) == 0);
            }
        }
    }

    @Test
    public void testGetBufferedImage() throws Exception {

    }

    @Test
    public void testAddGaussian() throws Exception {
        Image i = new Image(10, 20);
        i.set(5, 5, 100);
        i.set(5, 6, 100);
        i.set(5, 7, 100);
        i.addGaussian(5);
        assertNotSame(100, i.get(5, 5));
    }

    @Test
    public void testEquals() throws Exception {
        Image i = new Image(10, 20);
        Image i2 = new Image(10, 20);
        i.set(1, 1, 100);
        i2.set(1, 1, 100);
        assertEquals(i, i2);
        assertEquals(i, i.copy());
        assertNotSame(null, i);
        assertNotSame(new Object(), i);
        assertNotSame(new Image(10, 21), i);
        i2.set(1, 1, 2);
        assertNotSame(i2, i);
    }
}
