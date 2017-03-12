package dvoraka.avservice.rest.service;

import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.client.service.FileServiceClient;
import dvoraka.avservice.client.service.ResponseClient;
import dvoraka.avservice.common.data.MessageStatus;

/**
 * Main REST service.
 */
public interface RestService extends AvServiceClient, FileServiceClient, ResponseClient {

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
