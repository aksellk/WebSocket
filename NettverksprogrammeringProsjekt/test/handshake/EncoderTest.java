package handshake;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for class Encoder
 * @author Aksel
 */
public class EncoderTest {
    
    public EncoderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createKey method, of class Encoder.
     */
    @Test
    public void testCreateKey() {
        System.out.println("createKey");
        String key = "hKSeRnDaneLmm+e286TRyg==";
        Encoder instance = new Encoder();
        String expResult = "NbmUIscoI8Z4smeNcMZBVg5cx2Y=";
        String result = instance.createKey(key);
        assertEquals(expResult, result);
    }
    
}
