package dvoraka.avservice.common.testing;

/**
 * Interface for tests.
 */
public interface Test {

    /**
     * Runs test.
     */
    void run();

    /**
     * Returns if the test is done.
     *
     * @return true if the test execution ended
     */
    boolean isDone();

    /**
     * Returns a test passed status.
     *
     * @return true if the test passed the test criteria
     */
    boolean passed();
}
