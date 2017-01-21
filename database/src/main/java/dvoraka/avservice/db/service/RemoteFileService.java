package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.FileMessage;

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
}
