package View;

/**
 * Interface to provide some basic debugging and logging functionality.
 *
 * @author sebastianzillessen
 */
public interface Log {

    /**
     * Appends a new Line with the object s as output (calls to String on it e.g)
     *
     * @param s the object to print
     */
    public void append(Object s);


    /**
     * appends the object s as output (no new line before)
     *
     * @param s the object to print
     */
    public void write(Object s);

    /**
     * Displays an alert which is normally used when an error occurs.
     * @param s the message to display.
     */
    public void alert(String s);
}
