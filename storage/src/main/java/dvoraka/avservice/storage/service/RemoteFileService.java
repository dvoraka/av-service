package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.FileMessage;

/**
 * File service with a remote connection.
 */
public class RemoteFileService implements FileService {

    private final FileService service;


    public RemoteFileService(FileService service) {
        this.service = service;
    }

    @Override
    public void saveFile(FileMessage message) {
        //TODO
    }

    @Override
    public FileMessage loadFile(FileMessage message) {
        //TODO
        return null;
    }

    @Override
    public void updateFile(FileMessage message) {
        //TODO
    }

    @Override
    public void deleteFile(FileMessage message) {
        //TODO
    }
}
