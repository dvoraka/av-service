package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.ReplicationMessageList;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import dvoraka.avservice.common.ReplicationMessageListener;
import dvoraka.avservice.common.data.MessageRouting;
import dvoraka.avservice.common.data.ReplicationMessage;
import dvoraka.avservice.common.data.ReplicationStatus;
import dvoraka.avservice.common.replication.ReplicationHelper;
import dvoraka.avservice.common.service.CachingService;
import dvoraka.avservice.common.service.DefaultCachingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

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

    private static final int MAX_RESPONSE_TIME = 1_000; // one second

    private final AtomicLong sequence;
    private final Set<String> lockedFiles;

    private final CachingService cachingService;


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

        // TODO:
        cachingService = new DefaultCachingService();
    }

    @PostConstruct
    @Override
    public void start() {
        log.info("Start.");
        responseClient.addNoResponseMessageListener(this);
        //TODO: it is too early to synchronize and with every lock request is sync possible
//        new Thread(this::synchronize).start();
    }

    @Override
    public void stop() {
        log.info("Stop.");
        responseClient.removeNoResponseMessageListener(this);
    }

    @Override
    public boolean lockForFile(String filename, String owner, int lockCount)
            throws InterruptedException {

        synchronized (lockedFiles) {
            // check if the file is already locked
            if (isFileLocked(filename, owner)) {
                return false;
            }
            // lock the local file
            lockFile(filename, owner);
        }

        // send the lock request
        ReplicationMessage lockRequest = createLockRequest(filename, owner, nodeId, getSequence());
        serviceClient.sendMessage(lockRequest);

        // get replies
        Optional<ReplicationMessageList> lockReplies =
                responseClient.getResponseWait(lockRequest.getId(), MAX_RESPONSE_TIME, lockCount);

        // count success locks
        if (lockReplies.isPresent()) {
            long successLocks = lockReplies.get().stream()
                    .filter(message -> message.getReplicationStatus() == ReplicationStatus.READY)
                    .count();

            if (lockCount == successLocks) {

                return true;
            } else {
                unlockFile(filename, owner);

                return false;
            }
        } else {
            unlockFile(filename, owner);

            return false;
        }
    }

    @Override
    public boolean unlockForFile(String filename, String owner, int lockCount) {

        // send the unlock request
        ReplicationMessage unlockRequest = createUnLockRequest(filename, owner, nodeId, 0);
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
                unlockFile(filename, owner);

                return true;
            }
        }

        return false;
    }

    private void synchronize() {
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

    private void lockFile(String filename, String owner) {
        log.debug("Locking: {}, {}", filename, owner);
        lockedFiles.add(hash(filename, owner));
    }

    private void unlockFile(String filename, String owner) {
        log.debug("Unlocking: {}, {}", filename, owner);
        lockedFiles.remove(hash(filename, owner));
    }

    private String hash(String filename, String owner) {
        byte[] bytes = (filename + owner).getBytes(StandardCharsets.UTF_8);

        return cachingService.arrayDigest(bytes);
    }

    @Override
    public void onMessage(ReplicationMessage message) {
        // filter out unicasts
        if (message.getRouting() == MessageRouting.UNICAST) {
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
                    if (getSequence() == message.getSequence()) {
                        incSequence();
                        lockFile(message.getFilename(), message.getOwner());
                        serviceClient.sendMessage(createLockSuccessReply(message, nodeId));
                    } else {
                        serviceClient.sendMessage(createLockFailReply(message, nodeId));
                    }
                    break;

                case UNLOCK:
                    unlockFile(message.getFilename(), message.getOwner());
                    serviceClient.sendMessage(createUnlockSuccessReply(message, nodeId));
                    break;

                default:
                    log.debug("Unhandled broadcast command: {}", message.getCommand());
                    break;
            }
        }
    }
}
