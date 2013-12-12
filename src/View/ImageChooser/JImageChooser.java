package View.ImageChooser;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * File chooser that allows only to select images and offers a preview for the user.
 *
 * @author sebastianzillessen
 * @see http://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html#FileChooserDemo2
 */
public class JImageChooser extends JFileChooser {
    public JImageChooser(String folder) {
        super();
        setAcceptAllFileFilterUsed(false);
        setFileFilter(new ImageFilter());
        setFileView(new ImageFileView());
        setAccessory(new ImagePreview(this));
        setCurrentDirectory(new File(folder));
        setMultiSelectionEnabled(true);
        setName("Bilder wählen");
        addRegistrationHint();
    }

    private void addRegistrationHint() {
        JPanel hint = new JPanel(new BorderLayout(5, 5));
        hint.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel lbl = new JLabel("Hinweis:");
        lbl.setForeground(Color.red);
        hint.add(lbl, BorderLayout.NORTH);
        hint.add(new JLabel("<html>Die hier ausgewählten Bilder müssen registriert sein, damit der Algorithmus korrekt arbeitet. Bei nicht registrierten Bildern kann es zu Problemen bei der Erstellung des HDR-Bildes kommen.</html>"), BorderLayout.CENTER);

        add(hint);
    }
}
