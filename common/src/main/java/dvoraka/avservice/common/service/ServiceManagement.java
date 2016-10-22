package dvoraka.avservice.common.service;

/**
 * Services management.
 */
public interface ServiceManagement {

    /**
     * Starts service.
     */
    void start();

    /**
     * Stops service.
     */
    void stop();

    /**
     * Restarts service.
     */
    void restart();

    /**
     * Check service status.
     */
    boolean isRunning();

    /**
     * Checks service status.
     *
     * @return the started status
     */
    boolean isStarted();

    /**
     * Checks service status.
     *
     * @return the stopped status
     */
    boolean isStopped();
}
