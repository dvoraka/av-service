package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.ReplicationMessageList;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import dvoraka.avservice.common.ReplicationMessageListener;
import dvoraka.avservice.common.data.Command;
import dvoraka.avservice.common.data.MessageRouting;
import dvoraka.avservice.common.data.ReplicationMessage;
import dvoraka.avservice.common.data.ReplicationStatus;
import dvoraka.avservice.common.replication.ReplicationHelper;
import dvoraka.avservice.common.service.HashingService;
import dvoraka.avservice.common.service.Md5HashingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Default remote lock implementation.
 */
@Component
public class DefaultRemoteLock implements
        RemoteLock, ReplicationMessageListener, ReplicationHelper {

    private final ReplicationServiceClient serviceClient;
    private final ReplicationResponseClient responseClient;
    private final String nodeId;

    private static final Logger log = LogManager.getLogger(DefaultRemoteLock.class);

    private static final String UNLOCKING_FAILED = "Unlocking failed.";
    private static final int MAX_RESPONSE_TIME = 1_000; // one second

    private final AtomicLong sequence;
    private final Set<String> lockedFiles;
    private final Lock lockingLock;

    private final HashingService hashingService;


    @Autowired
    public DefaultRemoteLock(
            ReplicationServiceClient serviceClient,
            ReplicationResponseClient responseClient,
            String nodeId
    ) {
        this.serviceClient = serviceClient;
        this.responseClient = responseClient;
        this.nodeId = nodeId;

        sequence = new AtomicLong();
        lockedFiles = new HashSet<>();
        lockingLock = new ReentrantLock();

        hashingService = new Md5HashingService();
    }

    @PostConstruct
    @Override
    public void start() {
        log.info("Start ({}). {}", nodeId, this);
        responseClient.addNoResponseMessageListener(this);
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        log.debug("Context refreshed event received.");
        synchronize();
    }

    @Override
    public void stop() {
        log.info("Stop.");
        responseClient.removeNoResponseMessageListener(this);
    }

    @Override
    public boolean lockForFile(String filename, String owner, int lockCount)
            throws InterruptedException {

        // lock local file if possible
        if (!lockFile(filename, owner)) {

            return false;
        }

        log.debug("Locking...");
        lockingLock.lock();
        log.debug("Locked.");

        ReplicationMessage lockRequest = createLockRequest(filename, owner, nodeId, getSequence());
        serviceClient.sendMessage(lockRequest);

        Optional<ReplicationMessageList> lockReplies =
                responseClient.getResponseWait(lockRequest.getId(), MAX_RESPONSE_TIME, lockCount);

        // count success locks
        if (lockReplies.isPresent()) {
            long successLocks = lockReplies.get().stream()
                    .filter(message -> message.getReplicationStatus() == ReplicationStatus.READY)
                    .count();

            if (lockCount == successLocks) {
                incSequence();

                log.info("Remote locking success.");
                lockingLock.unlock();
                log.debug("Unlocked.");

                return true;
            } else {
                lockingLock.unlock();
                log.debug("Unlocked.");

                try {
                    unlockFile(filename, owner);
                } catch (FileNotLockedException e) {
                    log.warn(UNLOCKING_FAILED, e);
                }

                return false;
            }
        }

        lockingLock.unlock();
        log.debug("Unlocked.");

        try {
            unlockFile(filename, owner);
        } catch (FileNotLockedException e) {
            log.warn(UNLOCKING_FAILED, e);
        }

        return false;
    }

    @Override
    public boolean unlockForFile(String filename, String owner, int lockCount) {

        // send the unlock request
        ReplicationMessage unlockRequest = createUnlockRequest(
                filename, owner, nodeId, getSequence());
        serviceClient.sendMessage(unlockRequest);

        // get replies
        Optional<ReplicationMessageList> unlockReplies =
                responseClient.getResponseWait(unlockRequest.getId(), MAX_RESPONSE_TIME, lockCount);

        // count success unlocks
        if (unlockReplies.isPresent()) {
            long successUnlocks = unlockReplies.get().stream()
                    .filter(message -> message.getReplicationStatus() == ReplicationStatus.OK)
                    .count();

            if (lockCount == successUnlocks) {
                try {
                    unlockFile(filename, owner);
                } catch (FileNotLockedException e) {
                    log.warn(UNLOCKING_FAILED, e);
                }

                return true;
            } else {
                log.warn(UNLOCKING_FAILED);
            }
        }

        return false;
    }

    /**
     * Synchronizes the lock with others.
     */
    public void synchronize() {
        initializeSequence();
    }

    private void initializeSequence() {
        log.debug("Initializing sequence...");

        ReplicationMessage request = createSequenceRequest(nodeId);
        serviceClient.sendMessage(request);

        ReplicationMessageList responses = responseClient
                .getResponseWait(request.getId(), MAX_RESPONSE_TIME)
                .orElseGet(ReplicationMessageList::new);

        long actualSequence = responses.stream()
                .peek(message -> log.debug("Sequence: {}", message))
                .findFirst()
                .map(ReplicationMessage::getSequence)
                .orElse(1L);

        setSequence(actualSequence);
    }

    private long getSequence() {
        return sequence.get();
    }

    private void setSequence(long sequence) {
        log.debug("Setting sequence: {}", sequence);
        this.sequence.set(sequence);
    }

    private void incSequence() {
        sequence.getAndIncrement();
    }

    private boolean isFileLocked(String filename, String owner) {
        return lockedFiles.contains(hash(filename, owner));
    }

    private boolean lockFile(String filename, String owner) {
        log.debug("Locking: {}, {}", filename, owner);

        synchronized (lockedFiles) {
            if (isFileLocked(filename, owner)) {

                return false;
            } else {
                lockedFiles.add(hash(filename, owner));

                return true;
            }
        }
    }

    private void unlockFile(String filename, String owner) throws FileNotLockedException {
        log.debug("Unlocking: {}, {}", filename, owner);

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

    @Override
    public void onMessage(ReplicationMessage message) {
        // filter out unicasts
        if (message.getRouting() == MessageRouting.UNICAST) {
            return;
        }

        // filter out discover messages
        if (message.getCommand() == Command.DISCOVER) {
            return;
        }

        log.debug("On message: {}", message);

        // handle broadcasts
        if (message.getRouting() == MessageRouting.BROADCAST) {
            switch (message.getCommand()) {

                case SEQUENCE:
                    serviceClient.sendMessage(createSequenceReply(message, nodeId, getSequence()));
                    break;

                case LOCK:
                    lock(message);
                    break;

                case UNLOCK:
                    try {
                        unlockFile(message.getFilename(), message.getOwner());
                        serviceClient.sendMessage(createUnlockSuccessReply(message, nodeId));
                    } catch (FileNotLockedException e) {
                        log.warn("Unlocking failed.", e);
                        serviceClient.sendMessage(createUnlockFailedReply(message, nodeId));
                    }
                    break;

                default:
                    log.debug("Unhandled broadcast command: {}", message.getCommand());
                    break;
            }
        }
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
            serviceClient.sendMessage(createLockFailedReply(message, getSequence(), nodeId));
        }
    }
}
