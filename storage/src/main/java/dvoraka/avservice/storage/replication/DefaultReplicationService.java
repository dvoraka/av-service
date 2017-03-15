package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.service.ReplicationResponseClient;
import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageType;
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
        // check if file exists locally
        FileMessage fileMessage = loadFile(message);
        if (fileMessage.getType().equals(MessageType.FILE_NOT_FOUND)) {
            // check if file exists anywhere
            ReplicationMessage replicationMessage = null; // we need broadcast replication query
            serviceClient.sendMessage(replicationMessage);
        }

    }

    @Override
    public FileMessage loadFile(FileMessage message) {
        // we load locally first and then remotely
        return null;
    }

    @Override
    public void updateFile(FileMessage message) {

    }

    @Override
    public void deleteFile(FileMessage message) {

    }

    @Override
    public boolean exists(String filename, String owner) {
        //TODO
        return false;
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
