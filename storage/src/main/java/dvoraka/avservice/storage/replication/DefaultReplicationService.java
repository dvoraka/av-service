package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.service.ResponseClient;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.storage.service.FileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.Objects.requireNonNull;

/**
 * Default replication service implementation.
 */
public class DefaultReplicationService implements ReplicationService {

    private final FileService fileService;
    private final ResponseClient responseClient;

    private static final Logger log = LogManager.getLogger(DefaultReplicationService.class);


    public DefaultReplicationService(FileService fileService, ResponseClient responseClient) {
        this.fileService = requireNonNull(fileService);
        this.responseClient = requireNonNull(responseClient);
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
