package dvoraka.avservice.client.service

import dvoraka.avservice.client.ReplicationComponent
import dvoraka.avservice.common.data.replication.ReplicationMessage
import dvoraka.avservice.common.replication.ReplicationHelper
import spock.lang.Specification
import spock.lang.Subject

/**
 * Client spec.
 */
class DefaultReplicationServiceClientSpec extends Specification implements ReplicationHelper {

    @Subject
    DefaultReplicationServiceClient client

    ReplicationComponent replicationComponent
    String nodeId = 'testID'


    def setup() {
        replicationComponent = Mock()
        client = new DefaultReplicationServiceClient(replicationComponent, nodeId)
    }

    def "send message"() {
        given:
            ReplicationMessage message = createDiscoverRequest(nodeId)

        when:
            client.sendMessage(message)

        then:
            1 * replicationComponent.sendMessage(message)
    }

    def "send message with incorrect ID"() {
        given:
            ReplicationMessage message = createDiscoverRequest('other ID')

        when:
            client.sendMessage(message)

        then:
            0 * replicationComponent.sendMessage(message)
    }
}
