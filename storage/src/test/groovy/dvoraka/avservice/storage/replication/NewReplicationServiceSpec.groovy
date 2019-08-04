package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.transport.ReplicationComponent
import dvoraka.avservice.client.transport.test.DefaultSimpleBroker
import dvoraka.avservice.client.transport.test.SimpleBroker
import dvoraka.avservice.client.transport.test.TestingReplicationAdapter
import dvoraka.avservice.common.data.replication.ReplicationMessage
import dvoraka.avservice.common.helper.replication.ReplicationHelper
import dvoraka.avservice.storage.service.DummyFileService
import dvoraka.avservice.storage.service.FileService
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

    TestingReplicationAdapter component1
    ReplicationComponent component2
    ReplicationComponent component3

    FileService fileService1
    FileService fileService2
    FileService fileService3

    SimpleBroker<ReplicationMessage> broker


    def setup() {
        broker = new DefaultSimpleBroker()

        fileService1 = new DummyFileService()
        fileService2 = new DummyFileService()
        fileService3 = new DummyFileService()

        component1 = new TestingReplicationAdapter(broker, nodeId1, broadcastKey)
        service1 = new NewReplicationService(fileService1, component1)
        component2 = new TestingReplicationAdapter(broker, nodeId2, broadcastKey)
        service2 = new NewReplicationService(fileService2, component2)
        component3 = new TestingReplicationAdapter(broker, nodeId3, broadcastKey)
        service3 = new NewReplicationService(fileService3, component3)

        broker.addMessageListener(component1, component1.getServiceId())
        broker.addMessageListener(component2, component2.getServiceId())
        broker.addMessageListener(component2, component3.getServiceId())

        broker.addMessageListener(component1, broadcastKey)
        broker.addMessageListener(component2, broadcastKey)
        broker.addMessageListener(component3, broadcastKey)

        service1.start()
        service2.start()
        service3.start()
    }

    def "test"() {
        expect:
            component1.send(createDiscoveryRequest(nodeId1))
    }
}
