package dvoraka.avservice;

/**
 * Anti-virus server interface.
 */
public interface AVServer {

    /**
     * Starts server.
     */
    void start();

    /**
     * Stops server.
     */
    void stop();

    /**
     * Checks server status.
     *
     * @return started status
     */
    boolean isStarted();

    /**
     * Checks server status.
     *
     * @return stopped status
     */
    boolean isStopped();

    /**
     * Restarts server.
     */
    void restart();
}
