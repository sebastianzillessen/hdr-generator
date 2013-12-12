package View.ImageTable;

import View.EventListener.InvalidInputListener;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Table rederer which highlights empty cells with red background.
 * Has a invalidInputListener to inform other components if there are some invalid fields.
 *
 * @author sebastianzillessen
 */
public class HighlightEmptyRenderer extends DefaultTableCellRenderer {
    private List<InvalidInputListener> listeners = new ArrayList<InvalidInputListener>();
    private boolean invalid = true;


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row == 0 && column == 0)
            invalid = false;
        if (value == null) {
            setBackground(Color.RED);
            invalid = true;
        } else {
            if (isSelected)
                setBackground(Color.blue);
            else if (hasFocus)
                setBackground(Color.cyan);
            else
                setBackground(Color.white);

        }
        if (row + 1 == table.getRowCount() && column + 1 == table.getColumnCount()) {
            notifyListener(invalid);
        }
        return this;
    }


    /**
     * adds a listener to be fired when a cell is empty.
     *
     * @param i listener
     */
    public void addInvalidInputListener(InvalidInputListener i) {
        this.listeners.add(i);
    }

    /**
     * informs all listeners
     *
     * @param isInvalid
     */
    private void notifyListener(boolean isInvalid) {
        for (InvalidInputListener i : listeners)
            i.statusUpdate(isInvalid);
    }
}
