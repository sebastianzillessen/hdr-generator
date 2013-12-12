package View.ToneMappers;

import Model.Image;

/**
 * Abstract representation of a tone mapping operator.
 *
 * @author sebastianzillessen
 */
public abstract class ToneMapping {
    private Image image;

    protected ToneMapping() {

    }

    /**
     * sets the generated image to null and causes a recalculation of the image.
     */
    protected void invalidateMapping() {
        this.image = null;
    }

    /**
     * Public entry point for the Tone-Mapping Plot wheren we return a 2d array of greyvalues to print it.
     *
     * @param e the radiance map (2d)
     * @return
     */
    public short[][] getValues(double[][] e) {
        minMax(e);
        e = mapToRange(e, 0, 1);
        minMax(e);
        return mapToShort(getValuesIntern(e), 0, 255);
    }

    /**
     * this method performs the actual tone mapping in the subclasses.
     *
     * @param e radiance map as 2 d array.
     * @return resulting tone mapped image
     */
    protected abstract double[][] getValuesIntern(double[][] e);


    /**
     * Gets the available variables of this tone mapper
     *
     * @return list of variables
     */
    public abstract String[] getVars();

    /**
     * returns the value of an parameter for this tone mapper
     *
     * @param s name (@see #getVars) of the tone mapper
     * @return current value
     */
    public abstract String getVar(String s);


    /**
     * method to set a variabale of the Tone Mapping operator
     *
     * @param var   name of the Variable
     * @param value value of the variable
     * @return
     */
    public abstract boolean setVar(String var, String value);


    protected double max;
    protected double min;

    /**
     * calculates the min and max of the radiance map e
     *
     * @param e radiance map as 2 d array.
     */
    protected void minMax(double[][] e) {
        min = min(e);
        max = max(e);
    }


    /**
     * Maps a value to a given range
     *
     * @param val       value to map
     * @param in_lower  lowest value in the input area
     * @param in_upper  highest value in the input area
     * @param out_lower lowest value in the output area
     * @param out_upper highest value in the output area
     * @return mapped val
     */
    protected double mapToRange(double val, double in_lower, double in_upper, double out_lower, double out_upper) {
        double out_range = out_upper - out_lower;
        double in_range = in_upper - in_lower;
        double in_val = val - in_lower;
        val = (in_val / in_range) * out_range;
        return out_lower + val;
    }

    /**
     * Maps a complete radiance map to a lower and upper bound.
     *
     * @param luminanceMap radiance map
     * @param lower        lowest output value
     * @param upper        hightest output value
     * @return mapped instance
     */
    protected double[][] mapToRange(double[][] luminanceMap, double lower, double upper) {
        double max = max(luminanceMap);
        double min = min(luminanceMap);
        double[][] res = new double[luminanceMap.length][luminanceMap[0].length];
        for (int i = 0; i < luminanceMap.length; i++) {
            for (int j = 0; j < luminanceMap[0].length; j++) {
                res[i][j] = mapToRange(luminanceMap[i][j], min, max, lower, upper);
            }
        }
        return res;

    }

    /**
     * Maps a complete radiance map to a lower and upper bound.
     * the result will be converted to short value
     *
     * @param luminanceMap radiance map
     * @param lower        lowest output value
     * @param upper        hightest output value
     * @return mapped instance
     */
    protected short[][] mapToShort(double[][] luminanceMap, double lower, double upper) {
        double[][] d = mapToRange(luminanceMap, lower, upper);
        short[][] res = new short[luminanceMap.length][luminanceMap[0].length];
        for (int i = 0; i < luminanceMap.length; i++) {
            for (int j = 0; j < luminanceMap[0].length; j++) {
                res[i][j] = (short) Math.round(d[i][j]);
            }
        }
        return res;
    }


    /**
     * Generates an image out of the value
     *
     * @param width   width of the image
     * @param height  height of the image
     * @param doubles irradiance map
     * @return Image representing this Tone Mapeed instance
     */
    public Image getImage(int width, int height, double[] doubles) {
        if (image == null) {
            image = new Image(width, height);
            double[][] d = new double[width][height];
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    d[w][h] = doubles[w + h * width];
                }
            }
            short[][] res = getValues(d);
            for (int x = 0; x < res.length; x++) {
                for (int y = 0; y < res[x].length; y++) {
                    image.set(x, y, res[x][y]);
                }
            }
        }
        return image;
    }


    /**
     * calculates max of 2d array
     *
     * @param e input
     * @return maximum
     */
    private double max(double[][] e) {
        double v = e[0][0];
        for (int i = 0; i < e.length; i++) {
            for (int j = 0; j < e[i].length; j++) {
                v = Math.max(e[i][j], v);
            }
        }
        return v;
    }

    /**
     * calculates min of 2d array
     *
     * @param e input
     * @return minimum
     */
    private double min(double[][] e) {
        double v = e[0][0];
        for (int i = 0; i < e.length; i++) {
            for (int j = 0; j < e[i].length; j++) {
                v = Math.min(e[i][j], v);
            }
        }
        return v;
    }
}
