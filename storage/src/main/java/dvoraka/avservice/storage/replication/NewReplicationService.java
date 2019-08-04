package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.transport.ReplicationComponent;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.replication.ReplicationMessage;
import dvoraka.avservice.common.data.replication.ReplicationStatus;
import dvoraka.avservice.common.helper.replication.ReplicationHelper;
import dvoraka.avservice.common.listener.ReplicationMessageListener;
import dvoraka.avservice.common.service.TimedStorage;
import dvoraka.avservice.storage.exception.FileAlreadyExistsException;
import dvoraka.avservice.storage.exception.FileServiceException;
import dvoraka.avservice.storage.service.FileService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * New replication service.
 */
@Slf4j
public class NewReplicationService implements ReplicationService, ReplicationHelper {

    private final FileService fileService;
    private final ReplicationComponent component;

    private static final int DISCOVERY_DELAY = 20_000;

    private final String nodeId;
    private final Set<String> neighbours;
    private final ScheduledExecutorService executorService;
    private final String idString;

    private final TimedStorage<String> openFiles;

    private volatile boolean running;
    private final ReplicationMessageListener messageListener;


    public NewReplicationService(FileService fileService, ReplicationComponent component) {
        this.fileService = requireNonNull(fileService);
        this.component = requireNonNull(component);

        openFiles = new TimedStorage<>();
        messageListener = this;

        nodeId = this.component.getServiceId();
        neighbours = new CopyOnWriteArraySet<>();
        executorService = Executors.newSingleThreadScheduledExecutor();
        idString = "(" + nodeId + ")";
    }

    @PostConstruct
    @Override
    public void start() {
        component.addMessageListener(messageListener);
        running = true;

        scheduleDiscovery();
    }

    @PreDestroy
    @Override
    public void stop() {
        component.removeMessageListener(messageListener);
        running = false;
    }

    private void scheduleDiscovery() {
        executorService.scheduleWithFixedDelay(
                this::discoverNeighboursSafe, 0, DISCOVERY_DELAY, TimeUnit.MILLISECONDS);
    }

    private void discoverNeighboursSafe() {
        try {
            discoverNeighbours();
        } catch (Exception e) {
            log.warn("Discovery failed!", e);
        }
    }

    private void discoverNeighbours() {
        log.debug("Discovering neighbours {}...", idString);

        // old code
//        ReplicationMessage message = createDiscoverRequest(nodeId);
//        serviceClient.sendMessage(message);
//
//        Set<String> newNeighbours = responseClient
//                .getResponseWait(message.getId(), maxResponseTime, maxResponseTime)
//                .orElseGet(ReplicationMessageList::new)
//                .stream()
//                .filter(msg -> msg.getReplicationStatus() == ReplicationStatus.READY)
//                .map(ReplicationMessage::getFromId)
//                .collect(Collectors.toSet());
//
//        if (newNeighbours.isEmpty()) {
//            log.debug("Neighbours discovered {}: none", idString);
//        } else {
//
//            if (!neighbours.equals(newNeighbours)) {
//                log.info("Neighbours changed {}: {} to {}",
//                        idString, neighbours.size(), newNeighbours.size());
//
//                remoteLock.networkChanged();
//            }
//
//            // TODO: improve synchronization
//            neighbours.clear();
//            neighbours.addAll(newNeighbours);
//            log.debug("Neighbours discovered {}: {}", idString, neighbourCount());
//        }
//
//        //TODO: if it's stopped it sets running flag again
//        if (!isRunning()) {
//            setRunning(true);
//        }
    }

    @Override
    public ReplicationStatus getStatus(FileMessage message) {
        return null;
    }

    @Override
    public void setReplicationCount(int count) {

    }

    @Override
    public int getMaxResponseTime() {
        return 0;
    }

    @Override
    public void setMaxResponseTime(int maxTime) {

    }

    @Override
    public void onMessage(ReplicationMessage message) {

        // filter out our messages (here for now)
        if (message.getFromId().equals(nodeId)) {
            return;
        }

        log.debug("On message ({}): {}", nodeId, message);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    public String getNodeId() {
        return nodeId;
    }

    @Override
    public void saveFile(FileMessage message) throws FileServiceException {
        // sync version by design

        // check local copy
        if (localCopyExists(message)) {
            throw new FileAlreadyExistsException();
        }

        // send file to remote nodes

        // save local copy and prepare undo

        // check remote responses
        // - send remote rollback
        // - local undo
        // - exception

        // send remote commit

        // check remote responses
        // - send remote rollback
        // - local undo
        // - exception

        // commit local copy
    }

    @Override
    public FileMessage loadFile(FileMessage message) throws FileServiceException {
        return null;
    }

    @Override
    public void updateFile(FileMessage message) throws FileServiceException {

    }

    @Override
    public void deleteFile(FileMessage message) throws FileServiceException {

    }

    @Override
    public boolean exists(String filename, String owner) {
        return false;
    }

    private boolean localCopyExists(FileMessage message) {
        return localCopyExists(message.getFilename(), message.getOwner());
    }

    private boolean localCopyExists(String filename, String owner) {
        return fileService.exists(filename, owner);
    }

    private Future<Set<ReplicationMessage>> waitForResponses(String corrId, int count, int maxTime,
                                                             Consumer<Set<ReplicationMessage>> callback) {

        return CompletableFuture.supplyAsync(() -> {

            // check responses until max time

            // call callback if entered

            return null;
        });
    }
}
