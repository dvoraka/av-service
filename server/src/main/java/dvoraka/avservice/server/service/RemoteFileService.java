package dvoraka.avservice.server.service;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Client file service with a remote connection.
 */
public interface RemoteFileService {

    void saveFile(AvMessage message);

    AvMessage loadFile(AvMessage message);

    void updateFile(AvMessage message);

    void deleteFile(AvMessage message);
}
