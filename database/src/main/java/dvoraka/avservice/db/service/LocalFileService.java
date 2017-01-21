package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.FileMessage;

/**
 * File service with direct connection.
 */
public class LocalFileService implements FileService {

    private final FileService fileService;


    public LocalFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public void saveFile(FileMessage message) {
        fileService.saveFile(message);
    }
}
