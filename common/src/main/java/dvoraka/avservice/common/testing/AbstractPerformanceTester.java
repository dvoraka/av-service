package dvoraka.avservice.common.testing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for performance tests.
 */
//TODO: implement
public abstract class AbstractPerformanceTester implements PerformanceTest {

    protected final Logger log = LogManager.getLogger(this.getClass());


    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean passed() {
        return false;
    }
}
