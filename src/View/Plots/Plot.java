package View.Plots;

import Ctrl.Controller;
import View.ImageChooser.ImageFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Represents a Plot to display in the UI.
 *
 * @author sebastianzillessen
 */
public abstract class Plot extends JPanel {

    private final JPanel pnl;
    protected String outputFileName;
    private boolean zoom = false;


    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    /**
     * constructor for a plot which inits the controlls and sets the size.
     */
    public Plot() {
        super(new BorderLayout());
        add(buildControls(), BorderLayout.SOUTH);
        pnl = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintPlot(g);
            }
        };
        pnl.setPreferredSize(new Dimension(getWidth(), getHeight()));
        add(pnl, BorderLayout.CENTER);
        pnl.repaint();
    }


    /**
     * stores the buffered image to a file
     *
     * @param filename filename
     * @param buff     buffered image to store
     * @return true if saving was successfull
     */
    public boolean saveGraphic(String filename, BufferedImage buff) {
        if (!filename.endsWith(".png"))
            filename += ".png";
        try {
            ImageIO.write(buff, "png", new File(filename));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Saves the current displayed content to a file.
     *
     * @param filename filename
     * @return true if plot was saved.
     */
    public boolean saveGraphic(String filename) {
        BufferedImage bi = new BufferedImage(getPlotWidth(), getPlotHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        this.pnl.paint(g);  //this == JComponent
        g.dispose();
        return saveGraphic(filename, bi);
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        pnl.repaint();
    }

    protected void redraw() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                pnl.repaint();
            }
        });
    }

    protected abstract void paintPlot(Graphics g);

    protected int getPlotWidth() {
        return pnl.getWidth();
    }


    protected int getPlotHeight() {
        return pnl.getHeight();
    }


    protected JPanel buildControls() {
        JPanel pnl = new JPanel(new BorderLayout());
        JButton save = new JButton("Speichern");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Plot.this.save();
            }
        });
        pnl.add(save, BorderLayout.SOUTH);
        return pnl;
    }

    protected void save() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new ImageFilter());

        int returnVal = fileChooser.showSaveDialog(this);


        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String filename = file.getCanonicalPath();
                if (!saveGraphic(filename)) {
                    Controller.getInstance().getDisplay().alert("Couldn not save the File!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
