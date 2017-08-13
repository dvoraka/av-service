package dvoraka.avservice.common.replication

import dvoraka.avservice.common.data.Command
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.data.replication.MessageRouting
import dvoraka.avservice.common.data.replication.ReplicationMessage
import dvoraka.avservice.common.data.replication.ReplicationStatus
import dvoraka.avservice.common.helper.replication.ReplicationServiceHelper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

/**
 * ReplicationServiceHelperSpec.
 */
class ReplicationServiceHelperSpec extends Specification {

    @Subject
    ReplicationServiceHelper helper

    ReplicationMessage result

    @Shared
    String testFilename = 'testFilename'
    @Shared
    String testOwner = 'testOwner'
    @Shared
    String testId = 'testID'
    @Shared
    String otherId = 'otherID'
    @Shared
    long testSequence = 999L


    def setup() {
        helper = Spy()
    }

    def "exists request"() {
        when:
            result = helper.createExistsRequest(testFilename, testOwner, testId)

        then:
            checkRequestBase(result)
            result.getCommand() == Command.EXISTS
    }

    def "exists reply"() {
        when:
            ReplicationMessage request = helper.createExistsRequest(
                    testFilename, testOwner, testId)
            result = helper.createExistsReply(request, otherId)

        then:
            checkReplyBase(result)
            result.getCommand() == Command.EXISTS
            result.getReplicationStatus() == ReplicationStatus.OK
    }

    def "status request"() {
        when:
            result = helper.createStatusRequest(testFilename, testOwner, testId)

        then:
            checkRequestBase(result)
            result.getCommand() == Command.STATUS
    }

    def "status OK reply"() {
        when:
            ReplicationMessage request = helper.createStatusRequest(
                    testFilename, testOwner, testId)
            result = helper.createOkStatusReply(request, otherId)

        then:
            checkReplyBase(result)
            result.getCommand() == Command.STATUS
            result.getReplicationStatus() == ReplicationStatus.OK
    }

    def "discover request"() {
        when:
            result = helper.createDiscoverRequest(testId)

        then:
            result.getCommand() == Command.DISCOVER

            result.getType() == MessageType.REPLICATION_SERVICE
            result.getRouting() == MessageRouting.BROADCAST

            result.getFromId() == testId
            result.getToId() == null
    }

    def "discover reply"() {
        when:
            ReplicationMessage request = helper.createDiscoverRequest(testId)
            result = helper.createDiscoverReply(request, otherId)

        then:
            checkReplyBase(result)
            result.getCommand() == Command.DISCOVER
            result.getReplicationStatus() == ReplicationStatus.READY
    }

    def "lock request"() {
        when:
            result = helper.createLockRequest(testFilename, testOwner, testId, testSequence)

        then:
            checkRequestBase(result)
            result.getCommand() == Command.LOCK
            result.getSequence() == testSequence
    }

    def "lock success reply"() {
        when:
            ReplicationMessage request = helper.createLockRequest(
                    testFilename, testOwner, testId, testSequence)
            result = helper.createLockSuccessReply(request, otherId)

        then:
            checkReplyBase(result)
            result.getCommand() == Command.LOCK
            result.getReplicationStatus() == ReplicationStatus.READY

            result.getFilename() == testFilename
            result.getOwner() == testOwner
    }

    def "lock failed reply"() {
        when:
            ReplicationMessage request = helper.createLockRequest(
                    testFilename, testOwner, testId, testSequence)
            result = helper.createLockFailedReply(request, testSequence, otherId)

        then:
            checkReplyBase(result)
            result.getCommand() == Command.LOCK
            result.getReplicationStatus() == ReplicationStatus.FAILED

            result.getSequence() == testSequence

            result.getFilename() == testFilename
            result.getOwner() == testOwner
    }

    def "unlock request"() {
        when:
            result = helper.createUnlockRequest(testFilename, testOwner, testId, testSequence)

        then:
            checkRequestBase(result)
            result.getCommand() == Command.UNLOCK
            result.getSequence() == testSequence
    }

    def "unlock success reply"() {
        when:
            ReplicationMessage request = helper.createUnlockRequest(
                    testFilename, testOwner, testId, testSequence)
            result = helper.createUnlockSuccessReply(request, otherId)

        then:
            checkReplyBase(result)
            result.getCommand() == Command.UNLOCK
            result.getReplicationStatus() == ReplicationStatus.OK

            result.getFilename() == testFilename
            result.getOwner() == testOwner
    }

    def "unlock failed reply"() {
        when:
            ReplicationMessage request = helper.createUnlockRequest(
                    testFilename, testOwner, testId, testSequence)
            result = helper.createUnlockFailedReply(request, otherId)

        then:
            checkReplyBase(result)
            result.getCommand() == Command.UNLOCK
            result.getReplicationStatus() == ReplicationStatus.FAILED

            result.getFilename() == testFilename
            result.getOwner() == testOwner
    }

    def "sequence request"() {
        when:
            result = helper.createSequenceRequest(testId)

        then:
            result.getCommand() == Command.SEQUENCE

            result.getType() == MessageType.REPLICATION_SERVICE
            result.getRouting() == MessageRouting.BROADCAST

            result.getFromId() == testId
            result.getToId() == null
    }

    def "sequence reply"() {
        when:
            ReplicationMessage request = helper.createSequenceRequest(testId)
            result = helper.createSequenceReply(request, otherId, testSequence)

        then:
            checkReplyBase(result)
            result.getCommand() == Command.SEQUENCE
            result.getSequence() == testSequence
    }

    void checkRequestBase(ReplicationMessage message) {
        assert message.getType() == MessageType.REPLICATION_SERVICE
        assert message.getRouting() == MessageRouting.BROADCAST

        assert message.getFromId() == testId
        assert message.getToId() == null

        assert message.getFilename() == testFilename
        assert message.getOwner() == testOwner
    }

    void checkReplyBase(ReplicationMessage message) {
        assert message.getType() == MessageType.REPLICATION_SERVICE
        assert message.getRouting() == MessageRouting.UNICAST

        assert message.getFromId() == otherId
        assert message.getToId() == testId
    }
}
