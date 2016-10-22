package dvoraka.avservice.server;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.ProcessedAvMessageListener;

/**
 * Anti-virus server interface.
 */
public interface AvServer extends AvMessageListener, ProcessedAvMessageListener {

    /**
     * Starts server.
     */
    void start();

    /**
     * Stops server.
     */
    void stop();

    /**
     * Restarts server.
     */
    void restart();
}
