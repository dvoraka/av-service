package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationMessageList
import dvoraka.avservice.client.service.response.ReplicationResponseClient
import dvoraka.avservice.common.data.replication.ReplicationMessage
import dvoraka.avservice.common.replication.ReplicationHelper
import spock.lang.Shared
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

    @Shared
    String nodeId = 'testNode'
    @Shared
    String testFilename = 'testFilename'
    @Shared
    String testOwner = 'testOwner'


    def setup() {
        serviceClient = Mock()
        responseClient = Mock()

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

    def "lock file unsuccessfully"() {
        when:
            boolean result = lock.lockForFile('test', 'test', 5)

        then:
            1 * serviceClient.sendMessage(_)
            1 * responseClient.getResponseWaitSize(_, _, _) >> Optional.empty()
            !result
    }

    def "lock file successfully"() {
        when:
            boolean result = lock.lockForFile('test', 'test', 1)

        then:
            1 * serviceClient.sendMessage(_)
            1 * responseClient.getResponseWaitSize(_, _, _) >> Optional.of(genLockResponse())
            result
    }

    def "lock file and get lower lock count"() {
        when:
            boolean result = lock.lockForFile('test', 'test', 2)

        then:
            1 * serviceClient.sendMessage(_)
            1 * responseClient.getResponseWaitSize(_, _, _) >> Optional.of(genLockResponse())
            !result
    }

    def "unlock file"() {
        when:
            lock.unlockForFile('test', 'test', 5)

        then:
            1 * serviceClient.sendMessage(_)
            1 * responseClient.getResponseWaitSize(_, _, _) >> Optional.empty()
    }

    def "synchronize"() {
        when:
            lock.synchronize()

        then:
            1 * serviceClient.sendMessage(_)
            1 * responseClient.getResponseWait(_, _) >> Optional.of(genSequenceResponse())
    }

    def "on message with unicast message"() {
        when:
            lock.onMessage(createDiscoverReply(createDiscoverRequest(nodeId), nodeId))

        then:
            0 * _
    }

    def "on message with unknown broadcast message"() {
        when:
            lock.onMessage(createExistsRequest(testOwner, testFilename, nodeId))

        then:
            0 * _
    }

    def "on message with sequence request"() {
        when:
            lock.onMessage(createSequenceRequest(nodeId))

        then:
            1 * serviceClient.sendMessage(_)
    }

    def "on message with lock request with wrong sequence"() {
        when:
            lock.onMessage(createLockRequest(testFilename, testOwner, nodeId, 1))

        then:
            1 * serviceClient.sendMessage(_)
    }

    def "on message with lock request with right sequence"() {
        when:
            lock.onMessage(createLockRequest(testFilename, testOwner, nodeId, 0))

        then:
            1 * serviceClient.sendMessage(_)
    }

    def "on message with unlock request"() {
        when:
            lock.onMessage(createUnlockRequest(testFilename, testOwner, nodeId, 1))

        then:
            1 * serviceClient.sendMessage(_)
    }

    ReplicationMessageList genLockResponse() {
        ReplicationMessage lockRequest = createLockRequest(
                testFilename, testOwner, nodeId, 1)
        ReplicationMessage lockReply = createLockSuccessReply(lockRequest, 'otherNode')
        ReplicationMessageList messageList = new ReplicationMessageList()
        messageList.add(lockReply)

        return messageList
    }

    ReplicationMessageList genSequenceResponse() {
        ReplicationMessage seqRequest = createSequenceRequest(nodeId)
        ReplicationMessage seqReply = createSequenceReply(seqRequest, nodeId, 1)
        ReplicationMessageList messageList = new ReplicationMessageList()
        messageList.add(seqReply)

        return messageList
    }
}
