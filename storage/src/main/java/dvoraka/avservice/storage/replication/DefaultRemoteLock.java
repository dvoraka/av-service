package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.ReplicationMessageList;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import dvoraka.avservice.common.ReplicationMessageListener;
import dvoraka.avservice.common.data.Command;
import dvoraka.avservice.common.data.MessageRouting;
import dvoraka.avservice.common.data.ReplicationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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


    private AtomicLong sequence;


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
    public boolean lockForFile(String filename, String owner) throws InterruptedException {

        //
        // send lock query
        //
        // get lock query responses
        //
        serviceClient.sendMessage(createLockRequest(filename, owner, nodeId, getSequence()));

        // return locking status
        return true;
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
            serviceClient.sendMessage(createLockFailReply(message, nodeId));
        }
    }
}
