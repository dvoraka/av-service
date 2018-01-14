package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationMessageList
import dvoraka.avservice.client.service.response.ReplicationResponseClient
import dvoraka.avservice.common.data.replication.ReplicationMessage
import dvoraka.avservice.common.helper.WaitingHelper
import dvoraka.avservice.common.helper.replication.ReplicationHelper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.util.concurrent.PollingConditions


class DefaultRemoteLockSpec extends Specification implements ReplicationHelper, WaitingHelper {

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
    @Shared
    PollingConditions pollingConditions = new PollingConditions(timeout: 3)


    def setup() {
        serviceClient = Mock()

        responseClient = Mock()
        responseClient.isRunning() >> true
        responseClient.getResponseWait(_, _) >> Optional.empty()

        lock = new DefaultRemoteLock(serviceClient, responseClient, nodeId)
    }

    def "start"() {
        when:
            lock.start()

        then:
            1 * responseClient.addNoResponseMessageListener(_)

            pollingConditions.eventually {
                lock.isRunning()
            }
    }

    def "stop"() {
        when:
            lock.stop()

        then:
            1 * responseClient.removeNoResponseMessageListener(_)

            pollingConditions.eventually {
                !lock.isRunning()
            }
    }

    def "lock file unsuccessfully"() {
        when:
            boolean result = lock.lockForFile('test', 'test', 5)

        then:
            2 * serviceClient.sendMessage(_) // request and force unlock
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

    def "lock file with lower lock count"() {
        when:
            boolean result = lock.lockForFile('test', 'test', 2)

        then:
            2 * serviceClient.sendMessage(_) // request and force unlock
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

    def "on message with sequence request with initialized lock"() {
        setup:
            startLock()

        when:
            lock.onMessage(createSequenceRequest(nodeId))

        then:
            1 * serviceClient.sendMessage(_)
    }

    def "on message with sequence request without initialized lock"() {
        when:
            lock.onMessage(createSequenceRequest(nodeId))

        then:
            0 * _
    }

    def "on message with lock request with wrong sequence"() {
        setup:
            startLock()

        when:
            lock.onMessage(createLockRequest(testFilename, testOwner, nodeId, 99))

        then:
            1 * serviceClient.sendMessage(_)
    }

    def "on message with lock request with right sequence"() {
        setup:
            startLock()

        when:
            lock.onMessage(createLockRequest(testFilename, testOwner, nodeId, 1))

        then:
            1 * serviceClient.sendMessage(_)
    }

    def "on message with unlock request"() {
        setup:
            startLock()

        when:
            lock.onMessage(createUnlockRequest(testFilename, testOwner, nodeId, 1))

        then:
            1 * serviceClient.sendMessage(_)
    }

    def "on message with force unlock request for locked file"() {
        setup:
            startLock()

        expect:
            lock.onMessage(createLockRequest(testFilename, testOwner, nodeId, 1))

        when:
            lock.onMessage(createForceUnlockRequest(testFilename, testOwner, nodeId, 1))

        then:
            0 * serviceClient.sendMessage(_)
    }

    def "set max response time"() {
        given:
            int maxTime = 1234

        when:
            lock.setMaxResponseTime(maxTime)

        then:
            lock.getMaxResponseTime() == maxTime
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

    void startLock() {
        lock.start()
        waitUntil({ lock.isRunning() })
    }
}
