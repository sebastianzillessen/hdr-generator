package Maths;

import java.util.Arrays;

/**
 * This helper class provides possibilities to solve linear equotations with different algorithms.
 *
 * @author Sebastian Zillessen
 */
public class EquationSolver {

    private static int MAX_ITERATIONS_SOR = 100;
    private static double ACCEPTED_DIFFERENCE_SOR = 0.001;
    private static double ACCEPTED_PERCENTAGE_RESIDUUM_SOR = 0.02;
    private static double OMEGA_SOR = 1.2;


    /**
     * Method to solve a system of linear equotations of the structure A * x = b.
     * We use an LU-Decomposition internall
     *
     * @param A         The matrix A of the system of linear equotations.
     * @param b         The right side of the system of linear equotations
     * @param algorithm The algorithm which should be used.
     * @return
     */
    public static Vector solve(AbstractMatrix A, Vector b, EquationSolverAlgorithm algorithm) throws EquationSolverException {
        if (algorithm == EquationSolverAlgorithm.LU) {
            return solveWithLU(A, b);
        } else if (algorithm == EquationSolverAlgorithm.SOR) {
            return solveWithSOR(A, b);
        } else {
            throw new IllegalArgumentException("This solving strategy is not supported");
        }
    }

    /**
     * solves a linear equotation system of the kind
     * A * x = b
     * where A is the matrix itself (this) and x is the result of the function.
     * The Matrix A has to be in pentadiagonal structure (so only the 5 center diagonales
     * are allowed to have values).
     *
     * @param a pentadiagonale Matrix
     * @param b the vector on the right sight of the LGS
     * @return the vector x.
     */
    private static Vector solveWithLU(AbstractMatrix a, Vector b) throws EquationSolverException {
        if (!a.isQuadratic())
            throw new EquationSolverException("Matrix A is not quadratic.", a, b);
        if (b.length() != a.rows())
            throw new EquationSolverException("Size of Vextor and Matrix does not match.", a, b);
        if (!a.isPentadiagonale())
            throw new EquationSolverException("Matrix A is not pentadiagonale.", a, b);
        AbstractMatrix[] LU = a.decomposePenta();
        // forward elimination L*y = b
        Vector y = forwardElimination(b, LU[0]);
        // backward substitution U*g = y
        return backwardSubstitution(LU[1], y);
    }

    /**
     * Solves an equation with the iterative SOR algorithm.
     * The matrix needs to be quadratic, symmetric and positiv semi definite.
     *
     * @param a Matrix
     * @param b Vector
     * @return Result vector of a * x = b
     * @throws EquationSolverException if the matrix is not symetric, quadratic or positiv semi definite or
     *                                 if the size of the vector and the matrix do not match.
     */
    private static Vector solveWithSOR(AbstractMatrix a, Vector b) throws EquationSolverException {
        if (!a.isQuadratic())
            throw new EquationSolverException("Matrix A is not quadratic.", a, b);
        if (b.length() != a.rows())
            throw new EquationSolverException("Size of Vextor and Matrix does not match.", a, b);
        if (!a.isSymmetric())
            throw new EquationSolverException("Matrix A is not symmetric.", a, b);
        if (!a.isPositiveSemiDefinit())
            throw new EquationSolverException("Matrix A is not positive-semi-definite.", a, b);

        if (!(a instanceof BandMatrix))
            throw new EquationSolverException("Matrix A is no Band-Matrix. SOR-Solver should only be used on them.", a, b);
        return solveSORBand((BandMatrix) a, b);
    }


    /**
     * Forward Elimination (only used in LU decomposition with pentadiagonale matrizes)
     *
     * @param b Vector b
     * @param L lower matrix of the LU decomposition.
     * @return temporary result
     */
    private static Vector forwardElimination(Vector b, AbstractMatrix L) {
        Vector y = new Vector(b.length());
        y.set(0, b.get(0));
        y.set(1, b.get(1) - L.get(1, 0) * y.get(0));
        for (int i = 2; i < b.length(); i++) {
            y.set(i, b.get(i) - L.get(i, i - 2) * y.get(i - 2) - L.get(i, i - 1) * y.get(i - 1));
        }
        return y;
    }

    /**
     * Backward substitution (only used in LU decomposition with pentadiagonale matrizes)
     *
     * @param y Vector y
     * @param U upper matrix of the LU decomposition.
     * @return Result (if forward elimination was used result of LGS with A = L * U)
     */

    private static Vector backwardSubstitution(AbstractMatrix U, Vector y) {
        int n = y.length();
        Vector g = new Vector(n);
        g.set(n - 1, y.get(n - 1) / U.get(n - 1, n - 1));
        g.set(n - 2, (y.get(n - 2) - U.get(n - 2, n - 1) * g.get(n - 1)) / U.get(n - 2, n - 2));
        for (int i = n - 3; i >= 0; i--) {
            double d = U.get(i, i);
            double v = U.get(i, i + 1) * g.get(i + 1) / d;
            double v1 = U.get(i, i + 2) * g.get(i + 2) / d;
            double v2 = y.get(i) / d;
            g.set(i, v2 - v - v1);
        }
        return g;
    }


    /**
     * solves an system of equations if it is a band matrix efficient with SOR
     * @param a Matrix (band)
     * @param b Vector (right side)
     * @return x so that A * x = b
     */
    private static Vector solveSORBand(BandMatrix a, Vector b) {
        double vec[] = b.toArray();
        double x[] = new Vector(a.rows(), 1).toArray();

        double first_res = -1;
        double old_x[] = new double[x.length];
        for (int iterations = 0; iterations <= MAX_ITERATIONS_SOR; iterations++) {
            for (int row = 0; row < a.rows(); row++) {
                double phi = 0;
                for (int i = 0; i < a.bandIndexes.length; i++) {
                    int col = row + a.bandIndexes[i];
                    if (col >= 0 && col < a.cols() && col != row) {
                        phi += a.get(row, col) * x[col];
                    }
                }
                x[row] = x[row] + OMEGA_SOR * ((vec[row] - phi) / a.get(row, row) - x[row]);
            }

            if (iterations % 10 == 0) {
                // Are the break conditions met?
                // http://www.home.hs-karlsruhe.de/~weth0002/buecher/mathe/downloads/kap21.pdf, S 143
                try {
                    double max_abs_res = a.mult(x).subtract(b).absMax();
                    if (first_res == -1)
                        first_res = max_abs_res;
                    max_abs_res /= first_res;
                    double max_diff = ArrayMaths.diffMax(x, old_x);
                    if (max_abs_res < ACCEPTED_PERCENTAGE_RESIDUUM_SOR && max_diff < ACCEPTED_DIFFERENCE_SOR) {
                        break;
                    }
                } catch (ArithmeticException e) {
                }
            }
            old_x = Arrays.copyOf(x, x.length);
        }
        return new Vector(x);
    }

}
