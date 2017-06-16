package dvoraka.avservice.common.testing;

/**
 * Interface for performance test properties.
 */
public interface PerformanceTestProperties extends TestProperties {

    /**
     * Returns a message count for a test.
     *
     * @return the message count
     */
    long getMsgCount();

    /**
     * If a test sends a data without receiving.
     *
     * @return the send only flag
     */
    boolean isSendOnly();

    /**
     * Maximum rate per second.
     *
     * @return the rate
     */
    long getMaxRate();
}
