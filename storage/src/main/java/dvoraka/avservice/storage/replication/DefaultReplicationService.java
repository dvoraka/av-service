package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.ReplicationMessage;
import dvoraka.avservice.common.data.ReplicationStatus;
import dvoraka.avservice.storage.service.FileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.Objects.requireNonNull;

/**
 * Default replication service implementation.
 */
public class DefaultReplicationService implements ReplicationService {

    private final FileService fileService;
    private final ReplicationServiceClient serviceClient;
    private final ReplicationResponseClient responseClient;

    private static final Logger log = LogManager.getLogger(DefaultReplicationService.class);


    public DefaultReplicationService(
            FileService fileService,
            ReplicationServiceClient replicationServiceClient,
            ReplicationResponseClient replicationResponseClient
    ) {
        this.fileService = requireNonNull(fileService);
        this.serviceClient = requireNonNull(replicationServiceClient);
        this.responseClient = requireNonNull(replicationResponseClient);
    }

    @Override
    public void saveFile(FileMessage message) {
        if (exists(message)) {
            // throw something
        } else {
            // save
        }
    }

    @Override
    public FileMessage loadFile(FileMessage message) {
        if (exists(message)) {
            // load locally if possible then remotely
        } else {
            // throw something
        }

        return null;
    }

    @Override
    public void updateFile(FileMessage message) {
        if (exists(message)) {
            // update
        } else {
            // throw something
        }
    }

    @Override
    public void deleteFile(FileMessage message) {
        if (exists(message)) {
            // delete
        } else {
            // throw something
        }
    }

    @Override
    public boolean exists(String filename, String owner) {
        boolean result;
        // local check
        result = fileService.exists(filename, owner);
        // remote check
        serviceClient.sendMessage(null);

        return result;
    }

    private boolean exists(FileMessage message) {
        return exists(message.getFilename(), message.getOwner());
    }

    @Override
    public ReplicationStatus getStatus(FileMessage message) {
        ReplicationMessage replicationMessage = null; // broadcast status
        serviceClient.sendMessage(replicationMessage);

        ReplicationMessage response = responseClient.getResponse(message.getId());
        ReplicationStatus status = response.getReplicationStatus();

        return status;
    }
}
