package dvoraka.avservice.storage.replication;

import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import dvoraka.avservice.common.ReplicationMessageListener;
import dvoraka.avservice.common.data.ReplicationMessage;
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
    public void start() {
        responseClient.addNoResponseMessageListener(this);
        initializeSequence();
    }

    @Override
    public boolean lockForFile(String filename, String owner) throws InterruptedException {

        // send lock sequence query
        //
        // get actual lock sequence or create new one
        initializeSequence();

        //
        // send lock query
        //
        // get lock query responses
        //
        serviceClient.sendMessage(createLockRequest(filename, owner, nodeId));

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

    private void initializeSequence() {
        serviceClient.sendMessage(createSequenceRequest(nodeId));

        // read response

        // set sequence
    }

    public long getSequence() {
        return sequence.get();
    }

    private void setSequence(long sequence) {
        this.sequence.set(sequence);
    }

    private void incSequence() {
        sequence.getAndIncrement();
    }

    @Override
    public void onMessage(ReplicationMessage message) {

    }
}
