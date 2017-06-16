package dvoraka.avservice.common.runner;

/**
 * Application runner interface.
 */
public interface AppRunner {

    /**
     * Runs the runner.
     */
    void run();

    /**
     * Runs the runner asynchronously.
     */
    void runAsync();

    /**
     * Returns a running status.
     *
     * @return the running flag
     */
    boolean isRunning();
}
