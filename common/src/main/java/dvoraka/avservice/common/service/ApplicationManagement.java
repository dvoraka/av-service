package dvoraka.avservice.common.service;

/**
 * Interface for an application management.
 */
public interface ApplicationManagement {

    /**
     * Starts an application.
     */
    void start();

    /**
     * Checks an application running status.
     *
     * @return the running status
     */
    boolean isRunning();
}
