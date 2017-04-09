package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.ReplicationMessageList;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import dvoraka.avservice.common.ReplicationMessageListener;
import dvoraka.avservice.common.data.Command;
import dvoraka.avservice.common.data.MessageRouting;
import dvoraka.avservice.common.data.ReplicationMessage;
import dvoraka.avservice.common.data.ReplicationStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    }

    @PostConstruct
    @Override
    public void start() {
        log.info("Start.");
        responseClient.addNoResponseMessageListener(this);
        new Thread(this::synchronize).start();
    }

    @Override
    public void stop() {
        log.info("Stop.");
        responseClient.removeNoResponseMessageListener(this);
    }

    @Override
    public boolean lockForFile(String filename, String owner, int lockCount)
            throws InterruptedException {

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

            return lockCount == successLocks;
        } else {

            return false;
        }
    }

    @Override
    public boolean unlockForFile(String filename, String owner) {

        // send unlock query
        //
        // get unlock query responses

        return true;
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

    public long getSequence() {
        return sequence.get();
    }

    private void setSequence(long sequence) {
        log.debug("Setting sequence: {}", sequence);
        this.sequence.set(sequence);
    }

    private void incSequence() {
        sequence.getAndIncrement();
    }

    private void lockFile(String filename, String owner) {
        log.debug("Locking: {}, {}", filename, owner);
        //TODO: create hash
//        lockedFiles.add(null);
    }

    private void unlockFile(String filename, String owner) {
        log.debug("Unlocking: {}, {}", filename, owner);
        // create a hash and remove from the map
//        lockedFiles.remove(null);
    }

    @Override
    public void onMessage(ReplicationMessage message) {
        log.debug("On message: {}", message);

        // handle discover
        if (message.getRouting() == MessageRouting.BROADCAST
                && message.getCommand() == Command.SEQUENCE) {
            serviceClient.sendMessage(createSequenceReply(message, nodeId, getSequence()));
        }

        // handle lock request
        if (message.getRouting() == MessageRouting.BROADCAST
                && message.getCommand() == Command.LOCK) {
            if (getSequence() == message.getSequence()) {
                incSequence();
                lockFile(message.getFilename(), message.getOwner());

                serviceClient.sendMessage(createLockSuccessReply(message, nodeId));
            } else {
                serviceClient.sendMessage(createLockFailReply(message, nodeId));
            }
        }

        // handle unlock request
        if (message.getRouting() == MessageRouting.BROADCAST
                && message.getCommand() == Command.UNLOCK) {

            unlockFile(message.getFilename(), message.getOwner());

            serviceClient.sendMessage(createUnlockSuccessReply(message, nodeId));
        }
    }
}
