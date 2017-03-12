package dvoraka.avservice.client.service;

import dvoraka.avservice.common.data.AvMessage;

/**
 * File service client. It is used for client connections to the file service.
 *
 * @see dvoraka.avservice.storage.service.FileService
 */
public interface FileServiceClient {

    /**
     * Saves a file from the message.
     *
     * @param message the message.
     */
    void saveFile(AvMessage message);

    /**
     * Loads a message with a given description.
     *
     * @param message the load message
     */
    void loadFile(AvMessage message);

    /**
     * Updates a file.
     *
     * @param message the update message
     */
    void updateFile(AvMessage message);

    /**
     * Deletes a file.
     *
     * @param message the delete message
     */
    void deleteFile(AvMessage message);
}
