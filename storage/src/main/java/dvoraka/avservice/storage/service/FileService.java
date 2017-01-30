package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.FileMessage;

/**
 * File service interface.
 */
public interface FileService {

    void saveFile(FileMessage message);

    FileMessage loadFile(FileMessage message);

    void updateFile(FileMessage message);

    void deleteFile(FileMessage message);
}
