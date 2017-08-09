package dvoraka.avservice.common.service;

/**
 * Interface for getting service status.
 */
public interface ServiceStatus {

    /**
     * If service is running.
     *
     * @return the running status
     */
    boolean isRunning();
}
