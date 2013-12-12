package Model;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by sebastianzillessen on 30.11.13.
 */
public class WeightModeTest {
    @Test
    public void length() {
        assertEquals(3, WeightMode.values().length);
    }
}
