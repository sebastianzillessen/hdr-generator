package Ctrl;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sebastianzillessen on 08.12.13.
 */
public class ControllerTest {
    @Test
    public void testGetInstance() throws Exception {
        Controller instance = Controller.getInstance();
        assertNotNull(instance);
        assertTrue(Controller.getInstance() instanceof Controller);
        assertEquals(Controller.getInstance(), instance);
    }




    @Test
    public void testGetDisplay() throws Exception {
        assertNotNull(Controller.getInstance().getDisplay());
    }


    @Test
    public void testCalculate() throws Exception {
        assertEquals(0.2, Controller.getInstance().calculate("1/5"), 0.0001);
        assertEquals(0.2, Controller.getInstance().calculate("1:5"), 0.0001);
        assertEquals(0.2, Controller.getInstance().calculate("10/50"), 0.0001);
        assertEquals(0.2, Controller.getInstance().calculate("0.2"), 0.0001);
        assertEquals(null, Controller.getInstance().calculate("test"));
    }
}
