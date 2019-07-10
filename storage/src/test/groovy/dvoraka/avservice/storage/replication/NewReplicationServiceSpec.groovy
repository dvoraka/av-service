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
    String nodeId2 = "testNode2"
    @Shared
    String nodeId3 = "testNode3"
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
        component2 = new TestingReplicationAdapter(broker, nodeId2, broadcastKey)
        service2 = new NewReplicationService(component2)
        component3 = new TestingReplicationAdapter(broker, nodeId3, broadcastKey)
        service3 = new NewReplicationService(component3)

        broker.addMessageListener(component1, component1.getServiceId())
        broker.addMessageListener(component2, component2.getServiceId())
        broker.addMessageListener(component2, component3.getServiceId())

        broker.addMessageListener(component1, broadcastKey)
        broker.addMessageListener(component2, broadcastKey)
        broker.addMessageListener(component3, broadcastKey)
    }

    def "test"() {
        expect:
            component1.send(createDiscoverRequest(nodeId1))
    }
}
