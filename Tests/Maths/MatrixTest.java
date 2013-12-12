package Maths;

import org.junit.Before;
import org.junit.Test;

import static Maths.UnitHelper.assertArrayEquals;
import static junit.framework.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: sebastianzillessen
 * Date: 13.07.13
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
public class MatrixTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testMult() throws Exception {

    }

    @Test
    public void testMultDouble() throws Exception {

    }

    @Test
    public void testSetDouble() throws Exception {

    }

    @Test
    public void testCols() throws Exception {

    }

    @Test
    public void testRows() throws Exception {

    }

    private Matrix m(int n) {
        Matrix d = new Matrix(n);
        int[] basis = new int[]{1, -4, 6, -4, 1};
        // first row
        d.set(0, 0, 1);
        d.set(0, 1, -2);
        d.set(0, 2, 1);
        // second row
        d.set(1, 0, -2);
        d.set(1, 1, 5);
        d.set(1, 2, -4);
        d.set(1, 3, 1);
        // diagonale
        for (int row = 2; row < n - 2; row++) {
            // default matrixG 2nd degree on the base.
            for (int c = 0; c < 5; c++) {
                d.set(row, row + (c - 2), basis[c]);
            }
        }
        // second last row
        d.set(n - 2, n - 4, 1);
        d.set(n - 2, n - 3, -4);
        d.set(n - 2, n - 2, 5);
        d.set(n - 2, n - 1, -2);

        // last row
        d.set(n - 1, n - 3, 1);
        d.set(n - 1, n - 2, -2);
        d.set(n - 1, n - 1, 1);
        for (int row = 0; row < n; row++) {
            d.set(row, row, d.get(row, row) + 1);
        }
        return d;
    }

    @Test
    public void testDecomposePenta() {
        final int n = 7;
        Matrix d = m(n);
        AbstractMatrix[] ms = d.decomposePenta();
        AbstractMatrix L = ms[0];
        AbstractMatrix U = ms[1];
        assertArrayEquals(AbstractMatrix.parse("1 0 0 0 0 0 0\n" +
                "-1 1 0 0 0 0 0\n" +
                "0.5 -0.75 1 0 0 0 0\n" +
                "0 0.25 -0.7647059 1 0 0 0\n" +
                "0 0 0.2352941 -0.7586207 1 0 0\n" +
                "0 0 0 0.2344828 -0.7520000 1 0\n" +
                "0 0 0 0 0.2320000 -0.3750000 1").toArray(), L.toArray(), 0.001);
        assertArrayEquals(AbstractMatrix.parse("2 -2 1 0 0 0 0\n" +
                "0 4 -3 1 0 0 0\n" +
                "0 0 4.25 -3.25 1 0 0\n" +
                "0 0 0 4.264706 -3.235294 1 0\n" +
                "0 0 0 0 4.310345 -3.241379 1\n" +
                "0 0 0 0 0 3.328000 -1.248000\n" +
                "0 0 0 0 0 0 1.300000").toArray()
                , U.toArray(), 0.0001);
    }

    @Test
    public void solvePenta() {
        Matrix d = m(7);
        Vector b = new Vector(new double[]{
                4, 5, 12, 3, 1, 2, 4
        });

        Vector r = null;
        try {
            r = EquationSolver.solve(d, b, EquationSolverAlgorithm.LU);
        } catch (EquationSolverException e) {
            fail();
        }

    }


    @Test
    public void shouldFailToSolveWithSOR() {
        Matrix d = m(7);
        Vector b = new Vector(new double[]{
                4, 5, 12, 3, 1, 2, 4
        });
        try {
            EquationSolver.solve(d, b, EquationSolverAlgorithm.SOR);
            fail();
        } catch (EquationSolverException e) {
            assertTrue(e.getMessage().length() > 0);
        }
    }

    @Test
    public void mult() throws Exception {
        Matrix n = new Matrix(3);
        // 1 0 0     1 1 1      1 1 1
        // 0 2 0  *  2 2 2  =   4 4 4
        // 0 0 3     3 3 3      9 9 9

        n.set(0, 0, 1);
        n.set(1, 1, 2);
        n.set(2, 2, 3);
        Matrix m = new Matrix(3);
        Matrix r = new Matrix(3);

        for (int i = 0; i < 3; i++) {
            m.set(0, i, 1);
            m.set(1, i, 2);
            m.set(2, i, 3);
            r.set(0, i, 1);
            r.set(1, i, 4);
            r.set(2, i, 9);
        }
        System.out.println("n = " + n);
        System.out.println("m = " + m);
        System.out.println("n.mult(m) = " + n.mult(m));
        assertEquals(r, n.mult(m));

    }


    @Test
    public void add() throws Exception {
        Matrix n = new Matrix(3);
        // 1 0 0     0 0 0      1 0 0
        // 0 2 0  +  0 0 0  =   0 2 0
        // 0 0 3     0 0 0      0 3 0

        n.set(0, 0, 1);
        n.set(1, 1, 2);
        n.set(2, 2, 3);
        Matrix m = new Matrix(3);
        assertEquals(n, n.add(m));
    }

    @Test
    public void multA() throws Exception {
        Matrix n = new Matrix(3);
        // 1 0 0     1 1 1      1 1 1
        // 0 0 0  *  2 2 2  =   0 0 0
        // 0 0 0     3 3 3      0 0 0

        n.set(0, 0, 1);
        Matrix m = new Matrix(3);
        Matrix r = new Matrix(3);

        for (int i = 0; i < 3; i++) {
            m.set(0, i, 1);
            m.set(1, i, 2);
            m.set(2, i, 3);
            r.set(0, i, 1);
        }
        System.out.println("n = " + n);
        System.out.println("m = " + m);
        System.out.println("n.mult(m) = " + n.mult(m));
        assertEquals(r, n.mult(m));

    }

    @Test
    public void addA() throws Exception {
        Matrix n = new Matrix(3);
        // 1 0 0     1 1 1      2 1 1
        // 0 0 0  +  2 2 2  =   2 2 2
        // 0 0 1     3 3 3      3 3 4

        n.set(0, 0, 1);
        n.set(2, 2, 1);
        Matrix m = new Matrix(3);
        Matrix r = new Matrix(3);

        for (int i = 0; i < 3; i++) {
            m.set(0, i, 1);
            m.set(1, i, 2);
            m.set(2, i, 3);
        }
        r.set(0, 0, 2);
        r.set(1, 0, 2);
        r.set(2, 0, 3);
        r.set(0, 1, 1);
        r.set(1, 1, 2);
        r.set(2, 1, 3);

        r.set(0, 2, 1);
        r.set(1, 2, 2);
        r.set(2, 2, 4);
        assertEquals(r, n.add(m));

    }

    @Test
    public void transposeBasic() {
        Matrix m = m(4);
        assertEquals(m, m.transpose().transpose());
    }


    @Test
    public void transpose2x4() {
        Matrix m = new Matrix(2, 4);
        assertEquals(4, m.cols());
        assertEquals(2, m.rows());
        assertEquals(2, m.transpose().cols());
        assertEquals(4, m.transpose().rows());
    }

    @Test
    public void transpose2x4Value() {
        Matrix m = new Matrix(2, 4);
        m.set(1, 0, 4);
        assertEquals(0, m.transpose().get(1, 0), 0.00001);
        assertEquals(4, m.transpose().get(0, 1), 0.00001);
    }

    @Test
    public void multMatrix() {
        Matrix a = new Matrix(3, 4);
        Matrix b = new Matrix(4, 3);
        Matrix r = new Matrix(3, 3);
        for (int i = 0; i < 12; i++) {
            a.set(i / 4, i % 4, i + 1);
        }

        for (int i = 0; i < 12; i++) {
            b.set(i / 3, i % 3, 9 - i);
        }
        System.out.println("a = " + a);
        System.out.println("b = " + b);


        //30    20    10
        //102    76    50
        //174   132    90
        r.set(0, 0, 30);
        r.set(1, 0, 102);
        r.set(2, 0, 174);
        r.set(0, 1, 20);
        r.set(1, 1, 76);
        r.set(2, 1, 132);
        r.set(0, 2, 10);
        r.set(1, 2, 50);
        r.set(2, 2, 90);

        assertEquals(r.toString(), a.mult(b).toString());


    }


    @Test
    public void MatrixMultVector() {
        Matrix a = (Matrix) AbstractMatrix.parse(
                "1 3 4 5\n" +
                        "2 0 9 1\n" +
                        "-3 2 0 0\n"
        );
        Vector b = new Vector(new double[]{3, 2, 1, 4});
        Vector res = new Vector(new double[]{33, 19, -5});

        System.out.println("_-----------");
        Vector n = a.mult(b);
        System.out.println("-------");
        n.setPrecision(3);
        res.setPrecision(3);

        System.out.println(a);
        System.out.println(b);
        System.out.println(res);
        assertEquals(res, n);
    }

    @Test
    public void multFail() {
        Vector v = new Vector(2);
        Matrix d = new Matrix(2, 3);
        try {
            d.mult(v);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Multiplikation should have failed.");

    }

    @Test
    public void multSuccess() {
        Matrix d = new Matrix(2, 3);
        BandMatrix d2 = new BandMatrix(3, new int[]{});
        AbstractMatrix m = d.mult(d2);
        for (int r = 0; r < m.rows(); r++) {
            for (int c = 0; c < m.cols(); c++) {
                assertEquals(0, m.get(r, c), 0.000);
            }
        }
    }

    @Test
    public void multFailMatrixMatrix() {
        Matrix d = new Matrix(2, 3);
        BandMatrix d2 = new BandMatrix(2, new int[]{});
        try {
            d.mult(d2);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail(" Mutliplikation should have failed");
    }

    @Test
    public void addFail() {
        Matrix d = new Matrix(2, 3);
        Matrix d1 = new Matrix(3, 3);
        try {
            d.add(d1);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("addition should have failed.");

    }

    @Test
    public void Penta() {
        Matrix m = new Matrix(6);
        assertTrue(m.isPentadiagonale());
        for (int i = 0; i < 6; i++) {
            m.set(i, i, 1);
        }
        assertTrue(m.isPentadiagonale());
        for (int i = 0; i < 6; i++) {
            for (int j = -2; j < 3; j++) {
                if (j + i > 0 && j + i < 6) {
                    m.set(i, i + j, 1);
                }
            }
        }
        assertTrue(m.isPentadiagonale());
        m.set(0, 5, 1);
        assertFalse(m.isPentadiagonale());
    }

    @Test
    public void cloneTest() {
        Matrix d = new Matrix(2, 3);
        AbstractMatrix clone = d.clone();
        assertEquals(d, clone);
        clone.set(1, 1, 1);
        assertNotSame(d, clone);
        assertEquals(0, d.get(1, 1), 0);
        assertEquals(1, clone.get(1, 1), 0);
    }

    @Test
    public void EqualTest() {
        Matrix d = new Matrix(3, 3);
        BandMatrix d1 = new BandMatrix(3, new int[]{0});
        Matrix d2 = new Matrix(3, 2);
        assertFalse(d.equals(new Object()));
        assertFalse(d.equals(d2));
        assertFalse(d2.equals(d));
        assertTrue(d.equals(d));
        assertSame(d, d);
        assertTrue(d1.equals(d));
        d1.set(1, 1, 1);
        assertFalse(d1.equals(d));
    }

    @Test
    public void toFile() {
        Matrix d = new Matrix(3, 3);
        try {
            d.toFile("test.txt");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void toFileAsync() {
        Matrix d = new Matrix(3, 3);
        try {
            assertTrue(d.toFileSync("test.txt"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void setinfinit() {
        Matrix d = new Matrix(3);
        try {
            d.set(0, 0, Double.NaN);
            fail();
        } catch (Exception e) {
        }
        try {
            d.set(0, 0, Double.NEGATIVE_INFINITY);
            fail();
        } catch (Exception e) {
        }
    }


    @Test
    public void testConstrShort() {
        Matrix d = new Matrix(new short[][]{{1, 2}, {3, 4}});
        assertEquals(1, d.get(0, 0), 0);
        assertEquals(2, d.get(0, 1), 0);
        assertEquals(3, d.get(1, 0), 0);
        assertEquals(4, d.get(1, 1), 0);
    }

    @Test
    public void testConstrInt() {
        Matrix d = new Matrix(new int[][]{{1, 2}, {3, 4}});
        assertEquals(1, d.get(0, 0), 0);
        assertEquals(2, d.get(0, 1), 0);
        assertEquals(3, d.get(1, 0), 0);
        assertEquals(4, d.get(1, 1), 0);
    }
}
