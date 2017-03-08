package dvoraka.avservice.rest.service;

import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.server.client.service.AvServiceClient;
import dvoraka.avservice.server.client.service.FileServiceClient;

/**
 * Main REST service.
 */
public interface RestService extends AvServiceClient, FileServiceClient {

    /**
     * Returns a message status.
     *
     * @param id the message ID
     * @return the status
     */
    MessageStatus messageStatus(String id);

    /**
     * Starts the service.
     */
    void start();

    /**
     * Stops the service.
     */
    void stop();
}
