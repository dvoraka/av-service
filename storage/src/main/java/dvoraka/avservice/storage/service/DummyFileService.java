package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.FileMessage;

import java.util.UUID;

/**
 * Dummy file service implementation.
 */
public class DummyFileService implements FileService {

    @Override
    public void saveFile(FileMessage message) {
    }

    @Override
    public FileMessage loadFile(String filename, UUID owner) {
        return null;
    }
}
