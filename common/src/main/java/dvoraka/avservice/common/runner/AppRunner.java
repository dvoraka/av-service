package dvoraka.avservice.common.runner;

/**
 * Application runner interface.
 */
public interface AppRunner {

    /**
     * Runs runner.
     */
    void run();

    /**
     * Runs runner asynchronously.
     */
    void runAsync();

    /**
     * Returns a running status.
     *
     * @return the running flag
     */
    boolean isRunning();
}
