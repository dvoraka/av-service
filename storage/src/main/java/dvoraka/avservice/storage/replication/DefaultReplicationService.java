package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.ReplicationMessageList;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.ReplicationMessage;
import dvoraka.avservice.common.data.ReplicationStatus;
import dvoraka.avservice.storage.ExistingFileException;
import dvoraka.avservice.storage.FileNotFoundException;
import dvoraka.avservice.storage.service.FileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Default replication service implementation.
 */
public class DefaultReplicationService implements ReplicationService, ReplicationHelper {

    private final FileService fileService;
    private final ReplicationServiceClient serviceClient;
    private final ReplicationResponseClient responseClient;

    private static final Logger log = LogManager.getLogger(DefaultReplicationService.class);

    private static final int MAX_RESPONSE_TIME = 1_000; // one second

    private BlockingQueue<ReplicationMessage> commands;
    private RemoteLock remoteLock;

    private Set<String> neighbours;
    private ExecutorService executorService;


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

        neighbours = new CopyOnWriteArraySet<>();
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    private void discoverNeighbours() {
        ReplicationMessage message = createDiscoverQuery();
        serviceClient.sendMessage(message);

        ReplicationMessageList responses = responseClient
                .getResponseWait(message.getId(), MAX_RESPONSE_TIME);

        if (responses == null) {
            return;
        }

        Set<String> newNeighbours = responses.stream()
                .filter(msg -> msg.getReplicationStatus().equals(ReplicationStatus.READY))
                .map(ReplicationMessage::getFromId)
                .collect(Collectors.toSet());

        neighbours.retainAll(newNeighbours);
    }

    @Override
    public void saveFile(FileMessage message) throws ExistingFileException {
        log.debug("Save: " + message);

        if (exists(message)) {
            throw new ExistingFileException();
        }

        try {
            if (remoteLock.lockForFile(message.getFilename(), message.getOwner())) {
                if (!exists(message)) {
                    fileService.saveFile(message);

                    ReplicationMessage saveMessage = createSaveMessage(message, "neighbour");
                    serviceClient.sendMessage(saveMessage);
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
    public FileMessage loadFile(FileMessage message) throws FileNotFoundException {
        log.debug("Load: " + message);

        if (fileService.exists(message.getFilename(), message.getOwner())) {
            return fileService.loadFile(message);
        }

        if (exists(message)) {
            serviceClient.sendMessage(createLoadMessage(message, "neighbour"));

            ReplicationMessageList replicationMessages = responseClient.getResponseWait(
                    message.getId(), MAX_RESPONSE_TIME);
            if (replicationMessages != null) {
                return replicationMessages.stream()
                        .findFirst()
                        .orElseThrow(FileNotFoundException::new);
            }
        }

        throw new FileNotFoundException();
    }

    @Override
    public void updateFile(FileMessage message) throws FileNotFoundException {
        log.debug("Update: " + message);

        if (!exists(message)) {
            throw new FileNotFoundException();
        }

        if (fileService.exists(message)) {
            fileService.updateFile(message);
        }

        serviceClient.sendMessage(createUpdateMessage(message, "neighbour"));

//        ReplicationMessageList replicationMessages = responseClient.getResponseWait(
//                message.getId(), MAX_RESPONSE_TIME);
//        if (replicationMessages != null) {
//
//        }
    }

    @Override
    public void deleteFile(FileMessage message) {
        log.debug("Delete: " + message);

        if (!exists(message)) {
            return;
        }

        fileService.deleteFile(message);

        serviceClient.sendMessage(createDeleteMessage(message, "neighbour"));

//        ReplicationMessageList replicationMessages = responseClient.getResponseWait(
//                message.getId(), MAX_RESPONSE_TIME);
//        if (replicationMessages != null) {
//
//        }
    }

    @Override
    public boolean exists(String filename, String owner) {
        log.debug("Exists: {}, {}", filename, owner);

        if (fileService.exists(filename, owner)) {
            return true;
        }

        ReplicationMessage query = createExistsQuery(filename, owner);
        serviceClient.sendMessage(query);

        ReplicationMessageList response;
        response = responseClient.getResponseWait(query.getId(), MAX_RESPONSE_TIME);

        return response != null && response.stream()
                .anyMatch(message -> message.getReplicationStatus() == ReplicationStatus.OK);
    }

    @Override
    public ReplicationStatus getStatus(FileMessage message) {
        serviceClient.sendMessage(createStatusQuery(message.getFilename(), message.getOwner()));

//        ReplicationMessage response = responseClient.getResponse(message.getId());
//        ReplicationStatus status = response.getReplicationStatus();

//        return status;
        return null;
    }
}
