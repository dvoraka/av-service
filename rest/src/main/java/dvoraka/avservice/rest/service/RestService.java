package dvoraka.avservice.rest.service;

import dvoraka.avservice.common.data.MessageStatus;
import org.springframework.validation.annotation.Validated;

/**
 * Main REST service.
 */
@Validated
public interface RestService extends RestAvService, RestFileService {

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
