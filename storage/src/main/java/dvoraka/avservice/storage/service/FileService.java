package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.storage.FileServiceException;

/**
 * File service interface.
 */
public interface FileService {

    void saveFile(FileMessage message) throws FileServiceException;

    FileMessage loadFile(FileMessage message);

    void updateFile(FileMessage message) throws FileServiceException;

    void deleteFile(FileMessage message);

    boolean exists(String filename, String owner);

    default boolean exists(FileMessage message) {
        return exists(message.getFilename(), message.getOwner());
    }
}
