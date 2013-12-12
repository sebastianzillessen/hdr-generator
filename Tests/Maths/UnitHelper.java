package Maths;

import org.junit.Assert;

import static org.junit.Assert.assertEquals;

/**
 * Created by sebastianzillessen on 28.11.13.
 */
public class UnitHelper {
    public static void assertArrayEquals(double[][] d, double[][] e, double delta) {
        assertEquals(d.length, e.length);
        for (int i = 0; i < d.length; i++) {
            Assert.assertArrayEquals(d[i], e[i], delta);
        }
    }
}
