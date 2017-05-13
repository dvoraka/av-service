package dvoraka.avservice.common.replication

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.Command
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.data.MessageRouting
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.data.ReplicationMessage
import dvoraka.avservice.common.data.ReplicationStatus
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

/**
 * Helper spec.
 */
class ReplicationHelperSpec extends Specification {

    @Subject
    ReplicationHelper helper

    ReplicationMessage result

    @Shared
    FileMessage fileMessage = Utils.genFileMessage()
    @Shared
    String nodeId = 'testID'
    @Shared
    String otherId = 'otherID'


    def setup() {
        helper = Spy()
    }

    def "save message"() {
        when:
            result = helper.createSaveMessage(fileMessage, nodeId, otherId)

        then:
            checkBase(result)
            result.getCommand() == Command.SAVE
    }

    def "save success"() {
        when:
            result = helper.createSaveSuccess(
                    helper.createSaveMessage(fileMessage, otherId, nodeId), nodeId)

        then:
            checkBase(result)
            result.getCommand() == Command.SAVE
            result.getReplicationStatus() == ReplicationStatus.OK
    }

    def "save failed"() {
        when:
            result = helper.createSaveFailed(
                    helper.createSaveMessage(fileMessage, otherId, nodeId), nodeId)

        then:
            checkBase(result)
            result.getCommand() == Command.SAVE
            result.getReplicationStatus() == ReplicationStatus.FAILED
    }

    def "load message"() {
        when:
            result = helper.createLoadMessage(fileMessage, nodeId, otherId)

        then:
            checkBase(result)
            result.getCommand() == Command.LOAD
    }

    def "load reply"() {
        when:
            result = helper.createLoadReply(
                    helper.createLoadMessage(fileMessage, nodeId, otherId), nodeId, otherId)

        then:
            checkBase(result)
            result.getCommand() == Command.LOAD
    }

    def "update message"() {
        when:
            result = helper.createUpdateMessage(fileMessage, nodeId, otherId)

        then:
            checkBase(result)
            result.getCommand() == Command.UPDATE
    }

    def "delete message"() {
        when:
            result = helper.createDeleteMessage(fileMessage, nodeId, otherId)

        then:
            checkBase(result)
            result.getCommand() == Command.DELETE
    }

    void checkBase(ReplicationMessage message) {
        assert message.getType() == MessageType.REPLICATION_COMMAND
        assert message.getRouting() == MessageRouting.UNICAST
        assert message.getFromId() == nodeId
        assert message.getToId() == otherId
        assert Arrays.equals(message.getData(), fileMessage.getData())
        assert message.getFilename() == fileMessage.getFilename()
        assert message.getOwner() == fileMessage.getOwner()
    }
}