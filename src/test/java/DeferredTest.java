import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import bgu.spl.a2.Deferred;

import static com.sun.xml.internal.ws.dump.LoggingDumpTube.Position.Before;
import static org.junit.Assert.*;

/**
 * Created by win10 on 27-Dec-16.
 */
public class DeferredTest {
        Deferred<Integer> deferr;

        @Before
        public void setUp() throws Exception {
            deferr = createDeferred();
        }

        protected Deferred<Integer> createDeferred() {
            return new Deferred<Integer>();
        }

        @Test
        public void testGet() throws Exception {
            deferr.resolve(40);
            Assert.assertEquals(40, deferr.get().intValue());
        }

        @Test
        public void testIsResolved() throws Exception {
            Assert.assertEquals(false, deferr.isResolved());
            deferr.resolve(80);
            Assert.assertEquals(true, deferr.isResolved());
        }


        @Test
        public void testResolve() throws Exception {
            deferr.resolve(80);
            Assert.assertEquals(true, deferr.isResolved());
        }


        @Test
        public void testWhenResolved() throws Exception {
            Runnable currentTask = () -> {Assert.assertEquals(50, deferr.get().intValue());};

        }
    }