package dvoraka.avservice.server.service;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Client file service with a remote connection.
 */
public interface RemoteFileService {

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

    /**
     * Returns a response message.
     *
     * @param id the request message ID
     * @return the response message or null if message is not available
     */
    AvMessage getResponse(String id);
}
