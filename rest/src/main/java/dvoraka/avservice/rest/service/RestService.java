package dvoraka.avservice.rest.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * Main REST service.
 */
@Validated
public interface RestService extends RestFileService {

    /**
     * Returns a message status.
     *
     * @param id the message ID
     * @return the status
     */
    MessageStatus messageStatus(String id);

    /**
     * Checks a file from the message.
     *
     * @param message the AV message
     */
    void checkMessage(@Valid AvMessage message);

    /**
     * Returns a response AV message.
     *
     * @param id the request message ID
     * @return the response AV message or null if message is not available
     */
    AvMessage getResponse(String id);

    /**
     * Starts the service.
     */
    void start();

    /**
     * Stops the service.
     */
    void stop();
}
