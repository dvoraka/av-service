package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.ReplicationMessage;
import dvoraka.avservice.common.data.ReplicationStatus;
import dvoraka.avservice.storage.ExistingFileException;
import dvoraka.avservice.storage.service.FileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.Objects.requireNonNull;

/**
 * Default replication service implementation.
 */
public class DefaultReplicationService implements ReplicationService {

    private final FileService fileService;
    private final ReplicationServiceClient serviceClient;
    private final ReplicationResponseClient responseClient;

    private static final Logger log = LogManager.getLogger(DefaultReplicationService.class);

    private BlockingQueue<ReplicationMessage> commands;
    private RemoteLock remoteLock;
    private int neighbourCount;


    public DefaultReplicationService(
            FileService fileService,
            ReplicationServiceClient replicationServiceClient,
            ReplicationResponseClient replicationResponseClient
    ) {
        this.fileService = requireNonNull(fileService);
        this.serviceClient = requireNonNull(replicationServiceClient);
        this.responseClient = requireNonNull(replicationResponseClient);

        final int size = 10;
        commands = new ArrayBlockingQueue<>(size);
        remoteLock = new DefaultRemoteLock();
    }

    @Override
    public void saveFile(FileMessage message) throws ExistingFileException {
        if (exists(message)) {
            throw new ExistingFileException();
        }

        try {
            if (remoteLock.lockForFile(message.getFilename(), message.getOwner())) {
                if (!exists(message)) {
                    // save locally
                    // save remotely
                } else {
                    throw new ExistingFileException();
                }
            } else {
                log.warn("Save problem for: " + message);
            }
        } catch (InterruptedException e) {
            log.warn("Locking interrupted!", e);
            Thread.currentThread().interrupt();
        } finally {
            remoteLock.unlockForFile(message.getFilename(), message.getOwner());
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

    @Override
    public ReplicationStatus getStatus(FileMessage message) {
        ReplicationMessage replicationMessage = null; // broadcast status
        serviceClient.sendMessage(replicationMessage);

        ReplicationMessage response = responseClient.getResponse(message.getId());
        ReplicationStatus status = response.getReplicationStatus();

        return status;
    }
}
