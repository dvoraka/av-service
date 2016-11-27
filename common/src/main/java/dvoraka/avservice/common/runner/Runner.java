package dvoraka.avservice.common.runner;

/**
 * Runner interface.
 */
public interface Runner {

    /**
     * Runs something.
     */
    void run();

    /**
     * Runs asynchronously.
     */
    void runAsync();

    /**
     * Returns a running status.
     *
     * @return the running flag
     */
    boolean isRunning();

    /**
     * Stops something.
     */
    void stop();
}
