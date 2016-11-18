package dvoraka.avservice.common.testing;

/**
 * Interface for performance tests.
 */
public interface PerformanceTest extends Test {

    /**
     * Returns a result for the test.
     *
     * @return the test result
     */
    long getResult();
}
