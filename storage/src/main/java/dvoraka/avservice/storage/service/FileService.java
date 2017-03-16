package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.storage.ExistingFileException;

/**
 * File service interface.
 */
public interface FileService {

    void saveFile(FileMessage message) throws ExistingFileException;

    FileMessage loadFile(FileMessage message);

    void updateFile(FileMessage message);

    void deleteFile(FileMessage message);

    boolean exists(String filename, String owner);
}
