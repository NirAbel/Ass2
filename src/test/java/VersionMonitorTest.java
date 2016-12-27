import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import bgu.spl.a2.VersionMonitor;
import static org.junit.Assert.*;

/**
 * Created by win10 on 27-Dec-16.
 */
public class VersionMonitorTest {
    protected VersionMonitor monitor;

    @Before
    public void setUp() throws Exception {
        monitor = createVersionMonitor();
        monitor.version=0;
    }
    protected VersionMonitor createVersionMonitor() {
        return new VersionMonitor();
    }
    @After
    public void tearDown() throws Exception{
        monitor.version=0;
    }
    @Test
    public void testGetVersion() throws Exception {
        assertEquals(0,monitor.getVersion());
        monitor.inc();
        assertEquals(1,monitor.getVersion());
    }
    @Test
    public void testInc() throws Exception {
        int check=monitor.getVersion();
        monitor.inc();
        assertEquals(check+1,monitor.getVersion());
    }
    @Test
    public void testAwait() throws Exception {
        int ver=monitor.getVersion();
        Thread t=new Thread(()-> {
            try {
                monitor.await(ver);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        monitor.inc();
        try {t.join();
        }
        catch (InterruptedException exp){
            exp.printStackTrace();}
        assertNotSame(ver,monitor.getVersion());
    }
}