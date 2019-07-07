package dvoraka.avservice.client.transport.test;

import dvoraka.avservice.client.transport.AbstractNetworkComponent;
import dvoraka.avservice.client.transport.ReplicationComponent;
import dvoraka.avservice.common.data.replication.ReplicationMessage;
import dvoraka.avservice.common.helper.MessageHelper;
import dvoraka.avservice.common.listener.ReplicationMessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Testing component for the replication service.
 */
public class TestingReplicationComponent
        extends AbstractNetworkComponent<ReplicationMessage, ReplicationMessageListener>
        implements ReplicationComponent, MessageHelper {

    private static final Logger log = LogManager.getLogger(TestingReplicationComponent.class);


    @Override
    public String getServiceId() {
        return null;
    }

    @Override
    public void send(ReplicationMessage message) {

    }
}
