package dvoraka.avservice.common.service;

/**
 * Interface for a service management.
 */
public interface ServiceManagement extends ApplicationManagement {

    /**
     * Stops the service.
     */
    void stop();

    /**
     * Restarts the service.
     */
    default void restart() {
        stop();
        start();
    }

    /**
     * Checks a service started status.
     *
     * @return the started status
     */
    boolean isStarted();

    /**
     * Checks a service stopped status.
     *
     * @return the stopped status
     */
    boolean isStopped();
}
