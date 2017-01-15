package dvoraka.avservice.common.service;

/**
 * Interface for service management.
 */
public interface ServiceManagement extends ApplicationManagement {

    /**
     * Stops service.
     */
    void stop();

    /**
     * Restarts service.
     */
    default void restart() {
        stop();
        start();
    }

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
