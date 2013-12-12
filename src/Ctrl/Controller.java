package Ctrl;

import Maths.Vector;
import Model.HDRResult;
import Model.Image;
import Model.WeightMode;
import Solver.HDRSolverUpdateListener;
import Solver.IHDRSolver;
import Solver.IterativeEnergySolver;
import View.GUIFrame;
import View.Plots.ScatterPlot;
import View.Plots.ToneMappingPlot;
import View.ToneMappers.LocalReinhardMapping;
import View.ToneMappers.ReinhardMapping;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Controller of the application. It generates a view and starts the calculation of an HDR-Image.
 * <p/>
 * Access to the controller is available via the singelton pattern.
 *
 * @author sebastianzillessen
 */
public class Controller implements HDRSolverUpdateListener {

    private static final double PERCENTAGE_OF_SALT_N_PEPPER = 0.02;
    private GUIFrame display;

    private static Controller ourInstance = null;
    private SwingWorker<HDRResult, Vector> worker;
    private IHDRSolver solver;
    private ArrayList<Image> images;


    /**
     * Application entrance point. Generates a new controller instance.
     *
     * @param args
     */
    public static void main(String[] args) {
        ourInstance = new Controller();
    }


    /**
     * Singelton pattern for the controller.
     *
     * @return
     */
    public static Controller getInstance() {
        if (ourInstance == null) {
            ourInstance = new Controller();
        }
        return ourInstance;
    }


    /**
     * Constructor for a new controller. Generates the GUI.
     */
    private Controller() {
        display = new GUIFrame("HDR-Solver");
        display.append("Ready...");
    }


    /**
     * Method to start the generation of an HDR Image. Is called by the UI and updates the UI asynchron.
     *
     * @param lambda                weight factor for the smoothness term
     * @param iterations            number of iterations for the inner and outer iterations of the solver
     * @param mu                    weight factor for the monotonie constraint
     * @param robustnessDataG       true: subquadratic penalty functions are used in the calculation of the response curve
     * @param robustnessSmoothnessE true: subquadratic penalty functions are used for the 2d smoothness term of the resulting HDR image
     * @param weight                weightning mode which should be used to weight the greyvalues during the complete calculations
     * @param alpha                 weight factor of the 2d smoothness term of the resulting irradiance map
     */
    public void solve(double lambda, final int iterations, double mu, boolean robustnessDataG, boolean robustnessSmoothnessE, WeightMode weight, double alpha) {

        if (solver != null) {
            display.alert("Prozess bereits gestartet und kann nicht ein zweites Mal gestartet werden.");
        } else {
            solver = new IterativeEnergySolver(images, lambda, iterations, mu, robustnessDataG, robustnessSmoothnessE, weight, alpha, this);
            display.append(solver.toString());
            solver.execute();
        }
    }


    /**
     * Reads a list of images from a set if filenames and exposure times.
     * It adds noise if desired.
     * Old images get deleted and are not preserved.
     *
     * @param imgList            map of filename and exposure time. Each entry is a image.
     * @param saltAndPepperNoise adds salt and pepper noise if true
     * @param gaussianNoise      add gaussian with a standard derivation of this value (0 is no noise)
     * @throws
     */
    public void readImages(Map<String, Float> imgList, boolean saltAndPepperNoise, double gaussianNoise) {
        display.append("Reading files...");
        images = new ArrayList<Image>();
        for (Map.Entry<String, Float> e : imgList.entrySet()) {
            try {
                Image image = new Image(e.getKey(), e.getValue());
                images.add(image);
            } catch (Exception x) {
                display.alert("Datei '" + e.getKey() + "' konnte nicht gelesen werden und wurde übersprungen. (" + x.getMessage() + ")");
            }
        }

        for (Image image : images) {
            // add salt and pepper nois if required
            if (saltAndPepperNoise)
                image.addSaltAndPepper(PERCENTAGE_OF_SALT_N_PEPPER);
            // add gauss noise if required
            if (gaussianNoise > 0)
                image.addGaussian(gaussianNoise);
            display.append(image);
        }


    }


    /**
     * Returns the corresponding UI to be able to display informations.
     *
     * @return current instance of the UI
     */
    public GUIFrame getDisplay() {
        return display;
    }


    @Override
    public void updateState(int progress, HDRResult hdrResult) {
        //Controller.getInstance().getDisplay().addPlot(new ScatterPlot(E), "E(" + getProgress() + "%)");
        display.setProgress(progress);
        if (hdrResult != null) {
            ScatterPlot p = new ScatterPlot(hdrResult.getG());
            p.setXDescription("Grauwert");
            p.setYDescription("ln E(i)");
            display.addPlot(p, "g(" + progress + "%)");
            if (progress >= 100) {
                display.addPlot(new ToneMappingPlot(hdrResult, new LocalReinhardMapping(0.6, 0.05, 8.0, 0.18)), "LocalReinhardMapping");
                display.addPlot(new ToneMappingPlot(hdrResult, new ReinhardMapping(0.72)), "Reinhard");
            }
        }

    }

    /**
     * this method is going to be called if some errors occured in the solver. If available the last calculated result will be returned.
     *
     * @param message    Error message
     * @param lastResult last result if one is available (null otherwise)
     */
    @Override
    public void errorOccured(String message, HDRResult lastResult) {
        display.alert(message + "\n Falls verfügbar wird ein Zwischenresultat angezeigt.");
        if (lastResult != null) {
            display.addPlot(new ToneMappingPlot(lastResult, new LocalReinhardMapping(0.6, 0.05, 8.0, 0.18)), "LocalReinhardMapping");
            display.addPlot(new ToneMappingPlot(lastResult, new ReinhardMapping(0.72)), "Reinhard");
        }
    }

    /**
     * read exposure time first by trying to extract it from metadata, then by reading the filename.
     *
     * @param f file name to read
     * @return Exposure time if detectable, otherwise null
     */
    public Float extractExposureTime(File f) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(f);
            ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
            return directory.getFloat(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
        } catch (Exception e) {
            return calculate(f.getName());
        }
    }

    /**
     * calculates the exposure time of a string (1/5 will result to 0.2)
     *
     * @param s string to be parsed
     * @return Exposure time if detectable, otherwise null
     */
    public Float calculate(String s) {
        try {
            Double v = Double.valueOf(s);
            if (v != null && !v.isNaN())
                return v.floatValue();

        } catch (Exception e) {

        }
        try {

            String filename = s.split("\\.")[0].replace(":", "/");
            ScriptEngineManager mgr = new ScriptEngineManager();
            ScriptEngine engine = mgr.getEngineByName("JavaScript");
            return Float.parseFloat(engine.eval(String.valueOf(filename)).toString());
        } catch (Exception e) {
            return null;
        }
    }


}
