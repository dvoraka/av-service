package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.transport.ReplicationComponent
import dvoraka.avservice.client.transport.test.TestingReplicationComponent
import spock.lang.Specification

class NewReplicationServiceSpec extends Specification {

    ReplicationService service1
    ReplicationService service2
    ReplicationService service3

    ReplicationComponent component1
    ReplicationComponent component2
    ReplicationComponent component3


    def setup() {
        component1 = new TestingReplicationComponent()
        service1 = new NewReplicationService(component1)
    }

    def "test"() {
        expect:
            component1
    }
}
