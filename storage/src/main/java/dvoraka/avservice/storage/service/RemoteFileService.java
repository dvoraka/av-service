package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.FileMessage;

import java.util.UUID;

/**
 * File service with remote connection.
 */
public class RemoteFileService implements FileService {

    private final FileService service;


    public RemoteFileService(FileService service) {
        this.service = service;
    }

    @Override
    public void saveFile(FileMessage message) {

    }

    @Override
    public FileMessage loadFile(String filename, UUID owner) {
        return null;
    }
}
