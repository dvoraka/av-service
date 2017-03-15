package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.db.model.File;
import dvoraka.avservice.db.repository.db.DbFileRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * DB file service implementation.
 */
@Service
@Transactional
public class DbFileService implements FileService {

    private final DbFileRepository repository;

    private static final Logger log = LogManager.getLogger(DbFileService.class);


    @Autowired
    public DbFileService(DbFileRepository repository) {
        this.repository = requireNonNull(repository);
    }

    @Override
    public void saveFile(FileMessage message) {
        log.debug("Saving: " + message);
        repository.save(buildFile(message));
    }

    private File buildFile(FileMessage message) {
        File file = new File();
        file.setData(message.getData());
        file.setFilename(message.getFilename());
        file.setOwner(message.getOwner());

        return file;
    }

    @Override
    public FileMessage loadFile(FileMessage message) {
        log.debug("Loading: " + message);

        Optional<File> file = repository.findByFilenameAndOwner(
                message.getFilename(), message.getOwner());
        log.debug("Loaded: " + file);

        return file.map(f -> f.fileMessage(message.getId()))
                .orElse(buildFileNotFoundMessage(message.getId()));
    }

    private FileMessage buildFileNotFoundMessage(String originalId) {
        return new DefaultAvMessage.Builder(UUID.randomUUID().toString())
                .correlationId(originalId)
                .type(MessageType.FILE_NOT_FOUND)
                .build();
    }

    @Override
    public void updateFile(FileMessage message) {
        Optional<File> oldFile = repository.findByFilenameAndOwner(
                message.getFilename(), message.getOwner());
        oldFile.ifPresent(f -> f.setData(message.getData()));
        oldFile.ifPresent(repository::save);
    }

    @Override
    public void deleteFile(FileMessage message) {
        repository.removeByFilenameAndOwner(
                message.getFilename(),
                message.getOwner()
        );
    }

    @Override
    public boolean exists(String filename, String owner) {
        //TODO
        return false;
    }
}
