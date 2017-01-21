package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.FileMessage;

/**
 * File service interface.
 */
public interface FileService {

    void saveFile(FileMessage message);
}
