package dvoraka.avservice.checker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for App.
 */
public class AppTest {

    @Before
    public void setUp() {

        System.out.println("Set up");
    }

    @After
    public void tearDown() {

        System.out.println("Tear down");
    }

    @Test
    public void testApp() {

        assertTrue(true);
    }
}
