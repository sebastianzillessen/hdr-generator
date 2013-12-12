package Tests.Model;

import Maths.Vector;
import Model.HDRResult;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created by sebastianzillessen on 30.11.13.
 */
public class HDRResultTest {
    @Test
    public void testGetG() throws Exception {
        Vector g = new Vector(256);
        Vector E = new Vector(1);
        HDRResult h = new HDRResult(E, g, 10, 10);
        assertEquals(h.getG(), g);

    }

    @Test
    public void testGetGFail() throws Exception {
        Vector g = new Vector(1);
        Vector E = new Vector(1);
        try {
            new HDRResult(E, g, 10, 10);
            fail();
        } catch (IndexOutOfBoundsException e) {

        }
    }

    @Test
    public void testGetE() throws Exception {
        Vector g = new Vector(256);
        Vector E = new Vector(1);
        HDRResult h = new HDRResult(E, g, 10, 10);
        assertEquals(h.getE(), E);
    }

    @Test
    public void testGetSize() throws Exception {
        HDRResult h = new HDRResult(new Vector(1), new Vector(256), 20, 10);
        assertEquals(10, h.getHeight());
        assertEquals(20, h.getWidth());
    }
}
