package Maths;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Abstract implementation of a Matrix.
 * <p/>
 * This class offers the basic functionalities for a matrix.
 *
 * @author sebastianzillessen
 */
public abstract class AbstractMatrix {

    /**
     * Sets a entry of the Matrix.
     *
     * @param row   Row of the matrix element
     * @param col   Col of the matrix element
     * @param value value for the matrix element
     * @throws java.lang.IndexOutOfBoundsException if the row or col are not in the matrixs range.
     */
    public abstract void set(int row, int col, double value);


    /**
     * returns a value in the matrix. if the value has not been set yet it returns 0.
     *
     * @param row row of the matrix element.
     * @param col col of the matrix element
     * @return the value of the matrix in this position. if not set, then it returns 0.
     * @throws IndexOutOfBoundsException if the row or col are not in the matrix range.
     */
    public abstract double get(int row, int col);

    /**
     * Returns number of cols
     *
     * @return number of cols
     */
    public abstract int cols();

    /**
     * Returns number of rows
     *
     * @return number of rows
     */
    public abstract int rows();

    /**
     * Returns a string representation of this matrix containing relevant information for debugging processes.
     *
     * @return String for debug.
     */
    public abstract String debugString();


    /**
     * multiplication of this matrix with a real value. Each element of the matrix gets multiplied by a.
     *
     * @param a value to multiply this matrix
     * @return multiplied istance of this matrix. For a = 1 the same matrix is returned.
     */
    public abstract AbstractMatrix mult(double a);


    /**
     * Multiplies a matrix with a vector.
     *
     * @param x vector to multiply this matrix with
     * @return result of A * x
     * @throws java.lang.IllegalArgumentException if the Vector doesn't match the size of the matrix (x.length() != this.cols())
     */
    public Vector mult(final Vector x) {
        if (x.length() != cols())
            throw new IllegalArgumentException("Matrix * vector: vector must be of size " + cols() + " but was " + x.length());
        final Vector r = new Vector(this.rows());
        ExecutorService executor = Executors.newFixedThreadPool(8);
        for (int i = 0; i < rows(); i++) {
            final int finalI = i;
            Runnable worker = new Runnable() {
                @Override
                public void run() {
                    double sum = 0;
                    for (int j = 0; j < cols(); j++) {
                        if (x.get(j) != 0 && get(finalI, j) != 0)
                            sum += x.get(j) * get(finalI, j);
                    }
                    r.set(finalI, sum);
                }
            };
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        return r;
    }

    /**
     * Multiplies a matrix with a vector (vector is represented by the array of real numbers).
     *
     * @param x vector to multiply this matrix with
     * @return result of A * x
     * @throws java.lang.IllegalArgumentException if the Vector doesn't match the size of the matrix (x.length() != this.cols())
     */
    public Vector mult(double[] x) {
        return this.mult(new Vector(x));
    }


    /**
     * Multiplies a matrix with another matrix.
     *
     * @param m other matrix
     * @return result of the matrix multiplication this * m
     * @throws java.lang.IllegalArgumentException if the matrizes do not match.
     */
    public AbstractMatrix mult(AbstractMatrix m) {
        if (m.rows() != cols())
            throw new IllegalArgumentException("Not the same rows and colls. Inputs has " + m.rows() + " rows. I have " + cols() + " cols");
        AbstractMatrix c = new Matrix(rows(), m.cols());
        for (int i = 0; i < c.rows(); i++) {
            for (int k = 0; k < c.cols(); k++) {
                double a = 0;
                for (int j = 0; j < cols(); j++) {
                    if (get(i, j) != 0 && m.get(j, k) != 0)
                        a += get(i, j) * m.get(j, k);
                }
                c.set(i, k, a);
            }
        }

        return c;
    }


    /**
     * Adds an other matrix to this matrix.
     *
     * @param m other matrix
     * @return result of the matrix addition this + m
     * @throws java.lang.IllegalArgumentException if the matrizes do not match.
     */
    public AbstractMatrix add(AbstractMatrix m) {
        if (m.cols() != rows() || m.cols() != cols())
            throw new IllegalArgumentException("Not the same rows and colls");
        Matrix r = new Matrix(rows(), cols());
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < cols(); j++) {
                r.set(i, j, m.get(i, j) + get(i, j));
            }
        }
        return r;
    }

    /**
     * Transposed matrix of this matrix.
     *
     * @return transposed matrix.
     */
    public AbstractMatrix transpose() {
        AbstractMatrix res = new Matrix(cols(), rows());
        for (int r = 0; r < rows(); r++) {
            for (int c = 0; c < cols(); c++) {
                res.set(c, r, get(r, c));
            }
        }
        return res;
    }


    /**
     * Decomposes a matrix if it is pentadiagonale.
     *
     * @return two matrizes [L, U] with L * U = this,
     * L contains only entries lower then the diagonale and the diagonale entries are 1,
     * U contains only entries over or on the diagonale.
     * @throws java.lang.IllegalArgumentException if the matrix is not pentadiagonale this error is thrown.
     */
    public AbstractMatrix[] decomposePenta() throws IllegalArgumentException {
        final int n = rows();
        if (this.isPentadiagonale() == false)
            throw new IllegalArgumentException("Matrix is not pentadiagonale. Cannot do the pentadiagonal decomposition.");

        BandMatrix L = new BandMatrix(n, new int[]{0, -1, -2});
        BandMatrix R = new BandMatrix(n, new int[]{0, 1, 2});
        double[] m = new double[n];
        double[] l = new double[n];
        double[] k = new double[n];
        double[] p = new double[n];
        double[] r = new double[n];
        // initialization
        m[0] = get(0, 0);
        r[0] = get(0, 1);
        l[1] = get(1, 0) / m[0];
        m[1] = get(1, 1) - l[1] * r[0];

        // p_i s
        for (int i = 0; i < n - 2; i++) {
            p[i] = get(i, i + 2);
        }

        for (int i = 2; i < n; i++) {
            k[i] = get(i, i - 2) / m[i - 2];
            l[i] = (get(i, i - 1) - k[i] * r[i - 2]) / m[i - 1];
            r[i - 1] = get(i - 1, i) - (l[i - 1] * (p[i - 2]));
            m[i] = get(i, i) - k[i] * p[i - 2] - l[i] * r[i - 1];
        }

        // build L
        for (int i = 0; i < n; i++) {
            L.set(i, i, 1);
            if (i >= 1)
                L.set(i, i - 1, l[i]);
            if (i >= 2)
                L.set(i, i - 2, k[i]);
        }

        //build R
        for (int i = 0; i < n; i++) {
            R.set(i, i, m[i]);
            if (i < n - 1)
                R.set(i, i + 1, r[i]);
            if (i < n - 2)
                R.set(i, i + 2, p[i]);
        }

        return new AbstractMatrix[]{L, R};
    }


    /**
     * Method to print a matrix.
     * <p/>
     * Use this only on small matrices!
     *
     * @return a string representing the matrix entries.
     */
    @Override
    public String toString() {
        String s = "[\n";
        for (int row = 0; row < rows(); row++) {
            for (int col = 0; col < cols(); col++) {
                s += String.format(Locale.ENGLISH, "%8.4f", get(row, col));
                if (col < cols() - 1)
                    s += " ";
            }
            s += "\n";
        }
        s += "]\n";
        return s;
    }

    /**
     * Clones a matrix.
     *
     * @return clone of this matrix.
     */
    public AbstractMatrix clone() {
        AbstractMatrix r = new Matrix(rows(), cols());
        for (int row = 0; row < rows(); row++) {
            for (int col = 0; col < cols(); col++) {
                r.set(row, col, get(row, col));
            }
        }
        return r;
    }


    /**
     * Checks if two matrices are the same.
     *
     * @param o other matrix
     * @return true if  each entry is the exactly the same in each matrix and the size equals as well.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractMatrix) {
            AbstractMatrix m = (AbstractMatrix) o;
            if (m.rows() == rows() && m.cols() == cols()) {
                for (int i = 0; i < rows(); i++)
                    for (int j = 0; j < cols(); j++)
                        if (get(i, j) != m.get(i, j))
                            return false;
                return true;
            }
        }
        return false;
    }

    /**
     * Parses a string representation of a matrix to a matrix.
     * <p/>
     * Format has to be:
     * 0 0 0 0
     * ...
     * 0 0 0 0
     * (rows seperated by \n and entries seperated by space).
     *
     * @param s string representation (see above)
     * @return a matrix defined by the string
     */
    public static AbstractMatrix parse(String s) {
        String[] lines = s.trim().split("\n");
        int rows = lines.length;
        int cols = lines[0].trim().split(" ").length;

        AbstractMatrix m = new Matrix(rows, cols);
        for (int row = 0; row < rows; row++) {
            String[] rowElements = lines[row].trim().split(" ");
            for (int col = 0; col < cols; col++) {
                double v = Double.parseDouble(rowElements[col]);
                if (v != 0.0) {
                    m.set(row, col, v);
                }
            }
        }
        return m;
    }

    /**
     * Saves a matrix to a file in text form. This is handeld asynchron and does not return any errors,
     * if the save process failed.
     *
     * @param filename the filename where to save it.
     */
    public void toFile(final String filename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AbstractMatrix.this.toFileSync(filename);
            }
        }).start();
    }

    /**
     * Array representation of the Matrix.
     *
     * @return Matrix as array of doubles.
     */
    public double[][] toArray() {
        double[][] res = new double[rows()][cols()];
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < cols(); j++) {
                res[i][j] = get(i, j);
            }
        }
        return res;
    }

    /**
     * Stores a matrix in a file.
     *
     * @param filename the file where to save it.
     * @return true if the file was stored, false if not.
     */
    public boolean toFileSync(String filename) {
        BufferedWriter writer = null;
        boolean success = false;
        try {
            writer = new BufferedWriter(new FileWriter(filename));

            writer.append("[\n");
            for (int row = 0; row < rows(); row++) {
                for (int col = 0; col < cols(); col++) {
                    writer.append(String.format(Locale.ENGLISH, "%8.4f", get(row, col)));
                    if (col < cols() - 1)
                        writer.append(" ");
                }
                writer.append("\n");
            }
            writer.append("]\n");
            writer.flush();
            System.out.println("File '" + filename + "' saved successfully.");
            success = true;
        } catch (IOException e) {
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
            }
        }
        return success;

    }

    /**
     * Checks if a matrix is quadratic
     *
     * @return quadratic (rows == cols)
     */
    public boolean isQuadratic() {
        return rows() == cols();
    }

    /**
     * checks if a matrix is symmetric
     *
     * @return true if the matrix is symmetric
     */
    public boolean isSymmetric() {
        if (!isQuadratic())
            return false;
        for (int row = 0; row < rows(); row++) {
            for (int col = 0; col < cols(); col++) {
                if (get(row, col) != get(col, row)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if a matrix is positiv semi definite
     *
     * @return true if the matrix is positiv semi definite.
     */
    public boolean isPositiveSemiDefinit() {
        for (int row = 0; row < rows(); row++) {
            double sum = 0;
            for (int col = 0; col < cols(); col++) {
                sum = sum + (get(row, col));
            }
            if (sum > get(row, row)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a matrix is pentadiagonale
     * (only the diagonale and the two elements next to it are set, so special form of a band matrix)
     *
     * @return true if pentadiagonale
     */
    public boolean isPentadiagonale() {
        for (int row = 0; row < rows(); row++) {
            for (int col = 0; col < cols(); col++) {
                if ((col < row - 2 || col > row + 2) && get(row, col) != 0) {
                    return false;
                }
            }
        }
        return true;
    }
}