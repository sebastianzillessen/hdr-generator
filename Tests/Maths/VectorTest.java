package Maths;

import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: sebastianzillessen
 * Date: 10.11.13
 * Time: 12:17
 * To change this template use File | Settings | File Templates.
 */
public class VectorTest {

    double d = 0.00001;

    @Test
    public void testSize() {
        assertEquals(5, new Vector(5).length());
    }

    @Test
    public void testInit() {
        assertEquals(3, new Vector(5, 3).get(0), d);
    }

    @Test
    public void testInitWithArray() {
        assertEquals(2, new Vector(new int[]{1, 2}).length());
        assertEquals(2, new Vector(new int[]{1, 2}).get(1), d);
    }

    @Test
    public void addVector() {
        Vector v1 = new Vector(new int[]{1, 2});
        Vector v2 = new Vector(new int[]{0, -1});
        assertEquals(1, v1.add(v2).get(0), d);
        assertEquals(1, v1.add(v2).get(1), d);
    }

    @Test
    public void addVectorInvalid() {
        Vector v1 = new Vector(new int[]{1, 2});
        Vector v2 = new Vector(new int[]{0});
        try {
            v1.add(v2);
            fail();
        } catch (Exception e) {

        }
    }

    @Test
    public void subtractVector() {
        Vector v1 = new Vector(new int[]{1, 2});
        Vector v2 = new Vector(new int[]{0, -1});
        assertEquals(1, v1.subtract(v2).get(0), d);
        assertEquals(3, v1.subtract(v2).get(1), d);
    }

    @Test
    public void subtractVectorInvalid() {
        Vector v1 = new Vector(new int[]{1, 2});
        Vector v2 = new Vector(new int[]{0});
        try {
            v1.subtract(v2);
            fail();
        } catch (Exception e) {

        }
    }

    @Test
    public void subtractDouble() {
        Vector v1 = new Vector(new int[]{1, 2});
        assertEquals(-1, v1.subtract(2).get(0), d);
        assertEquals(0, v1.subtract(2).get(1), d);
    }

    @Test
    public void addDouble() {
        Vector v1 = new Vector(new int[]{1, 2});
        assertEquals(3, v1.add(2).get(0), d);
        assertEquals(4, v1.add(2).get(1), d);
    }

    @Test
    public void abs2() {
        assertEquals(5, new Vector(new int[]{-1, 2}).abs2(), d);
    }

    @Test
    public void exp() {
        assertEquals(Math.exp(-1), new Vector(new int[]{-1, 2}).exp().get(0), d);
        assertEquals(Math.exp(2), new Vector(new int[]{-1, 2}).exp().get(1), d);
    }

    @Test
    public void equalsTest() {
        assertNotSame(new Object(), new Vector(3));
    }

    @Test
    public void equalsTest1() {
        assertNotSame(new Vector(3), new Vector(2));
    }

    @Test
    public void equalsTest2() {
        assertEquals(new Vector(3), new Vector(3));
    }

    @Test
    public void equalsTest3() {
        assertEquals(new Vector(3), new Vector(3));
    }

    @Test
    public void equalsTest4() {
        Vector v1 = new Vector(3);
        Vector v2 = new Vector(3);
        v1.set(1, 1);
        v2.set(1, 1);
        assertEquals(v1, v2);
    }

    @Test
    public void equalsTest5() {
        Vector v1 = new Vector(3);
        Vector v2 = new Vector(3);
        v1.set(1, 1);
        v2.set(2, 1);
        assertNotSame(v1, v2);
    }

    @Test
    public void copy() {
        Vector v1 = new Vector(3);
        Vector v = v1.copy();
        v1.set(1, 1);
        assertEquals(v, v1);
        v.set(1, 2);
        assertNotSame(v, v1);

    }

    @Test
    public void max() {
        Vector v1 = new Vector(3);
        v1.set(1, 2);
        assertEquals(2, v1.max(), d);
        v1.set(1, -2);
        assertEquals(0, v1.max(), d);
        v1.set(2, 2);
        assertEquals(2, v1.max(), d);
    }

    @Test
    public void min() {
        Vector v1 = new Vector(3);
        v1.set(1, 2);
        assertEquals(0, v1.min(), d);
        v1.set(1, -2);
        assertEquals(-2, v1.min(), d);
        v1.set(2, 2);
        assertEquals(-2, v1.min(), d);
    }

    @Test
    public void toFile(){
        Vector v1 = new Vector(3);
        v1.toFile("calc/test.txt");
    }


}
