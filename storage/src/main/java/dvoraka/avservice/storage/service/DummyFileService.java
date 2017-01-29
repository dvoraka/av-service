package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.FileMessage;

/**
 * Dummy file service implementation.
 */
public class DummyFileService implements FileService {

    @Override
    public void saveFile(FileMessage message) {
    }

    @Override
    public FileMessage loadFile(FileMessage message) {
        return null;
    }
}
