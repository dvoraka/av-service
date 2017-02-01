package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.db.model.File;
import dvoraka.avservice.db.repository.db.DbFileRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Service
@Transactional
public class DbFileService implements FileService {

    private static final Logger log = LogManager.getLogger(DbFileService.class);

    private final DbFileRepository repository;


    public DbFileService(DbFileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveFile(FileMessage message) {
        File file = new File();
        file.setData(message.getData());
        file.setFilename(message.getFilename());
        file.setOwner(message.getOwner());

        repository.save(file);
    }

    @Override
    public FileMessage loadFile(FileMessage message) {
        File file = repository.findByFilenameAndOwner(
                message.getFilename(),
                message.getOwner()
        );

        return file.avMessage(message.getId());
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
