package Maths;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Representation of a vector with double values.
 * it uses a double array to store the vector internally.
 *
 * @author sebastianzillessen
 */
public class Vector {
    protected double[] v;
    private double max = -1;
    private double min = -1;
    private int precision = -1;


    /**
     * Default constructor to be used to init a vector of size n
     * the vector is inited with 0.
     *
     * @param n the size of the vector.
     */
    public Vector(int n) {
        v = new double[n];
        for (int j = 0; j < v.length; j++) {
            v[j] = 0;
        }
    }

    /**
     * Constructor which parses a given 1D array to a vector. The values of the array are used.
     *
     * @param values
     */
    public Vector(double[] values) {
        this(values.length);
        for (int i = 0; i < values.length; i++) {
            set(i, values[i]);
        }
    }

    /**
     * Constructor which parses a given 1D array to a vector. The values of the array are used.
     *
     * @param values
     */
    public Vector(int[] values) {
        this(values.length);
        for (int i = 0; i < values.length; i++) {
            set(i, values[i]);
        }
    }

    /**
     * Constructor to init a vector with a given value.
     *
     * @param n              length of the vector
     * @param initialization value for the initialization
     */
    public Vector(int n, double initialization) {
        this(n);
        for (int i = 0; i < n; i++) {
            v[i] = initialization;
        }
    }

    /**
     * Adds a vector to this one.
     *
     * @param v other vector to be added
     * @return this + v
     * @throws java.lang.IllegalArgumentException if the vectors do not match in their size.
     */
    public Vector add(Vector v) {
        if (v.length() == length()) {
            Vector r = new Vector(length());
            for (int i = 0; i < length(); i++) {
                r.set(i, get(i) + (v.get(i)));
            }
            return r;
        } else
            throw new IllegalArgumentException("Wrong length");
    }

    /**
     * subtracts a value from each entry in the vector
     *
     * @param v value to be subtracted
     * @return this vector subtracted by v in each entry.
     */
    public Vector subtract(double v) {
        return add(-v);
    }


    /**
     * Subtracts a vector from this one.
     *
     * @param v other vector to be subtracted
     * @return this - v
     * @throws java.lang.IllegalArgumentException if the vectors do not match in their size.
     */
    public Vector subtract(Vector v) {
        if (v.length() == length()) {
            Vector r = new Vector(length());
            for (int i = 0; i < length(); i++) {
                r.set(i, get(i) - v.get(i));
            }
            return r;
        } else
            throw new IllegalArgumentException("Wrong length self: " + length() + " other: " + v.length());
    }

    /**
     * Returns the quadrat of the euklidian norm.
     * @return (v_1^2 +...+v_n^)
     */
    public double abs2() {
        double t = 0;
        for (int i = 0; i < length(); i++) {
            t += v[i] * v[i];
        }
        return t;
    }


    /**
     * adds a value from each entry in the vector
     *
     * @param v value to be subtracted
     * @return this vector subtracted by v in each entry.
     */
    public Vector add(double v) {
        Vector r = new Vector(length());
        for (int i = 0; i < length(); i++) {
            r.set(i, this.v[i] + v);
        }
        return r;
    }

    /**
     * returns a vector where each value is e^v_n of the current vector
     * @return vector with same length but each vector entry is the exponent of the old one.
     */
    public Vector exp() {
        Vector v = new Vector(length());
        for (int i = 0; i < v.length(); i++) {
            v.set(i, Math.exp(get(i)));
        }
        return v;
    }

    /**
     * sets a vector entry.
     * @param i index to set
     * @param f value to set.
     */
    public void set(int i, BigDecimal f) {
        set(i, f.doubleValue());
    }

    /**
     * sets a vector entry.
     * @param i index to set
     * @param f value to set.
     */
    public void set(int i, double f) {
        if (Double.isNaN(f) || Double.isInfinite(f)) {
            throw new ArithmeticException("Value is " + f);
        }
        v[i] = f;
        min = -1;
        max = -1;
    }

    /**
     * gets a vector entry.
     * @param i index to get.
     */
    public double get(int i) {
        return v[i];
    }

    /**
     * Returns the length of the vector.
     * @return
     */
    public int length() {
        return v.length;
    }

    /**
     * Copies a vector
     * @return copied instance of this vector. Values are preserved.
     */
    public Vector copy() {
        Vector res = new Vector(length());
        for (int i = 0; i < length(); i++)
            res.set(i, get(i));
        return res;
    }


    /**
     * returns a matlab string representation of this vector.
     * @return String representation of this vector. The entries are rounded with the given precision in @see setPrecision
     */
    public String toString() {
        String s = "[";
        for (int i = 0; i < length(); i++) {
            s += precision >= 0 ? ArrayMaths.round(get(i), precision) : get(i);
            if (i < length() - 1)
                s += " ";
        }
        s += "]";
        return s;
    }

    /**
     * Converts a vector to an array.
     * @return array represenatation of this vector.
     */
    public double[] toArray() {
        double[] r = new double[length()];
        for (int i = 0; i < length(); i++)
            r[i] = get(i);
        return r;
    }


    /**
     * Returns the absolute biggest value of a vector.
     * @return absolute biggest value.
     */
    public double absMax() {
        double absMax = Math.abs(get(0));
        for (int i = 1; i < length(); i++) {
            absMax = Math.max(absMax, Math.abs(v[i]));
        }
        return absMax;
    }

    /**
     * Returns the maximum of an vector.
     * @return maximum.
     */
    public double max() {
        if (this.max == -1) {
            this.max = get(0);
            for (int i = 1; i < length(); i++) {
                max = Math.max(max, v[i]);
            }
        }
        return max;
    }

    /**
     * Returns the minimum of an vector.
     * @return maximum.
     */
    public double min() {
        if (this.min == -1) {
            this.min = get(0);
            for (int i = 1; i < length(); i++) {
                min = Math.min(min, v[i]);
            }
        }
        return min;
    }


    /**
     * Checks if a vector equals an other.
     * @param other
     * @return true if length and each entry are equal concerning the precision of the vector.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Vector))
            return false;
        Vector d = (Vector) other;
        if (d.length() != length())
            return false;
        int p = Math.max(d.precision, precision);
        for (int i = 0; i < length(); i++)
            if (get(i) - d.get(i) > Math.pow(10, precision))
                return false;
        return true;
    }

    /**
     * Sets the precision of this vector (number of digits in string output and in the method equals.
     * @param precision the number of digits to use.
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    /**
     * returns the precision of this vector set by @see setPrecision.
     * @return the number of digist -1 if no precision is set.
     */
    public int getPrecision() {
        return precision;
    }


    /**
     * Stores this vector to a file asynchronous.
     * @param filename the file name where to store it
     */
    public void toFile(final String filename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(filename));
                    writer.write(Vector.this.toString());
                    System.out.println("File '" + filename + "' saved successfully.");
                } catch (IOException e) {
                } finally {
                    try {
                        if (writer != null)
                            writer.close();
                    } catch (IOException e) {
                    }
                }
            }
        }).start();
    }

    /**
     * Returns a debug string of this vector
     * @return
     */
    public String debugString() {
        return toString();
    }
}
