package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.ReplicationMessageList;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import dvoraka.avservice.common.data.Command;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageRouting;
import dvoraka.avservice.common.data.ReplicationMessage;
import dvoraka.avservice.common.data.ReplicationStatus;
import dvoraka.avservice.common.helper.FileServiceHelper;
import dvoraka.avservice.common.replication.ReplicationHelper;
import dvoraka.avservice.storage.exception.ExistingFileException;
import dvoraka.avservice.storage.exception.FileNotFoundException;
import dvoraka.avservice.storage.exception.FileServiceException;
import dvoraka.avservice.storage.replication.exception.CannotAcquireLockException;
import dvoraka.avservice.storage.replication.exception.LockCountNotMatchException;
import dvoraka.avservice.storage.service.FileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Default replication service implementation.
 */
@Service
public class DefaultReplicationService implements
        ReplicationService, ReplicationHelper, FileServiceHelper {

    private final FileService fileService;
    private final ReplicationServiceClient serviceClient;
    private final ReplicationResponseClient responseClient;
    private final RemoteLock remoteLock;
    private final String nodeId;

    private static final Logger log = LogManager.getLogger(DefaultReplicationService.class);

    /**
     * Max waiting time for a response from the network in ms.
     */
    private static final int MAX_RESPONSE_TIME = 400;
    private static final int DISCOVER_DELAY = 20_000;
    private static final int TERM_TIME = 10;
    private static final int REPLICATION_COUNT = 3;

    private Set<String> neighbours;
    private int replicationCount;
    private ScheduledExecutorService executorService;

    private final String idString;


    @Autowired
    public DefaultReplicationService(
            FileService fileService,
            ReplicationServiceClient replicationServiceClient,
            ReplicationResponseClient replicationResponseClient,
            RemoteLock remoteLock,
            String nodeId
    ) {
        this.fileService = requireNonNull(fileService);
        this.serviceClient = requireNonNull(replicationServiceClient);
        this.responseClient = requireNonNull(replicationResponseClient);
        this.remoteLock = requireNonNull(remoteLock);
        this.nodeId = requireNonNull(nodeId);

        neighbours = new CopyOnWriteArraySet<>();
        replicationCount = REPLICATION_COUNT;
        executorService = Executors.newSingleThreadScheduledExecutor();

        idString = "(" + nodeId + ")";
    }

    @PostConstruct
    public void start() {
        log.info("Starting service ({})...", nodeId);
        responseClient.addNoResponseMessageListener(this);
        final int delayTime = 300;
        executorService.scheduleWithFixedDelay(
                this::discoverNeighbours, delayTime, DISCOVER_DELAY, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void stop() {
        log.info("Stopping service {}...", idString);
        responseClient.removeNoResponseMessageListener(this);
        remoteLock.stop();
        shutdownAndAwaitTermination(executorService, TERM_TIME, log);
    }

    private void discoverNeighbours() {
        log.debug("Discovering neighbours ({})...", nodeId);

        ReplicationMessage message = createDiscoverRequest(nodeId);
        serviceClient.sendMessage(message);

        Optional<ReplicationMessageList> responses = responseClient
                .getResponseWait(message.getId(), MAX_RESPONSE_TIME, MAX_RESPONSE_TIME);

        if (!responses.isPresent()) {
            log.debug("Discovered ({}): none", nodeId);

            return;
        }

        Set<String> newNeighbours = responses.orElseGet(ReplicationMessageList::new)
                .stream()
                .filter(msg -> msg.getReplicationStatus() == ReplicationStatus.READY)
                .map(ReplicationMessage::getFromId)
                .collect(Collectors.toSet());

        neighbours.clear();
        neighbours.addAll(newNeighbours);
        log.debug("Discovered ({}): {}", nodeId, neighbourCount());
    }

    public int neighbourCount() {
        return neighbours.size();
    }

    @Override
    public void saveFile(FileMessage message) throws FileServiceException {
        log.debug("Save {}: {}", idString, message);

        // depends on an efficiency of the sending algorithm and
        // it still must be different for "bigger" (~10 MB+) files
        final int sizeTimeRatio = 2_000;
        final int maxSaveTime = (message.getData().length / sizeTimeRatio) + MAX_RESPONSE_TIME;
        log.debug("Setting max save time to {} {}", maxSaveTime, idString);

        if (localCopyExists(message)) {
            throw new ExistingFileException();
        }

        int neighbours = neighbourCount();
        if (lockFile(message, neighbours)) {
            try {
                if (exists(message)) {
                    throw new ExistingFileException();
                } else {
                    log.debug("Saving locally {}...", idString);
                    fileService.saveFile(message);

                    log.debug("Saving remotely {}...", idString);
                    sendSaveMessage(message);

                    long successCount = getSaveResponse(
                            message.getId(),
                            maxSaveTime,
                            remoteReplicationCount());
                    if (successCount == remoteReplicationCount()) {
                        log.debug("Save success {}.", idString);
                    } else {
                        log.debug("Expected {}, got {} {}",
                                remoteReplicationCount(),
                                successCount,
                                idString);

                        log.debug("Rolling back save {}...", idString);
                        FileMessage deleteMessage = fileDeleteMessage(message);
                        fileService.deleteFile(fileDeleteMessage(deleteMessage));
                        sendDeleteMessage(deleteMessage);

                        throw new LockCountNotMatchException();
                    }
                }
            } finally {
                unlockFile(message, neighbours);
            }
        } else {
            log.warn("Save lock problem for {}: {}", idString, message);
            throw new CannotAcquireLockException();
        }
    }

    private void sendSaveMessage(FileMessage message) {
        neighbours.stream()
                .map(neighbourId -> createSaveMessage(message, nodeId, neighbourId))
                .limit(getReplicationCount() - 1L)
                .peek(msg -> log.debug(
                        "Sending save message to {} {}...", msg.getToId(), idString))
                .forEach(serviceClient::sendMessage);
    }

    private long getSaveResponse(String messageId, int maxTime, int replicationCount) {
        ReplicationMessageList messages = responseClient.getResponseWaitSize(
                messageId,
                maxTime,
                replicationCount
        ).orElseGet(ReplicationMessageList::new);

        return messages.stream()
                .filter(msg -> msg.getCommand() == Command.SAVE)
                .filter(msg -> msg.getReplicationStatus() == ReplicationStatus.OK)
                .count();
    }

    @Override
    public FileMessage loadFile(FileMessage message) throws FileServiceException {
        log.debug("Load ({}): {}", nodeId, message);

        int neighbours = neighbourCount();
        if (lockFile(message, neighbours)) {
            try {
                if (localCopyExists(message)) {
                    log.debug("Loading locally...");

                    return fileService.loadFile(message);
                }

                if (exists(message)) {
                    log.debug("Loading remotely {}...", idString);
                    sendLoadMessage(message);

                    return getLoadResponse(message.getId()).stream()
                            .filter(msg -> msg.getReplicationStatus() == ReplicationStatus.OK)
                            .peek(m -> log.debug("Load success {}.", idString))
                            .findFirst()
                            .orElseThrow(FileNotFoundException::new);
                } else {
                    log.debug("Loading failed {}.", idString);
                    throw new FileNotFoundException();
                }
            } finally {
                unlockFile(message, neighbours);
            }
        } else {
            log.warn("Load lock problem for {}: {}", idString, message);
            throw new CannotAcquireLockException();
        }
    }

    private void sendLoadMessage(FileMessage message) throws FileNotFoundException {
        String neighbourId = whoHas(message.getFilename(), message.getOwner())
                .stream()
                .findAny()
                .orElseThrow(FileNotFoundException::new);

        log.debug("Loading from {} {}...", neighbourId, idString);
        serviceClient.sendMessage(createLoadMessage(message, nodeId, neighbourId));
    }

    private ReplicationMessageList getLoadResponse(String messageId) {
        return responseClient
                .getResponseWait(messageId, MAX_RESPONSE_TIME)
                .orElseGet(ReplicationMessageList::new);
    }

    @Override
    public void updateFile(FileMessage message) throws FileServiceException {
        log.debug("Update {}: {}", idString, message);

        deleteFile(fileDeleteMessage(message));

        saveFile(fileSaveMessage(message));
    }

    @Override
    public void deleteFile(FileMessage message) throws FileServiceException {
        log.debug("Delete {}: {}", idString, message);

        int neighbours = neighbourCount();
        if (lockFile(message, neighbours)) {
            try {
                if (localCopyExists(message)) {
                    log.debug("Deleting local copy {}...", idString);
                    fileService.deleteFile(message);
                }

                Set<String> nodes = sendDeleteMessage(message);

                if (nodes.isEmpty()) {
                    throw new FileNotFoundException();
                }

                long successCount = getDeleteResponse(message.getId(), nodes.size());
                if (successCount == nodes.size()) {
                    log.debug("Delete success {}.", idString);
                } else {
                    log.debug("Delete failed {}.", idString);
                }
            } finally {
                unlockFile(message, neighbours);
            }
        } else {
            log.warn("Delete lock problem for {}: {}", idString, message);
            throw new CannotAcquireLockException();
        }
    }

    private Set<String> sendDeleteMessage(FileMessage message) {
        Set<String> nodes = whoHas(message.getFilename(), message.getOwner());
        nodes.stream()
                .map(id -> createDeleteMessage(message, nodeId, id))
                .peek(msg -> log.debug(
                        "Sending delete message to {} {}...", msg.getToId(), idString))
                .forEach(serviceClient::sendMessage);

        return nodes;
    }

    private long getDeleteResponse(String messageId, int responseCount) {
        return responseClient.getResponseWaitSize(messageId, MAX_RESPONSE_TIME, responseCount)
                .orElseGet(ReplicationMessageList::new)
                .stream()
                .filter(msg -> msg.getReplicationStatus() == ReplicationStatus.OK)
                .count();
    }

    private boolean lockFile(FileMessage message, int lockCount) {
        try {
            return remoteLock.lockForFile(message.getFilename(), message.getOwner(), lockCount);
        } catch (InterruptedException e) {
            log.warn("Locking interrupted!", e);
            remoteLock.unlockForFile(message.getFilename(), message.getOwner(), 0);
            Thread.currentThread().interrupt();

            return false;
        }
    }

    private boolean unlockFile(FileMessage message, int lockCount) {
        return remoteLock.unlockForFile(message.getFilename(), message.getOwner(), lockCount);
    }

    @Override
    public boolean exists(String filename, String owner) {
        log.debug("Exists {}: {}, {}", idString, filename, owner);

        return localCopyExists(filename, owner) || !whoHas(filename, owner).isEmpty();
    }

    /**
     * Returns a set of neighbour IDs with a given file.
     *
     * @param filename the filename
     * @param owner    the owner
     * @return the set of IDs
     */
    public Set<String> whoHas(String filename, String owner) {
        ReplicationMessage request = createExistsRequest(filename, owner, nodeId);
        serviceClient.sendMessage(request);

        return responseClient.getResponseWaitSize(
                request.getId(), MAX_RESPONSE_TIME, neighbourCount())
                .orElseGet(ReplicationMessageList::new)
                .stream()
                .filter(message -> message.getReplicationStatus() == ReplicationStatus.OK)
                .map(ReplicationMessage::getFromId)
                .collect(Collectors.toSet());
    }

    @Override
    public ReplicationStatus getStatus(FileMessage message) {
        log.debug("Status: " + message);

        ReplicationMessage request = createStatusRequest(
                message.getFilename(), message.getOwner(), nodeId);
        serviceClient.sendMessage(request);

        long resultCount = responseClient.getResponseWaitSize(
                message.getId(), MAX_RESPONSE_TIME, neighbourCount())
                .orElseGet(ReplicationMessageList::new)
                .stream()
                .filter(msg -> msg.getReplicationStatus() == ReplicationStatus.OK)
                .count();

        if (resultCount >= remoteReplicationCount()) {
            return ReplicationStatus.OK;
        } else {
            return ReplicationStatus.FAILED;
        }
    }

    public int getReplicationCount() {
        return replicationCount;
    }

    private int remoteReplicationCount() {
        return replicationCount - 1;
    }

    public void setReplicationCount(int replicationCount) {
        this.replicationCount = replicationCount;
    }

    private boolean localCopyExists(FileMessage message) {
        return localCopyExists(message.getFilename(), message.getOwner());
    }

    private boolean localCopyExists(String filename, String owner) {
        return fileService.exists(filename, owner);
    }

    @Override
    public void onMessage(ReplicationMessage message) {
        try {
            handleOnMessage(message);
        } catch (Exception e) {
            log.error("On message " + idString + " failed!", e);
        }
    }

    private void handleOnMessage(ReplicationMessage message) {
        // broadcast and unicast messages from the replication network
        log.debug("On message {}: {}", idString, message);

        if (message.getRouting() == MessageRouting.BROADCAST) {

            switch (message.getCommand()) {
                case DISCOVER:
                    handleDiscover(message);
                    break;

                case EXISTS:
                    handleExists(message);
                    break;

                default:
            }
        } else {

            switch (message.getCommand()) {
                case SAVE:
                    handleSave(message);
                    break;

                case LOAD:
                    handleLoad(message);
                    break;

                case DELETE:
                    handleDelete(message);
                    break;

                default:
            }
        }
    }

    private void handleDiscover(ReplicationMessage message) {
        serviceClient.sendMessage(createDiscoverReply(message, nodeId));
    }

    private void handleExists(ReplicationMessage message) {
        if (localCopyExists(message)) {
            serviceClient.sendMessage(createExistsReply(message, nodeId));
        } else {
            serviceClient.sendMessage(createNonExistsReply(message, nodeId));
        }
    }

    private void handleSave(ReplicationMessage message) {
        try {
            fileService.saveFile(message.fileMessage());
            serviceClient.sendMessage(createSaveSuccess(message, nodeId));
        } catch (FileServiceException e) {
            log.warn("Saving failed " + idString, e);
            serviceClient.sendMessage(createSaveFailed(message, nodeId));
        }
    }

    private void handleLoad(ReplicationMessage message) {
        try {
            FileMessage fileMessage = fileService.loadFile(message.fileMessage());
            serviceClient.sendMessage(
                    createLoadSuccess(fileMessage, message, nodeId));
        } catch (FileServiceException e) {
            log.warn("Loading failed " + idString, e);
            serviceClient.sendMessage(
                    createLoadFailed(message, nodeId, message.getFromId()));
        }
    }

    private void handleDelete(ReplicationMessage message) {
        try {
            fileService.deleteFile(message.fileMessage());
            serviceClient.sendMessage(
                    createDeleteSuccess(message, nodeId, message.getFromId()));
        } catch (FileServiceException e) {
            log.warn("Deleting failed " + idString, e);
//                      serviceClient.sendMessage();
        }
    }
}
