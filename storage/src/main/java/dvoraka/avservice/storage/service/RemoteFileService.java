package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.FileMessage;

/**
 * File service with a remote connection.
 */
//TODO
public class RemoteFileService implements FileService {

    private final FileService service;


    public RemoteFileService(FileService service) {
        this.service = service;
    }

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
