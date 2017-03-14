package dvoraka.avservice.client.service;

import dvoraka.avservice.client.ReplicationComponent;
import dvoraka.avservice.common.data.ReplicationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation for the remote replication service.
 */
@Service
public class DefaultReplicationServiceClient implements ReplicationServiceClient {

    private final ReplicationComponent replicationComponent;

    private static final Logger log = LogManager.getLogger(DefaultReplicationServiceClient.class);


    @Autowired
    public DefaultReplicationServiceClient(ReplicationComponent replicationComponent) {
        this.replicationComponent = requireNonNull(replicationComponent);
    }

    @Override
    public void sendMessage(ReplicationMessage message) {
        replicationComponent.sendMessage(message);
    }
}
