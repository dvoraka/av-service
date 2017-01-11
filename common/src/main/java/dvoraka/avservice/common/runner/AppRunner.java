package dvoraka.avservice.common.runner;

/**
 * Application unner interface.
 */
public interface AppRunner {

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
}
