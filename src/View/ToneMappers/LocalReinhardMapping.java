package View.ToneMappers;

import Maths.Convolution;

/**
 * Implements the Reinhard local View.ToneMappers operator
 * parameters:
 * hdr: high dynamic range radiance map, a matrix of size rows * columns * 3
 * % luminance map: the corresponding lumiance map of the hdr image
 * <p/>
 * Matlab Code from : http://cybertron.cg.tu-berlin.de/eitz/hdr/
 *
 * @author sebastianzillessen, Eitz
 */
public class LocalReinhardMapping extends ToneMapping {

    private double phi;
    private double eps;
    private double saturation;
    private double key;


    /**
     * constructor for local Reinhard mapping algorithm
     *
     * @param saturation
     * @param eps
     * @param phi
     * @param key
     */
    public LocalReinhardMapping(double saturation, double eps, double phi, double key) {
        this.saturation = saturation;
        this.eps = eps;
        this.phi = phi;
        this.key = key;
    }


    /**
     * actual tone mapping function, called by super
     *
     * @param luminance irradiance map
     * @return byte map with grey values
     */
    @Override
    public double[][] getValuesIntern(double[][] luminance) {

        double[][] luminanceNormalized = mapToRange(luminance, 0.0, 1.0);
        /*Implements the Reinhard local View.ToneMappers operator

         parameters:
         hdr: high dynamic range radiance map, a matrix of size rows * columns * 3
                % luminance map: the corresponding lumiance map of the hdr image
        */
        double alpha = 1 / (2 * Math.sqrt(2));

        double[][][] v1 = new double[9][luminanceNormalized.length][luminanceNormalized[0].length];
        double[][][] v = new double[8][luminanceNormalized.length][luminanceNormalized[0].length];

        double s = 0;
        //compute nine gaussian filtered version of the hdr luminance map, such
        //that we can compute eight differences.Each image gets filtered by a
        //standard gaussian filter, each time with sigma 1.6 times higher than
        //the sigma of the predecessor.
        int maxScale = 1;
        for (int scale = 0; scale < 9; scale++) {

            //s = exp(sigma0 + ((scale) / range) * (sigma1 - sigma0)) * 8
            s = Math.pow(1.6, (scale));

            double sigma = alpha * s;

            // dicretize gaussian filter to a fixed size kernel.
            // a radius of 2 * sigma should keep the error low enough...
            int kernelRadius = (int) Math.ceil(2 * sigma);
            int kernelSize = 2 * kernelRadius + 1;


            //double[][] conv = convoluteGaussianHorizontal(luminanceMap, kernelSize, sigma);
            //conv = convoluteGaussianVertival(conv, kernelSize, sigma);

            double[][] gaussianKernel2D = Convolution.getGaussianKernel2D(kernelRadius, sigma);
            maxScale = scale;
            if (kernelSize < luminanceNormalized.length && kernelSize < luminanceNormalized[0].length) {
                double[][] conv = Convolution.convolute(luminanceNormalized, gaussianKernel2D);
                v1[scale] = conv;
            } else {
                break;
            }
        }
        maxScale = Math.min(8, maxScale);

        for (int i = 0; i < maxScale; i++) {
            for (int j = 0; j < v[i].length; j++) {
                for (int k = 0; k < v[i][j].length; k++) {
                    double v2 = Math.pow(2, phi) * key / Math.pow(s, 2) + v1[i][j][k];
                    double v3 = v1[i][j][k] - v1[i + 1][j][k];
                    v[i][j][k] = Math.abs(v3) / v2;
                }
            }

        }


        int[][] sm = new int[luminanceNormalized.length][luminanceNormalized[0].length];

        for (int i = 0; i < sm.length; i++) {
            for (int j = 0; j < sm[i].length; j++) {
                sm[i][j] = -1;
                for (int scale = 0; scale < maxScale; scale++) {
                    // choose the biggest possible neighbourhood where v (i, j, scale)
                    //is still smaller than a certain epsilon.
                    // Note that we need to choose that neighbourhood which is
                    // as big as possible but all smaller neighbourhoods also
                    //fulfill v (i, j, scale)<eps !!!
                    if (v[scale][i][j] > eps) {
                        // if we already have a high contrast change in the
                        // first scale we can only use that one
                        if (scale == 0) {
                            sm[i][j] = 0;
                        }
                        //if we have a contrast change bigger than epsilon, we
                        //know that in scale scale - 1 the contrast change was
                        //smaller than epsilon and use that one
                        if (scale > 0) {
                            sm[i][j] = scale - 1;
                        }
                        break;
                    }
                }
            }
        }

        //all areas in the pic that have very small variations and therefore in
        // any scale no contrast change>epsilon will not have been found in
        // the loop above.
        // We manually need to assign them the biggest possible scale.
        //
        // idx = find(sm == 0);
        // sm(idx) = 8;
        //new Matrix(sm).toFileSync("calc/sm_before.txt");
        for (int i = 0; i < sm.length; i++) {
            for (int j = 0; j < sm[i].length; j++) {
                if (sm[i][j] == -1) {
                    sm[i][j] = maxScale;
                }
            }
        }
        //new Matrix(sm).toFileSync("calc/sm_after.txt");

        double[][] v1Final = new double[luminanceNormalized.length][luminanceNormalized[0].length];

        //build the local luminance map with luminance values taken
        // from the neighbourhoods with appropriate scale
        for (int x = 0; x < v1Final.length; x++) {
            for (int y = 0; y < v1Final[x].length; y++) {
                v1Final[x][y] = v1[sm[x][y]][x][y];
            }
        }
        //TODO:
        //try local scaling with a/key as in the global operator.
        //But compute key for each chosen neighbourhood !
        // numPixels = size(hdr, 1) * size(hdr, 2);
        //delta = 0.0001;
        //key = exp((1 / numPixels) * (sum(sum(log(v1Final + delta)))))
        // scaledLuminance = v1Final * (a / key);
        //luminanceCompressed = (luminanceMap * (a / key)). / (1 + scaledLuminance);


        //Do the actual View.ToneMappers
        double[][] luminanceCompressed = new double[luminanceNormalized.length][luminanceNormalized[0].length];
        for (int i = 0; i < luminanceNormalized.length; i++) {
            for (int j = 0; j < luminanceNormalized[i].length; j++) {
                double l = luminanceNormalized[i][j] * 255.0 / (1.0 + v1Final[i][j]);
                luminanceCompressed[i][j] = l;
            }
        }
        return luminanceCompressed;
    }

    /**
     * Our parameters
     */
    protected enum VARS {
        PHI, EPS, SATURATION, KEY
    }

    /**
     * @return the list of parameters
     */
    public String[] getVars() {
        String[] vars = new String[VARS.values().length];
        for (int i = 0; i < vars.length; i++)
            vars[i] = VARS.values()[i].toString();
        return vars;
    }

    /**
     * Gets the vlaue of a parameter
     * @param s name (@see #getVars) of the tone mapper
     * @return
     */
    public String getVar(String s) {
        switch (VARS.valueOf(s)) {
            case EPS:
                return this.eps + "";
            case PHI:
                return this.phi + "";
            case SATURATION:
                return this.saturation + "";
            case KEY:
                return this.key + "";
        }
        return null;
    }

    /**
     * Sets a value of a parameter
     * @param var   name of the Variable
     * @param value value of the variable
     * @return
     */
    public boolean setVar(String var, String value) {
        try {
            Double d = Double.valueOf(value);
            if (d.isNaN())
                return false;
            switch (VARS.valueOf(var)) {
                case EPS:
                    eps = d.doubleValue();
                    break;
                case PHI:
                    phi = d.doubleValue();
                    break;
                case SATURATION:
                    saturation = d.doubleValue();
                    break;
                case KEY:
                    key = d.doubleValue();
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
