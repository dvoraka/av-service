package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.FileMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Dummy file service implementation.
 */
public class DummyFileService implements FileService {

    private static final Logger log = LogManager.getLogger(DummyFileService.class);


    @Override
    public void saveFile(FileMessage message) {
        log.info("Save: {}", message);
    }

    @Override
    public FileMessage loadFile(FileMessage message) {
        log.info("Load: {}", message);
        return null;
    }

    @Override
    public void updateFile(FileMessage message) {
        log.info("Update: {}", message);
    }

    @Override
    public void deleteFile(FileMessage message) {
        log.info("Delete: {}", message);
    }

    @Override
    public boolean exists(String filename, String owner) {
        log.info("Exists: {}, {}", filename, owner);
        return false;
    }
}
