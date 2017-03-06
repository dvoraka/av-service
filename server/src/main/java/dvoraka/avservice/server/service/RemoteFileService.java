package dvoraka.avservice.server.service;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Client file service with a remote connection.
 */
public interface RemoteFileService {

    void saveFile(AvMessage message);

    void loadFile(AvMessage message);

    void updateFile(AvMessage message);

    void deleteFile(AvMessage message);

    /**
     * Returns a response message.
     *
     * @param id the request message ID
     * @return the response message or null if message is not available
     */
    AvMessage getResponse(String id);
}
