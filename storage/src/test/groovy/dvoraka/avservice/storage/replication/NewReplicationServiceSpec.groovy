package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.transport.ReplicationComponent
import dvoraka.avservice.client.transport.test.DefaultSimpleBroker
import dvoraka.avservice.client.transport.test.SimpleBroker
import dvoraka.avservice.client.transport.test.TestingReplicationAdapter
import dvoraka.avservice.common.data.replication.ReplicationMessage
import dvoraka.avservice.common.helper.replication.ReplicationHelper
import spock.lang.Shared
import spock.lang.Specification

class NewReplicationServiceSpec extends Specification implements ReplicationHelper {

    @Shared
    String nodeId1 = "testNode1"
    @Shared
    String broadcastKey = "broadcast"

    ReplicationService service1
    ReplicationService service2
    ReplicationService service3

    ReplicationComponent component1
    ReplicationComponent component2
    ReplicationComponent component3

    SimpleBroker<ReplicationMessage> broker


    def setup() {
        broker = new DefaultSimpleBroker()

        component1 = new TestingReplicationAdapter(broker, nodeId1, broadcastKey)
        service1 = new NewReplicationService(component1)

        broker.addMessageListener(component1, nodeId1)

        broker.addMessageListener(component1, broadcastKey)
    }

    def "test"() {
        expect:
            component1.send(createDiscoverRequest(nodeId1))
    }
}
