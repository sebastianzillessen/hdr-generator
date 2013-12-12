package Solver;

import Model.HDRResult;
import Model.Image;

import java.util.List;

/**
 * This class represents a abstract implementation of an IHDRSolver.
 * <p/>
 * It can be used to implement other HDR-Solvers if desired.
 * <p/>
 * Created with IntelliJ IDEA.
 * User: sebastianzillessen
 * Date: 02.07.13
 */
public abstract class IHDRSolver implements Runnable {
    private HDRSolverUpdateListener update;
    List<Image> images;
    private HDRResult lastResult = null;

    /**
     * accessible constructor for subclasses to init the images
     *
     * @param images images of this hdr solver.
     * @param update the iterative solver update listner. HDRSolverUpdateListener#updateState is fired whenever there is a status change.
     */
    protected IHDRSolver(List<Image> images, HDRSolverUpdateListener update) {
        this.images = images;
        this.update = update;
    }


    /**
     * Executes the calculation. HDRSolverUpdateListener#updateState is fired whenever there is a status change.
     */
    public void execute() {
        Thread t = new Thread(this);
        t.start();
    }

    protected void updateState(int percent, HDRResult h) {
        lastResult = h;
        update.updateState(percent, h);
    }

    protected void throwError(String message) {
        throwError(message, lastResult);
    }

    protected void throwError(String message, HDRResult res) {
        update.errorOccured(message, res);
    }

    private IHDRSolver() {

    }


    /**
     * Returns the grey value i of picture j
     *
     * @param i index of grey value
     * @param j index of picture
     * @return greyvalue at index i of picture j
     */
    protected int Z(int i, int j) {
        return images.get(j).getValue(i);
    }


    /**
     * returns the time of the exposure time of the picture j
     *
     * @param j index of picture
     * @return exposure time of picture j
     */
    protected double t(int j) {
        return images.get(j).getExposureTime();
    }

    /**
     * the weighning function which should be used for the algorithm. Default is a triangle function.
     *
     * @param z greyvalue
     * @return weight for the greyvalue z
     */
    protected double w(double z) {
        return Math.max((z <= 127) ? z + 1 : 256 - z, 0.0001);
    }
}
