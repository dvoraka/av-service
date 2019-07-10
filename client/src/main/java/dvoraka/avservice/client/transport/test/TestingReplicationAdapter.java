package dvoraka.avservice.client.transport.test;

import dvoraka.avservice.client.transport.AbstractNetworkComponent;
import dvoraka.avservice.client.transport.ReplicationComponent;
import dvoraka.avservice.common.data.replication.MessageRouting;
import dvoraka.avservice.common.data.replication.ReplicationMessage;
import dvoraka.avservice.common.helper.MessageHelper;
import dvoraka.avservice.common.listener.ReplicationMessageListener;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.requireNonNull;

/**
 * Testing adapter for the replication service.
 */
@Slf4j
public class TestingReplicationAdapter
        extends AbstractNetworkComponent<ReplicationMessage, ReplicationMessageListener>
        implements ReplicationComponent, MessageHelper {

    private final SimpleBroker<ReplicationMessage> broker;
    private final String nodeId;
    private final String broadcastKey;


    public TestingReplicationAdapter(
            SimpleBroker<ReplicationMessage> broker,
            String nodeId,
            String broadcastKey
    ) {
        this.broker = requireNonNull(broker);
        this.nodeId = requireNonNull(nodeId);
        this.broadcastKey = requireNonNull(broadcastKey);
    }


    @Override
    public String getServiceId() {
        return nodeId;
    }

    @Override
    public void send(ReplicationMessage message) {
        log.debug("Send ({}): {}", nodeId, message);
        if (message.getRouting() == MessageRouting.BROADCAST) {
            broker.send(broadcastKey, message);
        } else {
            broker.send(message.getToId(), message);
        }
    }

    @Override
    public void onMessage(ReplicationMessage message) {
        log.debug("On message ({}): {}", nodeId, message);
    }
}
