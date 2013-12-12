package Solver;

import Ctrl.Controller;
import Maths.*;
import Model.HDRResult;
import Model.Image;
import Model.WeightMode;

import java.util.List;

/**
 * Iterative Solver to get a HDRI from a series of LDR pictures.
 * This algorithm uses a extended version if Debevec and Malik and was developed as
 * Bachelor Thesis.
 *
 * @author sebastianzillessen
 * @see https://github.com/sebastianzillessen/hdr-generator
 */
public class IterativeEnergySolver extends IHDRSolver {
    private final boolean robustnessDataG;
    private final boolean robustnessSmoothnessE;
    private double alpha;
    private static final double EPSILON_2 = 0.0001d;
    private final int iterations;
    private final int N;
    private final int P;
    private WeightMode weightMode;
    private double mu = 50;
    private BandMatrix dt, d, weight;
    private final double lambda;

    private int[] histogram;
    private int energySteps = 2;
    private double[][] phi_data_g = null;
    private double ln_t[];


    /**
     * Default constructor to init a generation of an HDRI.
     * <p/>
     * To start the solving process please start the SwingWorker as Background task.
     *
     * @param images                the list of images with different exposure times
     * @param lambda                smoothness scaling factor           (0 disables this)
     * @param iterations            number of iterations to use in inner and outer iterations
     * @param mu                    monotonie scaling factor                             (0 disables this)
     * @param robustnessDataG       enable subquadratic penalty terms in the data term of g
     * @param robustnessSmoothnessE enable subquadratic penalty terms in the smoothness term for the radiance map
     * @param weightMode            which weight mode to use (@see WeightMode). Default ist WeightMode#normal
     * @param alpha                 smoothness term of E scaling factor (0 disables this)
     * @param update                the HDRSolverUpdateListener to be called on status changes.
     */
    public IterativeEnergySolver(List<Image> images,
                                 double lambda,
                                 int iterations,
                                 double mu,
                                 boolean robustnessDataG,
                                 boolean robustnessSmoothnessE,
                                 WeightMode weightMode,
                                 double alpha, HDRSolverUpdateListener update) {
        super(images, update);
        this.energySteps = Math.max(iterations / 3, 1);
        this.lambda = lambda;
        this.alpha = alpha;
        this.N = images.get(0).getImageSize();
        this.P = images.size();
        this.iterations = iterations;
        this.mu = mu;
        this.robustnessDataG = robustnessDataG;
        this.robustnessSmoothnessE = robustnessSmoothnessE;
        this.weightMode = weightMode;
        initLnT(images);
        // initialize robustness function with "1"
        initPhiData();
        generateOverallHistogramm();
        initWeightMatrix();
        initDerivativeMatrices();
    }

    /**
     * method which is called by the background swing worker to start this calculation of the HDRI.
     *
     * @return Returns HDR-Results as tempor. results with the current version of g and E.
     */
    @Override
    public void run() {
        try {
            // start value for g, lets assume we just use a linear equotation
            long started = System.currentTimeMillis();
            Vector g = new Vector(256);
            Vector F = new Vector(N);

            final double[] energy = new double[iterations / energySteps];
            g = initG(g);


            double[] w = new double[256];
            for (int i = 0; i < w.length; i++) {
                w[i] = w(i);
            }
            for (int i = 0; i < F.length(); i++)
                F.set(i, 1);

            for (int i = 0; i < iterations; i++) {
                Controller.getInstance().getDisplay().append("Running iteration " + i + " out of " + iterations);
                updateState(100 * i / iterations, new HDRResult(F.exp(), g, images.get(0).getWidth(), images.get(0).getHeight()));
                F = calculateF(g, F, i);
                g = calculateG(F, g, i);
                try {
                    energy[i / energySteps] = calculateEnergy(F, g);
                } catch (Exception e) {
                }
            }
            long finished = System.currentTimeMillis();
            Controller.getInstance().getDisplay().append("Took: " + (finished - started) / 1000.0 / 60.0 + "min");
            updateState(100, new HDRResult(F.exp(), g, images.get(0).getWidth(), images.get(0).getHeight()));
        } catch (Exception e) {
            throwError("Unbekannter Fehler aufgetreten: \n" + e.getMessage());
        }
    }

    /**
     * returns a string representation of the status of this Solver.
     *
     * @return String representation
     */
    @Override
    public String toString() {
        String s = "Iterativ Energy Solver:\n";
        s += " Monotonie:            " + (mu > 0 ? mu : "deaktiviert") + "\n";
        s += " Smoothness:           " + lambda + "\n";
        s += " Iterationen:          " + iterations + "\n";
        s += " Images:               " + P + "\n";
        s += " Image-Size:           " + N + "\n";
        s += " Weight-Mode:          " + weightMode.toString() + "\n";
        s += " Räumliche Glattheit:  " + (alpha > 0 ? alpha : "deaktiviert") + "\n";
        s += " Robustheit Datenterm :" + (robustnessDataG ? "aktiv" : "deaktiv") + "\n";
        s += " Robustheit Glattheit E:" + (robustnessSmoothnessE ? "aktiv" : "deaktiv") + "\n";

        return s;
    }


    /**
     * Gets the weightning value for a given grey value. It uses the specified weightning mode given in the constructur.
     *
     * @param z greyvalue
     * @return Weightning value
     */
    @Override
    protected double w(double z) {
        if (this.weightMode == WeightMode.NONE)
            return 1;
        else if (this.weightMode == WeightMode.PARABEL)
            return -(1.0 / 129) * z * z + (127.0 / 64) * z;
        else
            return super.w(z);
    }

    /**
     * initiates the factors for robust functions
     */
    private void initPhiData() {
        if (phi_data_g == null)
            phi_data_g = new double[images.get(0).getImageSize()][ln_t.length];
        for (int i = 0; i < phi_data_g.length; i++) {
            for (int j = 0; j < ln_t.length; j++) {
                phi_data_g[i][j] = 1;
            }
        }
    }

    /**
     * Generates the required derivative matrices for the calculation
     */
    private void initDerivativeMatrices() {
        int size = 256;
        d = new BandMatrix(size, new int[]{-1, 0});
        d.set(0, 0, 0);
        for (int row = 1; row < size; row++) {
            d.set(row, row - 1, -1);
            d.set(row, row, 1);
        }
        dt = d.transpose();
    }

    /**
     * inits the weight matrix. it is a diagonale matrix with the weights of w(z) on each entry
     */
    private void initWeightMatrix() {
        weight = new BandMatrix(256, new int[]{0});
        for (int i = 0; i < weight.cols(); i++) {
            weight.set(i, i, w(i));
        }
    }

    /**
     * Generates a histogram of all images in this serie.
     */
    private void generateOverallHistogramm() {
        // generate histogram over all pictures
        int[] d = images.get(0).getHistogram();
        for (int i = 1; i < P; i++) {
            for (int j = 0; j < d.length; j++) {
                d[j] += images.get(i).getHistogram()[j];
            }
        }
        histogram = d;
    }

    /**
     * generates a array to store the ln(t_j) in it
     *
     * @param images
     */
    private void initLnT(List<Image> images) {
        ln_t = new double[images.size()];
        for (int j = 0; j < ln_t.length; j++) {
            ln_t[j] = Math.log(t(j));
        }
    }

    /**
     * returns the quadratic weight
     *
     * @param z greyvalue (0-255)
     * @return w(z) * w(z)
     */
    private double w2(int z) {
        return w(z) * w(z);
    }

    /**
     * This method constructs a fourth derivative matrix approximation.
     *
     * @return fourth matrix derivative
     */
    private BandMatrix buildDerivativeMatrix() {
        int n = 256;
        BandMatrix d = new BandMatrix(n, new int[]{-2, -1, 0, 1, 2});
        d.set(0, 0, +1 * w2(1));
        d.set(0, 1, -2 * w2(1));
        d.set(0, 2, +1 * w2(1));
        // second row
        d.set(1, 0, -2 * w2(1));
        d.set(1, 1, 4 * w2(1) + w2(2));
        d.set(1, 2, -2 * (w2(1) + w2(2)));
        d.set(1, 3, w2(2));
        // diagonale
        for (int row = 2; row < n - 2; row++) {
            // default matrixG 2nd degree on the base.
            d.set(row, row - 2, w2(row - 1));
            d.set(row, row - 1, -2 * (w2(row - 1) + w2(row)));
            d.set(row, row, w2(row - 1) + 4 * w2(row) + w2(row + 1));
            d.set(row, row + 1, -2 * (w2(row) + w2(row + 1)));
            d.set(row, row + 2, w2(row + 1));
        }
        // second last row
        d.set(n - 2, n - 4, w2(253));
        d.set(n - 2, n - 3, -2 * (w2(253) + w2(254)));
        d.set(n - 2, n - 2, w2(253) + 4 * w2(254));
        d.set(n - 2, n - 1, -2 * w2(254));

        // last row
        d.set(n - 1, n - 3, w2(254));
        d.set(n - 1, n - 2, -2 * w2(254));
        d.set(n - 1, n - 1, w2(254));
        return d.mult(2 * lambda);
    }

    /**
     * Alternating solving step to calculate the discrete function g out of the Vector f and the old value of g
     *
     * @param F         current value of F (ln E_i)
     * @param g         last calculated version of vector g (used as initialization)
     * @param iteration iteration count
     * @return new version of g
     */
    private Vector calculateG(Vector F, Vector g, int iteration) {
        int MAX_ITERATIONS = 1;
        if (robustnessDataG || mu > 0) {
            MAX_ITERATIONS = iterations;
        }
        for (int iterations = 0; iterations < MAX_ITERATIONS; iterations++) {
            //update_phi_smooth(g);
            update_phi_data(g, F);
            BandMatrix m = buildDerivativeMatrix();
            Vector b = initializeB(F, g);
            // add on the diagonale the Matrix with the sums of each grayvalue in the picture.
            // Entry (k,k) says how many time the grayvalue k is present overall pictures and
            // is added to the derivate matrix above
            setupDataTerm(m);

            if (mu > 0) {
                m = monotonieConstraint(g, m);
            }


            try {
                g = EquationSolver.solve(m, b, EquationSolverAlgorithm.LU);
            } catch (EquationSolverException e) {
                Controller.getInstance().getDisplay().append("Error on calculation of g in iteration " + iteration + ". Skipping this iteration and processing to next one. " + e.getMessage());
            }
            // fix g to be zero at grey value 127
            g = g.subtract(g.get(127));

        }
        return g;
    }


    /**
     * sets up the data term in the given band matrix m. if robustness is required in this data term it is addes as factors.
     *
     * @param m the current matrix.
     */
    private void setupDataTerm(BandMatrix m) {
        // DATA TERM
        for (int k = 0; k < m.rows(); k++) {
            double t = 0;
            if (robustnessDataG) {
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < P; j++) {
                        if (Z(i, j) == k) {
                            t += w2(k) * phi_data_g[i][j];
                        }
                    }
                }
            } else {
                t = w(k) * histogram[k];
            }
            m.set(k, k, m.get(k, k) + t);
        }
    }

    /**
     * returns a matrix which represents the monotonie constraint. The result is the addition of the monotonie matrix and
     * the current calculation matrix. This matrix will be returned.
     *
     * @param g last calculated version of g
     * @param m the current band Matrix
     * @return Matrix m + monotonie constraint matrix
     */
    private BandMatrix monotonieConstraint(Vector g, BandMatrix m) {
        BandMatrix vwwv = new BandMatrix(256, new int[]{0});
        for (int i = 1; i < g.length(); i++) {
            double diff = g.get(i - 1) - (g.get(i));
            if (diff > 0) {
                vwwv.set(i, i, w2(i));
            }
        }

        // mu *(dt * vt * wt * w * v * d)
        // = mu (dt * vwwv * d)
        BandMatrix mon = dt.mult(vwwv.mult(d)).mult(mu);
        return m.add(mon);
    }

    /**
     * sets the vector for the right hand side of a calculation. It keeps all the stuff like adding robustness functions
     * and so on encapsulated from the user.
     *
     * @param F    old Vector F (ln E)
     * @param oldG old Version of the discrete function g
     * @return Vector b for the right hand side.
     */
    private Vector initializeB(Vector F, Vector oldG) {
        Vector b;
        b = new Vector(oldG.length());
        for (int k = 0; k < b.length(); k++) {
            double w = (robustnessDataG ? w2(k) : w(k));
            double s = 0;
            for (int i = 0; i < F.length(); i++) {
                for (int j = 0; j < ln_t.length; j++) {
                    if (Z(i, j) == k) {
                        double t = F.get(i) + ln_t[j];
                        if (robustnessDataG) {
                            t *= phi_data_g[i][j];
                        }
                        s += t;
                    }
                }
            }
            b.set(k, s * w);
        }
        return b;
    }

    /**
     * Calculates the new Version of F (ln E). If required it integrates the neighbors in the calculation.
     *
     * @param g         current instance of reponse curve g
     * @param F         current instance of the radiance map
     * @param iteration current number of iteration
     * @return
     */
    private Vector calculateF(Vector g, Vector F, int iteration) {
        int MAX_ITERATIONS = 1;
        if (!robustnessDataG) {
            MAX_ITERATIONS = iterations;
        }
        for (int it = 0; it < MAX_ITERATIONS; it++) {
            if (robustnessDataG) {
                update_phi_data(g, F);
            }
            if (alpha > 0)
                try {
                    return calculateFWithNeighboorhood(g, F, alpha);
                } catch (EquationSolverException e) {
                    Controller.getInstance().getDisplay().append("Exception detected during calculation of the neighbor matrix. Swapping to default algorithm. " + e.getMessage());
                    return solveFDefault(g, F);
                }
            else
                F = solveFDefault(g, F);
        }
        return F;
    }

    /**
     * Default solving algorithm of F (ln E). Does not include smoothness in the picture. Just calculates the new version
     * of F by using the old values.
     * <p/>
     * Might includes subquadratic functions for the calculation.
     *
     * @param g response curve
     * @param f current value of F
     * @return new instance of F
     */
    private Vector solveFDefault(Vector g, Vector f) {
        double quot;
        double div;
        double t;
        for (int i = 0; i < N; i++) {
            quot = 0;
            div = 0;
            for (int j = 0; j < P; j++) {
                if (robustnessDataG) {
                    // Zähler
                    t = w2(Z(i, j)) * phi_data_g[i][j] * (g.get(Z(i, j)) - ln_t[j]);
                    quot += t;
                    // Nenner
                    t = w2(Z(i, j)) * phi_data_g[i][j];
                    div += t;
                } else {
                    quot += g.get(Z(i, j)) - ln_t[j] * w2(Z(i, j));
                    div += w2(Z(i, j));
                }
            }
            f.set(i, quot / div);
        }
        return f;
    }


    /**
     * Inits the vector g with a linear curve g(127)=0, g(0) = -5, g(256) = 5
     *
     * @param g vector to be inited.
     * @return inited vector
     */
    private Vector initG(Vector g) {
        for (int i = 0; i < g.length(); i++)
            g.set(i, -5 + i * 5.0 / 127.0);
        return g;
    }

    /**
     * updates the coefficients for the subquadratic calculations.
     *
     * @param g current instance of reponse curve g
     * @param F current value of F (ln E_i)
     */
    private void update_phi_data(Vector g, Vector F) {
        if (robustnessDataG) {
            for (int i = 0; i < F.length(); i++) {
                for (int j = 0; j < ln_t.length; j++) {
                    phi_data_g[i][j] = 1.0 / (2.0 * Math.sqrt(Math.pow(g.get(Z(i, j)) - F.get(i) - ln_t[j], 2) + EPSILON_2));
                }
            }
        }
    }

    /**
     * calculates the Energy of a entire Picture set for given irradiance values E[0..N-1] and a given
     * function g where g is specified as vector (the entry i in the vector represents the value for the function g(i) )
     *
     * @param F - the log of the irradiance values 0..N-1 (N is the picture size) (log(Ei)!!!)
     * @param g - the function g (the entry i in the vector represents the value of g(i) )
     * @return the value of the energy functional  SUM(i=1,N,SUM(j=1,P,[g(Z_ij)-ln(E_i)-ln(dt_j)]^2))+lambda*SUM(z=1,254,g''(z)^2)
     */
    private double calculateEnergy(Vector F, Vector g) {
        // data term
        double data = 0.0;
        for (int i = 0; i < F.length(); i++) {
            for (int j = 0; j < images.size(); j++) {
                // w^2(Z(i,j))*phi([g(Z(i,j))-ln(E_i)-ln(t_j)]^2)
                double w = w2(Z(i, j));
                double inner = g.get(Z(i, j)) - (F.get(i)) - ((ln_t[j]));
                double phi = Math.sqrt(Math.pow(inner, 2) + EPSILON_2);
                data += w * phi;
            }
        }
        // smoothning term
        double smoothing = 0.0;
        for (int z = 1; z < 255; z++) {
            double g2 = g.get(z - 1) - 2 * g.get(z) + g.get(z + 1);
            double phi = Math.sqrt(Math.pow(g2, 2) + EPSILON_2);
            smoothing += w2(z) * phi;
        }
        // monotonie term
        double monotonie = 0.0;
        for (int z = 1; z <= 255; z++) {
            // w(z)*(g'(z)<0 * g'(z))^2
            double g1 = g.get(z - 1) - (g.get(z));
            if (g1 < 0)
                monotonie += w(z) * g1 * g1;
        }
        return data + lambda * smoothing + mu * monotonie;
    }


    /**
     * Solves the calculation of new values for F (ln E) with activated influence of the neighbors in the resulting radiance map.
     *
     * @param g     current instance of reponse curve g
     * @param F     current value of F (ln E_i)
     * @param alpha smoothness term of E scaling factor (0 disables this)
     * @return the new instance of F
     * @throws EquationSolverException if the equotation could not be solved because of some issues.
     */
    private Vector calculateFWithNeighboorhood(Vector g, Vector F, double alpha) throws EquationSolverException {
        int cols = images.get(0).getWidth();
        int rows = images.get(0).getHeight();

        BandMatrix res = generateNeighborsBandMatrix(F, alpha, cols, rows);
        Vector b = new Vector(F.length());
        for (int i = 0; i < b.length(); i++) {
            double sum = 0;
            for (int j = 0; j < P; j++) {
                sum += w2(Z(i, j)) * (g.get(Z(i, j)) - ln_t[j]);
            }
            b.set(i, sum);
        }
        return EquationSolver.solve(res, b, EquationSolverAlgorithm.SOR);
    }


    /**
     * returns the coefficient if a robust subquadratic penalty function is required.
     *
     * @param F current value of F (ln E_i)
     * @param i first index in F
     * @param j second index in F
     * @return coefficient for the 2d smoothness of F
     */
    private double phi_smoothness_e(Vector F, int i, int j) {
        if (robustnessSmoothnessE) {
            double v = F.get(i) - F.get(j);
            if (v == 0)
                return 1;
            else
                return 1.0 / (2 * Math.sqrt(v * v + EPSILON_2));
        } else
            return 1;
    }


    /**
     * Generates the Matrix which calculates the influence of each pixel on the others. The distance of influence is only 1.
     * <p/>
     * The returning matrix will be a band matrix with 5 setted elements (the central, top, left, right, bottom pixel).
     * The center pixel will be the positive sum of the 4 surrounding pixels.
     *
     * @param F     current instance of the radiance map
     * @param alpha the factor of the smoothness term
     * @param cols  number of cols in the picture
     * @param rows  number of rows in the picture
     * @return BandMatrix which represents the (scaled) influence of the surrounding pixels
     */
    private BandMatrix generateNeighborsBandMatrix(Vector F, double alpha, int cols, int rows) {
        BandMatrix neighborsBandMatrix = new BandMatrix(cols * rows, new int[]{-cols, -1, 0, 1, cols});
        for (int i = 0; i < cols * rows; i++) {
            // weight
            double sum = 0;
            for (int j = 0; j < P; j++) {
                sum += w2(Z(i, j));
            }
            double d = 0;
            // left band
            if (i - 1 >= 0) {
                double v = phi_smoothness_e(F, i, i - 1);
                d += v;
                neighborsBandMatrix.set(i - 1, i, -v * alpha);
            }
            // right band
            if (i + 1 < cols * rows) {
                double v = phi_smoothness_e(F, i + 1, i);
                d += v;
                neighborsBandMatrix.set(i + 1, i, -v * alpha);
            }
            // upper band
            if (i - cols >= 0) {
                double v = phi_smoothness_e(F, i, i - cols);
                d += v;
                neighborsBandMatrix.set(i - cols, i, -v * alpha);
            }
            // lower band
            if (i + cols < cols * rows) {
                double v = phi_smoothness_e(F, i + cols, i);
                d += v;
                neighborsBandMatrix.set(i + cols, i, -v * alpha);
            }

            neighborsBandMatrix.set(i, i, sum + alpha * d);

        }
        return neighborsBandMatrix;
    }
}
