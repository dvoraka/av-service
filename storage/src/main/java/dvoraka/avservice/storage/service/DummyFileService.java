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

    @Override
    public void updateFile(FileMessage message) {
    }

    @Override
    public void deleteFile(FileMessage message) {
    }
}
