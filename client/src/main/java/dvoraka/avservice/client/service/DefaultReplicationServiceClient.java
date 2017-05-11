package dvoraka.avservice.client.service;

import dvoraka.avservice.client.ReplicationComponent;
import dvoraka.avservice.common.data.ReplicationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

/**
 * Default client implementation for the remote replication service.
 */
@Service
public class DefaultReplicationServiceClient implements ReplicationServiceClient {

    private final ReplicationComponent replicationComponent;
    private final String nodeId;

    private static final Logger log = LogManager.getLogger(DefaultReplicationServiceClient.class);

    private static final String WRONG_ID = "Wrong from node ID in the message!";


    @Autowired
    public DefaultReplicationServiceClient(
            ReplicationComponent replicationComponent,
            String nodeId
    ) {
        this.replicationComponent = requireNonNull(replicationComponent);
        this.nodeId = requireNonNull(nodeId);
    }

    @Override
    public void sendMessage(ReplicationMessage message) {
        log.debug("Send: " + message);

        if (!nodeId.equals(message.getFromId())) {
            log.warn(WRONG_ID);
            throw new IllegalArgumentException(WRONG_ID);
        }

        replicationComponent.sendMessage(message);
    }
}
