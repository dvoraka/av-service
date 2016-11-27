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
     * Returns a running status.
     *
     * @return the running flag
     */
    boolean isRunning();
}
