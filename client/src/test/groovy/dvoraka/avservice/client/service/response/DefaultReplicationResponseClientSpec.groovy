package dvoraka.avservice.client.service.response

import dvoraka.avservice.client.transport.ReplicationComponent
import dvoraka.avservice.common.data.replication.ReplicationMessage
import dvoraka.avservice.common.helper.replication.ReplicationHelper
import dvoraka.avservice.common.listener.ReplicationMessageListener
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

/**
 * DefaultReplicationResponseClient spec.
 */
class DefaultReplicationResponseClientSpec extends Specification implements ReplicationHelper {

    @Subject
    DefaultReplicationResponseClient client

    ReplicationComponent replicationComponent

    @Shared
    String nodeId = 'testId'


    def setup() {
        replicationComponent = Mock()

        client = new DefaultReplicationResponseClient(replicationComponent, nodeId)
        client.start()
        // wait for cache initialization
        sleep(1_000)
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

    def "get response wait without anything sent"() {
        expect:
            client.getResponseWait('test id', 10) == Optional.empty()
    }

    def "get response wait 2 without anything sent"() {
        expect:
            client.getResponseWait('test id', 10, 10) == Optional.empty()
    }

    def "get response wait and size without anything sent"() {
        expect:
            client.getResponseWaitSize('test id', 10, 2) == Optional.empty()
    }

    def "processing message - response"() {
        given:
            ReplicationMessage request = createDiscoveryRequest(nodeId)
            ReplicationMessage reply = createDiscoverReply(request, 'otherId')

        when:
            client.onMessage(reply)

        then:
            client.getResponse(request.getId())
    }

    def "processing message - 2 responses"() {
        given:
            ReplicationMessage request = createDiscoveryRequest(nodeId)
            ReplicationMessage reply = createDiscoverReply(request, 'otherId')

        when:
            client.onMessage(reply)
            client.onMessage(reply)

        then:
            client.getResponse(request.getId())
    }

    def "processing message - own message"() {
        given:
            ReplicationMessage request = createDiscoveryRequest(nodeId)

        when:
            client.onMessage(request)

        then:
            client.getResponse(request.getId()) == null
    }

    def "processing message - message for different node"() {
        given:
            ReplicationMessage request = createDiscoverReply(
                    createDiscoveryRequest('otherId'), 'otherId')

        when:
            client.onMessage(request)

        then:
            client.getResponse(request.getId()) == null
    }

    def "add and remove no response listener"() {
        given:
            ReplicationMessageListener listener = Mock()
            ReplicationMessage request = createDiscoveryRequest('otherId')

        when:
            client.addNoResponseMessageListener(listener)
            client.onMessage(request)

        then:
            1 * listener.onMessage(_)

        when:
            client.removeNoResponseMessageListener(listener)
            client.onMessage(request)

        then:
            0 * listener.onMessage(_)
    }
}
