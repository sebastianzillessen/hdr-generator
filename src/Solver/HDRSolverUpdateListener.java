package Solver;

import Model.HDRResult;

/**
 * Interface which is used to inform the caller asynchronously of the progress of the execution.
 *
 * @author sebastianzillessen
 */
public interface HDRSolverUpdateListener {
    /**
     * method is called from the IHDRSolver whenever a new temp. result is available or when the process is finished.
     *
     * @param progress  progress in percent. 100% = finished
     * @param hdrResult temporary (or final) HDR result.
     */
    public void updateState(int progress, HDRResult hdrResult);

    /**
     * this method is going to be called if some errors occured in the solver. If available the last calculated result will be returned.
     *
     * @param message    Error message
     * @param lastResult last result if one is available (null otherwise)
     */
    public void errorOccured(String message, HDRResult lastResult);
}
