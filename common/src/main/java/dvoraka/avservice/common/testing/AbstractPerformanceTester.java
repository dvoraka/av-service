package dvoraka.avservice.common.testing;

import dvoraka.avservice.common.service.ApplicationManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for performance tests.
 */
//TODO: add timeout for tests
public abstract class AbstractPerformanceTester implements PerformanceTest, ApplicationManagement {

    @SuppressWarnings("checkstyle:VisibilityModifier")
    protected final Logger log = LogManager.getLogger(this.getClass());

    protected static final float MS_PER_SECOND = 1_000.0f;

    private volatile boolean running;
    private volatile boolean done;

    private boolean passed;
    private float result;


    @Override
    public boolean isRunning() {
        return running;
    }

    protected void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Returns messages per second.
     *
     * @return messages/second
     */
    @Override
    public long getResult() {
        return (long) result;
    }

    protected void setResult(float result) {
        this.result = result;
    }

    @Override
    public void run() {
        start();
    }

    @Override
    public boolean isDone() {
        return done;
    }

    protected void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public boolean passed() {
        return passed;
    }

    protected void setPassed(boolean passed) {
        this.passed = passed;
    }

    protected void startTest() {
        setPassed(false);
        setDone(false);
        setRunning(true);
    }

    protected void passTest() {
        setRunning(false);
        setDone(true);
        setPassed(true);
    }

    protected void failTest() {
        setRunning(false);
        setDone(true);
        setPassed(false);
    }
}
