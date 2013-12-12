package View.EventListener;

import java.util.EventListener;

/**
 * Listener to listen on invalid inputs in the UI.
 *
 * @author sebastianzillessen
 */
public interface InvalidInputListener extends EventListener {
    /**
     * Fired when the status is updated.
     *
     * @param invalid if true we have some invalid input fields in the UI.
     */
    void statusUpdate(boolean invalid);
}
