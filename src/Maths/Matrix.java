package Maths;

/**
 * Implementation of AbstractMatrix as Default implementation.
 * Uses an 2D array to store the Matrix internal. Should not be used with large matrizes.
 *
 * @author sebastianzillessen
 */
public class Matrix extends AbstractMatrix {

    protected double[][] mat;


    /**
     * Default constructor.
     * Creates a matrix of size n x n with all values inited to 0.
     *
     * @param n number of rows and cols of this matrix.
     */
    public Matrix(int n) {
        this(n, n);
    }

    /**
     * Default constructor.
     * Creates a matrix of size m (rows) x n (cols) with all values inited to 0.
     *
     * @param m number of rows
     * @param n number of cols
     */
    public Matrix(int m, int n) {
        mat = new double[m][n];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                mat[i][j] = 0;
            }
        }
    }

    /**
     * Constructor to parse an array to a matrix.
     * The ranges are taken from the 2d array.
     *
     * @param values The values to parse this matrix from.
     */
    public Matrix(short[][] values) {
        this(values.length, values[0].length);
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                mat[i][j] = (values[i][j]);
            }
        }
    }

    /**
     * Constructor to parse an array to a matrix.
     * The ranges are taken from the 2d array.
     *
     * @param values The values to parse this matrix from.
     */
    public Matrix(int[][] values) {
        this(values.length, values[0].length);
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                mat[i][j] = (values[i][j]);
            }
        }
    }

    /**
     * Constructor to parse an array to a matrix.
     * The ranges are taken from the 2d array.
     *
     * @param values The values to parse this matrix from.
     */
    public Matrix(double[][] values) {
        this(values.length, values[0].length);
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                mat[i][j] = (values[i][j]);
            }
        }
    }


    /**
     * multiplication of this matrix with a real value. Each element of the matrix gets multiplied by a.
     *
     * @param a value to multiply this matrix
     * @return multiplied istance of this matrix. For a = 1 the same matrix is returned.
     */
    @Override
    public Matrix mult(double a) {
        Matrix m = new Matrix(rows(), cols());
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                m.set(i, j, mat[i][j] * (a));
            }
        }
        return m;
    }

    /**
     * Sets a entry of the Matrix.
     *
     * @param row   Row of the matrix element
     * @param col   Col of the matrix element
     * @param value value for the matrix element
     * @throws java.lang.IndexOutOfBoundsException if the row or col are not in the matrixs range.
     */
    @Override
    public void set(int row, int col, double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new ArithmeticException("Value is " + value);
        }
        if (row >= 0 && col >= 0 && row < mat.length && col < mat[row].length) {
            mat[row][col] = value;
        } else {
            throw new IndexOutOfBoundsException("cannot set " + row + "|" + col + ": index out of range");
        }
    }

    /**
     * Returns number of cols
     *
     * @return number of cols
     */
    @Override
    public int cols() {
        return mat[0].length;
    }

    /**
     * Returns number of rows
     *
     * @return number of rows
     */
    @Override
    public int rows() {
        return mat.length;
    }

    /**
     * returns a value in the matrix. if the value has not been set yet it returns 0.
     *
     * @param row row of the matrix element.
     * @param col col of the matrix element
     * @return the value of the matrix in this position. if not set, then it returns 0.
     * @throws IndexOutOfBoundsException if the row or col are not in the matrix range.
     */
    @Override
    public double get(int row, int col) {
        return mat[row][col];

    }

    /**
     * Returns a string representation of this matrix containing relevant information for debugging processes.
     *
     * @return String for debug.
     */
    @Override
    public String debugString() {
        return String.format("Default Matrix: (%d x %d)", rows(), cols());
    }

}