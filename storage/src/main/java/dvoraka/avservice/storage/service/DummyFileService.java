package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.FileMessage;

/**
 * Dummy file service implementation.
 */
public class DummyFileService implements FileService {

    @Override
    public void saveFile(FileMessage message) {
        // do nothing
    }

    @Override
    public FileMessage loadFile(FileMessage message) {
        return null;
    }

    @Override
    public void updateFile(FileMessage message) {
        // do nothing
    }

    @Override
    public void deleteFile(FileMessage message) {
        // do nothing
    }

    @Override
    public boolean exists(String filename, String owner) {
        return false;
    }
}
