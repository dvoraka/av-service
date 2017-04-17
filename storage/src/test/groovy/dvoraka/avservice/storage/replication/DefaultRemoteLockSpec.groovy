package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationResponseClient
import spock.lang.Specification
import spock.lang.Subject

/**
 * DefaultRemoteLock spec.
 */
class DefaultRemoteLockSpec extends Specification implements ReplicationHelper {

    @Subject
    DefaultRemoteLock lock

    ReplicationServiceClient serviceClient
    ReplicationResponseClient responseClient
    String nodeId


    def setup() {
        serviceClient = Mock()
        responseClient = Mock()
        nodeId = 'testNode'

        lock = new DefaultRemoteLock(serviceClient, responseClient, nodeId)
    }

    def "start"() {
        when:
            lock.start()

        then:
            1 * responseClient.addNoResponseMessageListener(_)
    }

    def "stop"() {
        when:
            lock.stop()

        then:
            1 * responseClient.removeNoResponseMessageListener(_)
    }

    def "lock file"() {
        when:
            lock.lockForFile('test', 'test', 5)

        then:
            1 * serviceClient.sendMessage(_)
            1 * responseClient.getResponseWait(_, _, _) >> Optional.empty()
    }

    def "unlock file"() {
        when:
            lock.unlockForFile('test', 'test', 5)

        then:
            1 * serviceClient.sendMessage(_)
            1 * responseClient.getResponseWait(_, _, _) >> Optional.empty()
    }

    def "on message with unicast message"() {
        when:
            lock.onMessage(createDiscoverReply(createDiscoverRequest(nodeId), nodeId))

        then:
            0 * _
    }
}
