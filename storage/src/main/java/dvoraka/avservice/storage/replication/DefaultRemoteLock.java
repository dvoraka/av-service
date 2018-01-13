package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.ReplicationMessageList;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import dvoraka.avservice.common.data.Command;
import dvoraka.avservice.common.data.replication.ReplicationMessage;
import dvoraka.avservice.common.data.replication.ReplicationStatus;
import dvoraka.avservice.common.helper.WaitingHelper;
import dvoraka.avservice.common.helper.replication.ReplicationHelper;
import dvoraka.avservice.common.listener.ReplicationMessageListener;
import dvoraka.avservice.common.service.HashingService;
import dvoraka.avservice.common.service.Md5HashingService;
import dvoraka.avservice.storage.replication.exception.FileNotLockedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Default remote lock implementation.
 */
@Component
public class DefaultRemoteLock implements
        RemoteLock, ReplicationMessageListener, ReplicationHelper, WaitingHelper {

    private final ReplicationServiceClient serviceClient;
    private final ReplicationResponseClient responseClient;
    private final String nodeId;

    private static final Logger log = LogManager.getLogger(DefaultRemoteLock.class);

    /**
     * Not initialized value for the sequence counter.
     */
    private static final int NOT_INITIALIZED = -1;
    private static final int ISOLATED = 1;
    /**
     * Default max response time in ms.
     */
    private static final int MAX_RESPONSE_TIME = 500;
    private static final String UNLOCKING_FAILED = "Unlocking failed!";

    private final AtomicLong sequence;
    private final Set<String> lockedFiles;
    private final Lock lockingLock;
    private final HashingService hashingService;

    private int maxResponseTime;
    private volatile boolean running;

    private boolean master;
    private boolean isolated;

    private final String idString;


    @Autowired
    public DefaultRemoteLock(
            ReplicationServiceClient serviceClient,
            ReplicationResponseClient responseClient,
            String nodeId
    ) {
        this.serviceClient = requireNonNull(serviceClient);
        this.responseClient = requireNonNull(responseClient);
        this.nodeId = requireNonNull(nodeId);

        sequence = new AtomicLong(NOT_INITIALIZED);
        lockedFiles = new HashSet<>();
        lockingLock = new ReentrantLock();
        hashingService = new Md5HashingService();

        maxResponseTime = MAX_RESPONSE_TIME;

        idString = "(" + nodeId + ")";
    }

    @PostConstruct
    @Override
    public void start() {
        log.info("Start ({}). {}", nodeId, this);
        responseClient.addNoResponseMessageListener(this);
        CompletableFuture.runAsync(this::initializeSafe);
    }

    @PreDestroy
    @Override
    public void stop() {
        log.info("Stop {}.", idString);
        setRunning(false);
        responseClient.removeNoResponseMessageListener(this);
    }

    @Override
    public boolean lockForFile(String filename, String owner, int remoteLockCount)
            throws InterruptedException {

        if (isIsolated() && remoteLockCount > 0) {
            return false;
        }

        // lock local file
        if (!lockFile(filename, owner)) {
            return false;
        }

        // remote locking
        log.debug("Locking {} nodes {}...", remoteLockCount, idString);
        //TODO: unlocking looks unsafe
        lockingLock.lockInterruptibly();

        final int retryCount = 2;
        for (int i = 0; i <= retryCount; i++) {

            String id = sendLockRequest(filename, owner);
            final long successLocks = getLockResponse(id, remoteLockCount);

            if (successLocks == remoteLockCount) {
                incSequence();
                log.debug("Remote locking success {}.", idString);
                lockingLock.unlock();

                return true;
            } else if (successLocks > (remoteLockCount / 2)) {
                sendForceUnlockRequest(filename, owner);
            } else {
                break;
            }
        }

        log.warn("Remote locking failed {}.", idString);
        lockingLock.unlock();
        try {
            unlockFile(filename, owner);
        } catch (FileNotLockedException e) {
            log.warn(UNLOCKING_FAILED, e);
        }

        return false;
    }

    private String sendLockRequest(String filename, String owner) {
        ReplicationMessage lockRequest = createLockRequest(filename, owner, nodeId, getSequence());
        serviceClient.sendMessage(lockRequest);

        return lockRequest.getId();
    }

    private void sendForceUnlockRequest(String filename, String owner) {
        log.warn("Sending force unlock request {}...", idString);
        ReplicationMessage forceUnlockRequest = createForceUnlockRequest(
                filename, owner, nodeId, getSequence());
        serviceClient.sendMessage(forceUnlockRequest);
    }

    private long getLockResponse(String messageId, int lockCount) {
        return responseClient.getResponseWaitSize(messageId, maxResponseTime, lockCount)
                .orElseGet(ReplicationMessageList::new)
                .stream()
                .filter(message -> message.getReplicationStatus() == ReplicationStatus.READY)
                .count();
    }

    @Override
    public boolean unlockForFile(String filename, String owner, int lockCount) {

//        if (isIsolated() && lockCount > 1) {
//            return false;
//        }

        ReplicationMessage unlockRequest = createUnlockRequest(filename, owner, nodeId, getSequence());
        serviceClient.sendMessage(unlockRequest);

        long successUnlocks = responseClient.getResponseWaitSize(
                unlockRequest.getId(), maxResponseTime, lockCount)
                .orElseGet(ReplicationMessageList::new)
                .stream()
                .filter(message -> message.getReplicationStatus() == ReplicationStatus.OK)
                .count();

        boolean remoteSuccess = (successUnlocks == lockCount);

        try {
            unlockFile(filename, owner);
        } catch (FileNotLockedException e) {
            log.warn("Local file was not locked " + idString + "!", e);

            return false;
        }

        return remoteSuccess;
    }

    @Override
    public void networkChanged() {
//        updateSequence();
    }

    private void initializeSafe() {
        try {
            initialize();
        } catch (Exception e) {
            log.error("Initialization failed!", e);
        }
    }

    /**
     * Initializes the lock before start.
     */
    public void initialize() {
        waitUntil(responseClient::isRunning);
        initializeSequence();
    }

    /**
     * Initializes sequence before lock start.
     */
    private void initializeSequence() {
        log.info("Initializing sequence {}...", idString);

        ReplicationMessage request = createSequenceRequest(nodeId);
        serviceClient.sendMessage(request);

        long actualSequence = responseClient.getResponseWait(request.getId(), maxResponseTime)
                .orElseGet(ReplicationMessageList::new)
                .stream()
                .peek(message -> log.info("Sequence {}: {}", idString, message))
                .findFirst()
                .map(ReplicationMessage::getSequence)
                .orElse((long) NOT_INITIALIZED);

        if (actualSequence != NOT_INITIALIZED) {
            setSequence(actualSequence);
        } else {
            log.info("Start in isolated mode {}.", idString);
            setIsolatedMode();
        }

        setRunning(true);
    }

    /**
     * Updates sequence after changes on the replication network.
     */
    private void updateSequence() {
        log.info("Updating sequence {}...", idString);

        //TODO: updating should lock the lock

        if (!isRunning()) {
            log.info("Skip sequence update.");
            return;
        }

        if (!isMaster()) {
            ReplicationMessage request = createSequenceRequest(nodeId);
            serviceClient.sendMessage(request);

            List<Long> sequences = responseClient.getResponseWait(request.getId(), maxResponseTime)
                    .orElseGet(ReplicationMessageList::new)
                    .stream()
                    .peek(message -> log.info("Sequence from {} {}: {}",
                            message.getFromId(), idString, message))
                    .map(ReplicationMessage::getSequence)
                    .collect(Collectors.toList());

            if (sequences.isEmpty()) {
                setIsolatedMode();
            }

        } else {
            // negotiate the sequence with others
        }

        //TODO
    }

    private void setIsolatedMode() {
        setMaster(true);
        setIsolated(true);
        setSequence(ISOLATED);
    }

    private long getSequence() {
        return sequence.get();
    }

    private void setSequence(long sequence) {
        log.debug("Setting sequence {}: {}", idString, sequence);
        this.sequence.set(sequence);
    }

    private void incSequence() {
        sequence.getAndIncrement();
    }

    private boolean isFileLocked(String filename, String owner) {
        return lockedFiles.contains(hash(filename, owner));
    }

    private boolean lockFile(String filename, String owner) {
        log.debug("Locking {}: {}, {}", idString, filename, owner);

        synchronized (lockedFiles) {
            if (isFileLocked(filename, owner)) {
                log.debug("File is already locked {}: {}, {}",
                        idString, filename, owner);

                return false;
            } else {
                log.debug("Lock success {}: {}, {}", idString, filename, owner);
                lockedFiles.add(hash(filename, owner));

                return true;
            }
        }
    }

    private void unlockFile(String filename, String owner) throws FileNotLockedException {
        log.debug("Unlocking {}: {}, {}", idString, filename, owner);

        synchronized (lockedFiles) {
            if (!lockedFiles.remove(hash(filename, owner))) {
                throw new FileNotLockedException();
            }
        }
    }

    private String hash(String filename, String owner) {
        byte[] bytes = (filename + owner).getBytes(StandardCharsets.UTF_8);

        return hashingService.arrayHash(bytes);
    }

    public int getMaxResponseTime() {
        return maxResponseTime;
    }

    public void setMaxResponseTime(int maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
    }

    @Override
    public void onMessage(ReplicationMessage message) {
        try {
            handleOnMessage(message);
        } catch (Exception e) {
            log.warn("On message problem!", e);
        }
    }

    private void handleOnMessage(ReplicationMessage message) {
        if (!isRunning()) {
            return;
        }

        // input filtering
        if (isUnicast(message) || isCommand(message, Command.DISCOVER, Command.EXISTS)) {
            return;
        }

        log.debug("On message {}: {}", idString, message);

        switch (message.getCommand()) {
            case SEQUENCE:
                sequence(message);
                break;

            case LOCK:
                lock(message);
                break;

            case UNLOCK:
                unlock(message);
                break;

            case FORCE_UNLOCK:
                forceUnlock(message);
                break;

            default:
                log.debug("Unhandled broadcast command: {}", message.getCommand());
                break;
        }
    }

    private void sequence(ReplicationMessage message) {
        serviceClient.sendMessage(createSequenceReply(message, nodeId, getSequence()));
    }

    private void lock(ReplicationMessage message) {
        if (getSequence() == message.getSequence() && lockingLock.tryLock()) {

            if (lockFile(message.getFilename(), message.getOwner())) {
                incSequence();
                lockingLock.unlock();
                serviceClient.sendMessage(createLockSuccessReply(message, nodeId));
            } else {
                lockingLock.unlock();
                serviceClient.sendMessage(createLockFailedReply(message, getSequence(), nodeId));
            }
        } else {
            log.debug("Bad sequence {}: {}", idString, message.getSequence());
            serviceClient.sendMessage(createLockFailedReply(message, getSequence(), nodeId));
        }
    }

    private void unlock(ReplicationMessage message) {
        try {
            unlockFile(message.getFilename(), message.getOwner());
            serviceClient.sendMessage(createUnlockSuccessReply(message, nodeId));
        } catch (FileNotLockedException e) {
            log.warn("Unlocking failed " + idString + ".", e);
            serviceClient.sendMessage(createUnlockFailedReply(message, nodeId));
        }
    }

    private void forceUnlock(ReplicationMessage message) {
        if (isFileLocked(message.getFilename(), message.getOwner())) {
            log.warn("Force unlock {}: {}, {}",
                    idString, message.getFilename(), message.getOwner());
            try {
                unlockFile(message.getFilename(), message.getOwner());
            } catch (FileNotLockedException e) {
                log.warn("Force unlock failed " + idString + ".", e);
            }
        }
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private boolean isMaster() {
        return master;
    }

    private void setMaster(boolean master) {
        this.master = master;
    }

    private boolean isIsolated() {
        return isolated;
    }

    private void setIsolated(boolean isolated) {
        this.isolated = isolated;
    }
}
