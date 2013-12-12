package View.ImageTable;

import Ctrl.Controller;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Image Table model to represent the images listed in a table with the exposure time
 *
 * @author sebastianzillessen
 */
public class ImageTableModel extends AbstractTableModel {


    private Map<File, Float> fileMap;

    public ImageTableModel() {
        this.fileMap = new HashMap<File, Float>();
    }

    public ImageTableModel(File[] files) {
        this();
        setFiles(files);
    }

    @Override
    public int getRowCount() {
        return fileMap.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case 0:
                File f = ((File) fileMap.keySet().toArray()[row]);
                return f;
            case 1:
                return fileMap.values().toArray()[row];
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "File";
            case 1:
                return "Belichtungszeit";
            default:
                return "";
        }
    }

    /**
     * We want the second col to be editable
     *
     * @param row
     * @param col
     * @return
     */
    public boolean isCellEditable(int row, int col) {
        return (col == 1);
    }


    /**
     * handels the parsing of a changing value in the table.
     *
     * @param value
     * @param row
     * @param col
     */
    public void setValueAt(Object value, int row, int col) {
        try {
            if (col == 1) {
                fileMap.put((File) getValueAt(row, 0), Controller.getInstance().calculate(value.toString()));
                fireTableCellUpdated(row, col);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the files to display
     *
     * @param files
     */
    public void setFiles(File[] files) {
        this.fileMap.clear();
        for (File f : files) {
            fileMap.put(f, Controller.getInstance().extractExposureTime(f));
        }
        fireTableStructureChanged();
    }
}
