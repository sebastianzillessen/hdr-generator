package Maths;

/**
 * Convolution is the code for applying the convolution operator.
 *
 * @author: Simon Horne, Sebastian Zillessen
 * @see: http://www.inf.ufpr.br/danielw/pos/ci724/20102/HIPR2/flatjavasrc/Convolution.java
 */
public class /**/Convolution {
    private Convolution() {

    }

    /**
     * Returns a approximation of a gaussian kernel as stencil for the 1D.
     *
     * @param radius Radius in pixels
     * @param sigma  sigma for the gaussian to use
     * @return
     */
    public static double[] getGaussianKernel1D(int radius, double sigma) {
        double[] res = new double[radius * 2 + 1];
        double norm = 1.0 / (Math.sqrt(2 * Math.PI) * sigma);
        double coeff = 2 * sigma * sigma;
        double total = 0;
        for (int x = -radius; x <= radius; x++) {
            double g = norm * Math.exp(-x * x / coeff);
            res[x + radius] = g;
            total += g;
        }
        for (int x = 0; x < res.length; x++) {
            res[x] /= total;
        }
        return res;
    }

    /**
     * Returns a approximation of a gaussian kernel as stencil for the 2D case.
     *
     * @param radius Radius in pixels
     * @param sigma  sigma for the gaussian to use
     * @return
     */
    public static double[][] getGaussianKernel2D(int radius, double sigma) {
        double[] gaus = getGaussianKernel1D(radius, sigma);
        double[][] res = new double[gaus.length][gaus.length];
        for (int x = 0; x < gaus.length; x++) {
            for (int y = 0; y < gaus.length; y++) {
                res[x][y] = gaus[x] * gaus[y];
            }
        }
        return res;
    }


    /**
     * Takes an image (grey-levels) and a kernel and a position,
     * applies the convolution at that position and returns the
     * new pixel value.
     *
     * @param input The 2D double array representing the image.
     * @param x     The x coordinate for the position of the convolution.
     * @param y     The y coordinate for the position of the convolution.
     * @param k     The 2D array representing the kernel.
     * @return The new pixel value after the convolution.
     */
    public static double singlePixelConvolution(double[][] input,
                                                int x, int y,
                                                double[][] k) {
        int kernelWidth = k.length;
        int kernelHeight = k[0].length;
        double output = 0;
        for (int i = 0; i < kernelWidth; ++i) {
            for (int j = 0; j < kernelHeight; ++j) {
                if (x + i >= 0 && y + j >= 0 && x + i < input.length && y + j < input[0].length)
                    output += (input[x + i][y + j] * k[i][j]);
            }
        }
        return output;
    }


    /**
     * Takes a 2D array of grey-levels and a kernel, applies the convolution
     * over the area of the image specified by width and height and returns
     * a part of the final image.
     *
     * @param input  the 2D double array representing the image
     * @param kernel the 2D array representing the kernel
     * @return the 2D array representing the new image
     */
    public static double[][] convolute(double[][] input,
                                       double[][] kernel
    ) {
        double large[][] = new double[input.length][input[0].length];
        for (int i = 0; i < input.length; ++i) {
            for (int j = 0; j < input[i].length; ++j) {
                large[i][j] = singlePixelConvolution(input, i - kernel.length / 2, j - kernel.length / 2, kernel);
            }
        }
        return large;
    }

}
