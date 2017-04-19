package dvoraka.avservice.client.service.response

import dvoraka.avservice.client.ReplicationComponent
import dvoraka.avservice.common.data.ReplicationMessage
import dvoraka.avservice.common.replication.ReplicationHelper
import spock.lang.Specification
import spock.lang.Subject

/**
 * DefaultReplicationResponseClient spec.
 */
class DefaultReplicationResponseClientSpec extends Specification implements ReplicationHelper {

    @Subject
    DefaultReplicationResponseClient client

    ReplicationComponent replicationComponent
    String nodeId = 'testId'


    def setup() {
        replicationComponent = Mock()

        client = new DefaultReplicationResponseClient(replicationComponent, nodeId)
        client.start()
    }

    def cleanup() {
        client.stop()
    }

    def "start and stop"() {
        given:
            client = new DefaultReplicationResponseClient(replicationComponent, nodeId)

        when:
            client.start()

        then:
            client.isStarted()

        when: "start again"
            client.start()

        then:
            client.isStarted()

        when:
            client.stop()

        then:
            !client.isStarted()

        when: "stop again"
            client.stop()

        then:
            !client.isStarted()
    }

    def "processing message - response"() {
        given:
            ReplicationMessage request = createDiscoverRequest(nodeId)
            ReplicationMessage reply = createDiscoverReply(request, 'otherId')

        when:
            client.onMessage(reply)

        then:
            client.getResponse(request.getId())
    }
}
