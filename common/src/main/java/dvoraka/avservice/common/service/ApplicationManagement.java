package dvoraka.avservice.common.service;

/**
 * Interface for application management.
 */
public interface ApplicationManagement {

    /**
     * Starts application.
     */
    void start();

    /**
     * Check application status.
     *
     * @return the running status
     */
    boolean isRunning();
}
