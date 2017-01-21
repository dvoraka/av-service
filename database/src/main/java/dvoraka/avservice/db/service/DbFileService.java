package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.db.model.File;
import dvoraka.avservice.db.repository.DbFileRepository;
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
}
