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
    String mainId = 'testID'
    @Shared
    String otherId = 'otherID'


    def setup() {
        helper = Spy()
    }

    def "save message"() {
        when:
            result = helper.createSaveMessage(fileMessage, mainId, otherId)

        then:
            checkBase(result)
            Arrays.equals(result.getData(), fileMessage.getData())
            result.getCommand() == Command.SAVE
    }

    def "save success"() {
        when:
            result = helper.createSuccessResponse(
                    helper.createSaveMessage(fileMessage, otherId, mainId), mainId)

        then:
            checkBase(result)
            result.getData() == new byte[0]
            result.getCommand() == Command.SAVE
            result.getReplicationStatus() == ReplicationStatus.OK
    }

    def "save failed"() {
        when:
            result = helper.createFailedResponse(
                    helper.createSaveMessage(fileMessage, otherId, mainId), mainId)

        then:
            checkBase(result)
            result.getData() == new byte[0]
            result.getCommand() == Command.SAVE
            result.getReplicationStatus() == ReplicationStatus.FAILED
    }

    def "load message"() {
        when:
            result = helper.createLoadMessage(fileMessage, mainId, otherId)

        then:
            checkBase(result)
            result.getData() == new byte[0]
            result.getCommand() == Command.LOAD
    }

    def "load success"() {
        when:
            result = helper.createLoadSuccess(
                    fileMessage,
                    helper.createLoadMessage(Mock(FileMessage), otherId, mainId),
                    mainId)

        then:
            checkBase(result)
            Arrays.equals(result.getData(), fileMessage.getData())
            result.getCommand() == Command.LOAD
    }

    def "load failed"() {
        when:
            result = helper.createFailedResponse(
                    helper.createLoadMessage(fileMessage, otherId, mainId), mainId)

        then:
            checkBase(result)
            result.getData() == new byte[0]
            result.getCommand() == Command.LOAD
    }

    def "update message"() {
        when:
            result = helper.createUpdateMessage(fileMessage, mainId, otherId)

        then:
            checkBase(result)
            Arrays.equals(result.getData(), fileMessage.getData())
            result.getCommand() == Command.UPDATE
    }

    def "delete message"() {
        when:
            result = helper.createDeleteMessage(fileMessage, mainId, otherId)

        then:
            checkBase(result)
            result.getData() == new byte[0]
            result.getCommand() == Command.DELETE
    }

    void checkBase(ReplicationMessage message) {
        assert message.getType() == MessageType.REPLICATION_COMMAND
        assert message.getRouting() == MessageRouting.UNICAST
        assert message.getFromId() == mainId
        assert message.getToId() == otherId
        assert message.getFilename() == fileMessage.getFilename()
        assert message.getOwner() == fileMessage.getOwner()
    }
}
