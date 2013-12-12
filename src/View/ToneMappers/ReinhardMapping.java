package View.ToneMappers;

import Maths.AbstractMatrix;
import Maths.Matrix;

/**
 * Implements the Reinhard global Tone-Mapper
 * parameters:
 * hdr: high dynamic range radiance map, a matrix of size rows * columns * 3
 * % luminance map: the corresponding lumiance map of the hdr image
 * <p/>
 * Matlab Code from : http://cybertron.cg.tu-berlin.de/eitz/hdr/
 *
 * @author sebastianzillessen , Eitz
 */
public class ReinhardMapping extends ToneMapping {

    private double a;

    public ReinhardMapping(double a) {
        this.a = a;
    }

    /**
     * calculation
     *
     * @param lum
     * @return
     */
    @Override
    public double[][] getValuesIntern(double[][] lum) {
        double[][] e = super.mapToRange(lum, 0, 1);
        int numPixels = e.length * e[0].length;
        double sum = 0;
        double delta = 0.000000001;
        for (int i = 0; i < e.length; i++) {
            for (int j = 0; j < e[i].length; j++) {
                sum += Math.log(e[i][j] + delta);
            }
        }
        double key = Math.exp((1.0 / numPixels) * sum);
        AbstractMatrix m = new Matrix(e).mult(a / key);

        double[][] scaledLuminance = m.toArray();

        double[][] r = new double[e.length][e[0].length];
        for (int i = 0; i < r.length; i++) {
            for (int j = 0; j < r[i].length; j++) {
                double v = scaledLuminance[i][j] / (scaledLuminance[i][j] + 1) * 255.0;
                scaledLuminance[i][j] = v;
                r[i][j] = v;

            }
        }
        return r;
    }

    /**
     * WE only have one parameter
     */
    protected enum VARS {
        a
    }

    /**
     * returns the varaiables
     *
     * @return
     */
    public String[] getVars() {
        String[] vars = new String[VARS.values().length];
        for (int i = 0; i < vars.length; i++)
            vars[i] = VARS.values()[i].toString();
        return vars;
    }

    /**
     * gets a value of a parameter
     * @param s name (@see #getVars) of the tone mapper
     * @return value
     */
    public String getVar(String s) {
        switch (VARS.valueOf(s)) {
            case a:
                return this.a + "";
        }
        return null;
    }

    /**
     * sets a value
     * @param var   name of the Variable
     * @param value value of the variable
     * @return  true if was setable
     */
    public boolean setVar(String var, String value) {
        try {
            Double d = Double.valueOf(value);
            if (d.isNaN())
                return false;
            switch (VARS.valueOf(var)) {
                case a:
                    a = d.doubleValue();
                    break;
                default:
                    return false;
            }
            invalidateMapping();
            return true;
        } catch (NumberFormatException e) {

        }

        return false;
    }
}
