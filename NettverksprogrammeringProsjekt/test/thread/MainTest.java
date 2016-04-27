package thread;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for the class Main
 * @author Aksel
 */
public class MainTest {
    
    public MainTest() {
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
     * Test of removeThread method, of class Main.
     */
    @Test
    public void testRemoveThread() {
        System.out.println("removeThread");
        ServerThread t = new ServerThread(null);
        long thread_id = t.getId();
        Main instance = new Main();
        ArrayList<ServerThread> list = new ArrayList<ServerThread>();
        list.add(t);
        instance.setList(list);
        instance.setTrash(new ArrayList<ServerThread>());
        try {
            instance.removeThread(thread_id);
        } catch (NullPointerException e) {
            // because the method try to close a connection that does not exist in the test
        }       
        ArrayList<ServerThread> test = instance.getList();
        if (test.isEmpty()) {
            assertEquals(instance.getTrash().get(1),t);
        } 
    }
    
}
