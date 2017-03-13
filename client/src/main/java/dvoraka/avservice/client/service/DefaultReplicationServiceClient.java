package dvoraka.avservice.client.service;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.common.data.ReplicationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation for the remote replication service.
 */
public class DefaultReplicationServiceClient implements ReplicationServiceClient {

    private final ServerComponent serverComponent;

    private static final Logger log = LogManager.getLogger(DefaultReplicationServiceClient.class);


    public DefaultReplicationServiceClient(ServerComponent serverComponent) {
        this.serverComponent = requireNonNull(serverComponent);
    }

    @Override
    public void sendMessage(ReplicationMessage message) {
    }
}
