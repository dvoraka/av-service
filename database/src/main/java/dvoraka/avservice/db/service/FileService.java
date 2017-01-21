package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.FileMessage;

import java.util.UUID;

/**
 * File service interface.
 */
public interface FileService {

    void saveFile(FileMessage message);

    FileMessage loadFile(String filename, UUID owner);
}
