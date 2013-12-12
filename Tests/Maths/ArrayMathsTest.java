package Maths;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: sebastianzillessen
 * Date: 10.11.13
 * Time: 12:17
 * To change this template use File | Settings | File Templates.
 */
public class ArrayMathsTest {

    double d = 0.00001;

    @Test
    public void testIntToDouble() {
        assertEquals(1.0, ArrayMaths.intToDouble(new int[]{1})[0]);
        assertEquals(1.0, ArrayMaths.intToDouble(new int[]{1, 2, 3})[0]);
        assertEquals(2.0, ArrayMaths.intToDouble(new int[]{1, 2, 3})[1]);
        assertEquals(3.0, ArrayMaths.intToDouble(new int[]{1, 2, 3})[2]);
    }

    @Test
    public void testDiffMax() {
        double[] a = new double[]{3, 9, 1, 2};
        double[] b = new double[]{2, -9, 2, 4};
        assertEquals(18, ArrayMaths.diffMax(a, b), 0.0);
    }

    @Test
    public void testDiffMaxFail() {
        double[] a = new double[]{3, 9, 1, 2};
        double[] b = new double[]{2, -9, 2};
        try{
            ArrayMaths.diffMax(a,b);
            fail();
        }
        catch(Exception e){

        }
    }




}
