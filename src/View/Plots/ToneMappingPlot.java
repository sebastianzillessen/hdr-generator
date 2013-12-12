package View.Plots;

import Model.HDRResult;
import View.ToneMappers.ToneMapping;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays a HDR image with a given tone-mapper on the Screen.
 *
 * @author sebastianzillessen
 */
public class ToneMappingPlot extends ImagePlot {
    private final HDRResult r;
    private ToneMapping mapping;
    private JTextField[] inputs;
    private String[] vars;
    private JPanel pnl;

    /**
     * Constructor for a TOneMapping Plot.
     * This plot has controlls to change the parameters of the tone mapping operator.
     *
     * @param r       HDR Result which contains the response curve and the Radiance map
     * @param mapping ToneMapping algorithm.
     */
    public ToneMappingPlot(HDRResult r, ToneMapping mapping) {
        super(mapping.getImage(r.getWidth(), r.getHeight(), r.getE().toArray()));
        this.r = r;
        this.mapping = mapping;
        appendControl();
    }

    /**
     * extended controls in the south area of the panel.
     *
     * @return
     */
    @Override
    protected JPanel buildControls() {
        pnl = super.buildControls();
        return pnl;
    }

    /**
     * builds the control. the parameters are taken from ToneMapper.VARS.
     */
    private void appendControl() {
        vars = mapping.getVars();
        inputs = new JTextField[vars.length];
        if (vars.length == 0)
            return;
        final JPanel ctrlPanel = new JPanel(new GridLayout(vars.length + 2, 1));
        ctrlPanel.add(new JLabel("Parameter"));
        final JLabel loading = new JLabel();
        ctrlPanel.add(loading);
        final JButton doit = new JButton("Parameter übernehmen");
        doit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                doit.setEnabled(false);
                final Thread spinner = new Thread(new Runnable() {
                    private int count = 0;
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                count++;
                                String s = "Bitte warten";
                                for (int i = 0; i < count % 3; i++)
                                    s += ".";
                                loading.setText(s);
                                Thread.sleep(600);
                            }
                        } catch (InterruptedException e) {
                            loading.setText("");
                        }
                    }
                });
                spinner.start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean error = false;
                        for (int i = 0; i < vars.length; i++) {
                            if (!mapping.setVar(vars[i], inputs[i].getText())) {
                                inputs[i].setBorder(BorderFactory.createLineBorder(Color.red));
                                error = true;
                            } else {
                                inputs[i].setBorder(BorderFactory.createLineBorder(Color.gray));
                            }
                        }
                        if (!error) {
                            ToneMappingPlot.super.setImage(mapping.getImage(r.getWidth(), r.getHeight(), r.getE().toArray()));
                            redraw();
                        }
                        doit.setEnabled(true);
                        spinner.interrupt();
                    }
                }).start();

            }
        });

        for (int i = 0; i < vars.length; i++) {
            ctrlPanel.add(new JLabel(vars[i]));
            JTextField input = new JTextField(mapping.getVar(vars[i]));
            inputs[i] = input;
            ctrlPanel.add(input);
        }
        ctrlPanel.add(new JLabel());
        ctrlPanel.add(doit);
        this.pnl.add(ctrlPanel, BorderLayout.NORTH);

    }


    /**
     * Updates the input fields to the current value
     */
    private void updateInputFields() {
        for (int i = 0; i < vars.length; i++) {
            inputs[i].setText(mapping.getVar(vars[i]));
        }
    }


}
