package dvoraka.avservice.storage.service;

import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.db.model.File;
import dvoraka.avservice.db.repository.db.DbFileRepository;
import dvoraka.avservice.storage.ExistingFileException;
import dvoraka.avservice.storage.FileNotFoundException;
import dvoraka.avservice.storage.FileServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
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
    public void saveFile(FileMessage message) throws FileServiceException {
        log.debug("Saving: " + message);

        try {
            repository.save(buildFile(message));
        } catch (ConstraintViolationException e) {
            log.warn("Saving problem.", e);
            throw new ExistingFileException();
        }
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
    public void updateFile(FileMessage message) throws FileNotFoundException {
        log.debug("Updating: " + message);

        Optional<File> oldFile = repository.findByFilenameAndOwner(
                message.getFilename(), message.getOwner());

        if (oldFile.isPresent()) {
            oldFile.get().setData(message.getData());
            repository.save(oldFile.get());
        } else {
            throw new FileNotFoundException();
        }
    }

    @Override
    public void deleteFile(FileMessage message) {
        repository.removeByFilenameAndOwner(
                message.getFilename(),
                message.getOwner()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String filename, String owner) {
        return repository.findByFilenameAndOwner(filename, owner)
                .isPresent();
    }
}
