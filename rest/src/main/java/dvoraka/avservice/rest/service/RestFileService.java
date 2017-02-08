package dvoraka.avservice.rest.service;

import dvoraka.avservice.common.data.AvMessage;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * REST file service interface.
 */
@Validated
public interface RestFileService {

    /**
     * Saves a file from the message.
     *
     * @param message the message.
     */
    void saveMessage(@Valid AvMessage message);

    /**
     * Loads an AV message with a given description.
     *
     * @param message the message
     * @return the loaded message
     */
    AvMessage loadMessage(AvMessage message);

    /**
     * Updates a file.
     *
     * @param message the update message
     */
    void updateMessage(AvMessage message);

    /**
     * Deletes a file.
     *
     * @param message the delete message
     */
    void deleteMessage(AvMessage message);
}
