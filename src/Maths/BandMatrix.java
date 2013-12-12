package Maths;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This implements a band Matrix with diagonales from top left to bottom right.
 * It can be used to save memory.
 *
 * @author sebastianzillessen
 */
public class BandMatrix extends AbstractMatrix {


    protected double[] elements;
    protected int[] bandIndexes;
    private int size;

    /**
     * creates a new BandMatrix out of an existing matrix (can sometimes be good for transformations)
     *
     * @param m
     */
    public BandMatrix(AbstractMatrix m) {
        this(m.cols(), extractBands(m));
        // init values
        for (int row = 0; row < rows(); row++) {
            for (int i = 0; i < bandIndexes.length; i++) {
                int col = bandIndexes[i] + row;
                if (col >= 0 && col < size && m.get(row, col) != 0) {
                    set(row, col, m.get(row, col));
                }
            }
        }
    }

    /**
     * generates a new bandmatrix of the size size x size with the specified band indexes.
     * 0 is the center diagonale from top left to bottom right.
     * -n is the n-th diagonale below the center.
     * +n is the n-th diagonele above the center.
     *
     * @param size        size of the matrix
     * @param bandIndexes diagonales which should be able to be set (0 is zenter)
     */
    public BandMatrix(int size, int[] bandIndexes) {
        this.size = size;

        Arrays.sort(bandIndexes);
        int elementSize = 0;
        for (int i = 0; i < bandIndexes.length; i++) {
            elementSize += (size - Math.abs(bandIndexes[i]));
        }
        this.elements = new double[elementSize];
        for (int i = 0; i < elementSize; i++) {
            elements[i] = 0;
        }
        this.bandIndexes = bandIndexes;
    }

    /**
     * generates a new bandmatrix of the size size x size with the specified band indexes.
     * 0 is the center diagonale from top left to bottom right.
     * -n is the n-th diagonale below the center.
     * +n is the n-th diagonele above the center.
     *
     * @param size        size of the matrix
     * @param bandIndexes diagonales which should be able to be set (0 is zenter)
     */
    public BandMatrix(int size, Set bandIndexes) {
        this(size, ArrayMaths.TointArray(bandIndexes));
    }


    /**
     * Parses a String to a Band Matrix. Entries have to be seperated by space, new lines represent a new row in the matrix.
     * <p/>
     * Example:
     * <p/>
     * s = "1 2 0 0\n"+
     * "0 7 8 0 \n"+
     * "0 0 1 1"+
     *
     * @param s space seperated matrix input (new lines for new row)
     * @return a band matrix corresponding to s
     */
    public static BandMatrix parse(String s) {
        return new BandMatrix(AbstractMatrix.parse(s));
    }

    @Override
    public BandMatrix add(AbstractMatrix m) {

        if (m.cols() != size || m.rows() != size)
            throw new IllegalArgumentException("The both matrices to add are not of the same size");
        Set indexes = new HashSet<Integer>();
        for (int row = 0; row < m.rows(); row++) {
            for (int col = 0; col < m.cols(); col++) {
                if (m.get(row, col) != 0)
                    indexes.add(col - row);
            }
        }
        BandMatrix res = new BandMatrix(size, indexes);
        for (int row = 0; row < size; row++) {
            for (int i = 0; i < res.bandIndexes.length; i++) {
                int col = row + res.bandIndexes[i];
                if (col >= 0 && col < size && getIndex(row, col) != -1)
                    res.set(row, col, this.get(row, col) + (m.get(row, col)));
            }
        }
        return res;
    }


    /**
     * Adds two matrixes element wise.
     *
     * @param m other matrix to add on this matrix.
     * @return result of this + m (each coefficients will be added).
     * @throws java.lang.IllegalArgumentException if the matrixes are not of same size.
     */
    public BandMatrix add(BandMatrix m) {
        if (m.size != size) {
            throw new IllegalArgumentException("The both matrices to add are not of the same size");
        }
        // get indexes
        Set indexes = new HashSet<Integer>();
        for (int i = 0; i < bandIndexes.length; i++) {
            indexes.add(bandIndexes[i]);
        }
        for (int i = 0; i < m.bandIndexes.length; i++) {
            indexes.add(m.bandIndexes[i]);
        }

        BandMatrix res = new BandMatrix(size, indexes);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                double n = 0;
                if (m.get(row, col) != 0)
                    n += m.get(row, col);
                if (get(row, col) != 0)
                    n += get(row, col);
                if (n != 0)
                    res.set(row, col, n);
            }
        }
        return res;
    }

    /**
     * Clones a Band Matrix.
     *
     * @return the exact same Matrix as new instance.
     */
    @Override
    public BandMatrix clone() {
        BandMatrix r = new BandMatrix(size, bandIndexes);
        for (int i = 0; i < elements.length; i++) {
            r.elements[i] = elements[i];
        }
        return r;
    }

    /**
     * Multiplies the Band Matrix with a real number.
     *
     * @param a value to multiply this matrix
     * @return scaled instance of this matrix.
     */
    @Override
    public BandMatrix mult(double a) {
        BandMatrix m = new BandMatrix(size, bandIndexes);
        for (int i = 0; i < elements.length; i++) {
            m.elements[i] = elements[i] * a;
        }
        return m;
    }


    /**
     * sets an entry in the matrix.
     *
     * @param row Row of the matrix element
     * @param col Col of the matrix element
     * @param f   Number to set the entry to
     * @throws java.lang.ArithmeticException       if f is NaN or Infinite
     * @throws java.lang.IndexOutOfBoundsException if the row or col is out of range
     */
    @Override
    public void set(int row, int col, double f) {
        if (Double.isInfinite(f) || Double.isNaN(f)) {
            throw new ArithmeticException("Value to set in Band Matrix is " + f);
        }
        int pos = getIndex(row, col);
        if (pos == -1 || pos >= elements.length) {
            // give only an alert if i'm not zero
            if (f != 0.0) {
                throw new IndexOutOfBoundsException("This is a Band Matrix and you tried to set a value which is not in the bounds (" + row + "|" + col + ")\n" + this.debugString());
            }
        } else
            elements[pos] = f;
    }

    /**
     * Get columns of this matrix
     *
     * @return number of cols
     */
    @Override
    public int cols() {
        return size;
    }

    /**
     * Get rows of this matrix
     *
     * @return number of rows
     */
    @Override
    public int rows() {
        return size;
    }

    /**
     * Get the entry of a element.
     *
     * @param row row of the matrix element.
     * @param col col of the matrix element
     * @return the set value in this cell. If no value was set, zero is returned. If the entry is outside of the
     * specified bands of this matrix zero is returned as well.
     * @throws java.lang.IndexOutOfBoundsException if rows or cols is not in the range of this matrix
     */
    public double get(int row, int col) {
        if (row < 0 || col < 0 || row >= rows() || col >= cols())
            throw new IndexOutOfBoundsException("This is a Band Matrix and you tried to get a value which is not in the bounds (" + row + "|" + col + ")\n" + this.debugString());
        int pos = getIndex(row, col);
        if (pos == -1)
            return 0.0;
        else {
            return elements[pos];

        }
    }

    /**
     * returns a string which represents the instance of this matrix for debugging purposes.
     *
     * @return Debug-String representation.
     */
    @Override
    public String debugString() {
        String s = "";
        for (int i = 0; i < bandIndexes.length; i++) {
            s += bandIndexes[i] + ",";
        }
        s = "This is a Band Matrix :\n" +
                " The Matrix diagonales are: " + s + "\n" +
                " This Matrix has only " + elements.length + " elements.\n" +
                " The matrix is: " + rows() + "x" + cols() + "\n" +
                " The Matrix elements are: ";
        for (int i = 0; i < elements.length; i++) {
            s += elements[i] + ",";
        }
        s += "\n";
        return s;


    }


    /**
     * Tranposes a Matrix.
     *
     * @return transposed instance of this matrix.
     */
    @Override
    public BandMatrix transpose() {
        int[] newBandIndexes = Arrays.copyOf(bandIndexes, bandIndexes.length);
        for (int i = 0; i < bandIndexes.length; i++)
            newBandIndexes[i] = -1 * bandIndexes[i];
        BandMatrix res = new BandMatrix(size, newBandIndexes);
        for (int row = 0; row < size; row++) {
            for (int k = 0; k < newBandIndexes.length; k++) {
                int col = row + newBandIndexes[k];
                if (col >= 0 && col < size)
                    res.set(row, col, get(col, row));
            }
        }
        return res;
    }

    /**
     * Multiplies to Band-Matrices in an efficient way.
     * <p/>
     * If no efficient way is found it falls back to default multiplication and parses it as Band Matrix.
     *
     * @param m other band matrix
     * @return Result of this * m as matrix multiplication
     * @see AbstractMatrix#mult(AbstractMatrix)
     */
    public BandMatrix mult(BandMatrix m) {
        // Multiply column wise, because second matrix is only middle diagonale
        if (m.size == this.size && m.bandIndexes.length == 1 && m.bandIndexes[0] == 0) {
            BandMatrix res = this.clone();
            for (int col = 0; col < size; col++) {
                double f = m.get(col, col);
                for (int row = 0; row < size; row++) {
                    res.set(row, col, res.get(row, col) * (f));
                }
            }
            return res;
        }    // Multiply row wise, because first matrix is only middle diagonale
        else if (m.size == this.size && bandIndexes.length == 1 && bandIndexes[0] == 0) {
            BandMatrix res = new BandMatrix(size, m.bandIndexes);
            for (int row = 0; row < size; row++) {
                double f = this.get(row, row);
                for (int i = 0; i < m.bandIndexes.length; i++) {
                    int col = m.bandIndexes[i] + row;
                    if (col >= 0 && col < size) {
                        res.set(row, col, m.get(row, col) * (f));
                    }
                }
            }
            return res;
        } else {
            return new BandMatrix(super.mult(m));
        }

    }


    /**
     * Multiplies a matrix with a vector.
     *
     * @param x vector to multiply this matrix with
     * @return result of A * x
     * @throws java.lang.IllegalArgumentException if the Vector doesn't match the size of the matrix (x.length() != this.cols())
     */
    @Override
    public Vector mult(final Vector x) {
        if (x.length() != this.cols())
            throw new IllegalArgumentException("Matrix * vector: vector must be of size " + cols() + " but was " + x.length());
        final Vector r = new Vector(this.rows());

        ExecutorService executor = Executors.newFixedThreadPool(7);
        for (int i = 0; i < rows(); i++) {
            final int finalI = i;
            Runnable worker = new Runnable() {
                @Override
                public void run() {
                    double sum = 0;
                    for (int j = 0; j < bandIndexes.length; j++) {
                        int k = finalI + bandIndexes[j];
                        if (k >= 0 && k < cols() && x.get(finalI) != 0 && get(finalI, k) != 0)
                            sum += get(finalI, k);
                    }
                    r.set(finalI, sum * x.get(finalI));
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
     * Checks if a matrix is positiv semi definite
     *
     * @return true if the matrix is positiv semi definite.
     */
    @Override
    public boolean isPositiveSemiDefinit() {
        for (int row = 0; row < size; row++) {
            double sum = 0;
            for (int i = 0; i < bandIndexes.length; i++) {
                int col = row + bandIndexes[i];
                if (row != col && col < size && col >= 0)
                    sum = sum + (get(row, col));
            }
            if (sum > get(row, row)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Stores a matrix in a file.
     *
     * @param filename the file where to save it.
     * @return true if the file was stored, false if not.
     */
    @Override
    public boolean toFileSync(String filename) {
        BufferedWriter writer = null;
        boolean success = false;
        try {
            writer = new BufferedWriter(new FileWriter(filename));

            for (int row = 0; row < rows(); row++) {
                for (int i = 0; i < bandIndexes.length; i++) {
                    int col = row + bandIndexes[i];
                    if (col >= 0 && col < cols()) {
                        writer.append(String.format(Locale.ENGLISH, "%8.4f", get(row, col)));
                    } else {
                        writer.append("        ");
                    }
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
            e.printStackTrace();
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
     * checks if a matrix is symmetric
     *
     * @return true if the matrix is symmetric
     */
    @Override
    public boolean isSymmetric() {
        for (int i = 0; i < this.bandIndexes.length / 2; i++) {
            if (bandIndexes[i] != -bandIndexes[bandIndexes.length - 1 - i]) {
                return false;
            }
        }
        for (int row = 0; row < size; row++) {
            for (int i = 0; i < this.bandIndexes.length / 2; i++) {
                int col = row + bandIndexes[i];
                if (col >= 0 && col < size / 2 && get(row, col) != get(col, row)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if a matrix is pentadiagonale
     * (only the diagonale and the two elements next to it are set, so special form of a band matrix)
     * A Band matrix is pentadiagonale if the bandIndexes are a subset of [-2,-1,0,1,2]
     *
     * @return true if pentadiagonale
     */
    @Override
    public boolean isPentadiagonale() {
        for (int i = 0; i < bandIndexes.length; i++) {
            if (bandIndexes[i] < -2 || bandIndexes[i] > 2)
                return false;
        }
        return true;
    }

    // --------------- --------------- Private Methods  --------------- ---------------

    /**
     * returns the index in the internal array of a given row and col
     *
     * @param row row in the matrix
     * @param col col in the matrix
     * @return index in the band index array (1D) or -1 if not availble
     */
    private int getIndex(int row, int col) {
        int diffToCenterInRow = col - row;
        int pos = Arrays.binarySearch(bandIndexes, diffToCenterInRow);
        if (pos < 0)
            return -1;
        else {
            int index = row;
            for (int i = 0; i < pos; i++) {
                index += (size - Math.abs(bandIndexes[i]));
            }
            if (row > col)
                index += bandIndexes[pos];

            return index;
        }
    }

    /**
     * Extracts from a given Matrix the used bands to transform the matrix into a band matrix.
     *
     * @param m Matrix to check the bands on
     * @return indexes of the bands required for the band matrix representation.
     * [0] stands for only the center diagonale is set.
     * [1,0] is a diagonale two band matrix with center and one diagonale above set.
     */
    private static int[] extractBands(AbstractMatrix m) {
        Set<Integer> diagonales = new HashSet<Integer>();
        if (m.rows() != m.cols())
            throw new IllegalArgumentException("Only Quadratic Matrices allowed");
        for (int row = 0; row < m.rows(); row++) {
            for (int col = 0; col < m.cols(); col++) {
                double v = m.get(row, col);
                if (v != 0) {
                    diagonales.add(col - row);
                }
            }
        }
        return ArrayMaths.TointArray(diagonales);
    }


}
