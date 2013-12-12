package Maths;

import java.util.Set;

/**
 * Helpermethods to perform some frequently used operations on Arrays.
 *
 * @author sebastianzillessen
 */
public class ArrayMaths {

    private ArrayMaths(){

    }
    /**
     * Transforms an int array to double
     * @param d int array
     * @return same array as double
     */
    public static double[] intToDouble(int[] d) {
        double[] r = new double[d.length];
        for (int i = 0; i < d.length; i++) {
            r[i] = d[i];
        }
        return r;
    }

    /**
     * Transforms an Integer array to int
     * @param indexes Integer array
     * @return same array int
     */
    public static int[] TointArray(Integer[] indexes) {
        int[] r = new int[indexes.length];
        for (int i = 0; i < r.length; i++)
            r[i] = indexes[i].intValue();
        return r;
    }

    /**
     * Transforms an Integer set to int
     * @param indexes Integer set
     * @return same array int
     */

    public static int[] TointArray(Set<Integer> indexes) {
        return TointArray((Integer[]) indexes.toArray(new Integer[indexes.size()]));
    }


    /**
     * Rounds a double value with a given presicion of decimal places
     * @param value the value to round
     * @param precisionDigets the number of digests
     * @return the rounded value
     */
    public static double round(double value, int precisionDigets) {
        int valueInTwoDecimalPlaces = (int) (value * Math.pow(10, precisionDigets));
        return (float) (valueInTwoDecimalPlaces / Math.pow(10, precisionDigets));
    }


    /**
     * Returns the maximum difference of the the arrays in each component
     *
     * @param a1 array 1
     * @param a2 array 2
     * @return maximum difference (regarded on components)
     */
    public static double diffMax(double[] a1, double[] a2) {
        if (a1.length != a2.length)
            throw new IllegalArgumentException("Arrays do not match");
        double max = 0;
        for (int i = 0; i < a1.length; i++) {
            max = Math.max(max, Math.abs(a1[i] - a2[i]));
        }
        return max;
    }
}
