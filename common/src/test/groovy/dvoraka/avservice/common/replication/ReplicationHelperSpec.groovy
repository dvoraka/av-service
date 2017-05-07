package dvoraka.avservice.common.replication

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.Command
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.data.MessageRouting
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.data.ReplicationMessage
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
    String testId = 'testID'
    @Shared
    String otherId = 'otherID'


    def setup() {
        helper = Spy()
    }

    def "save message"() {
        when:
            result = helper.createSaveMessage(fileMessage, testId, otherId)

        then:
            checkBasics(result)
            result.with {
                getRouting() == MessageRouting.UNICAST
                getCommand() == Command.SAVE
                getToId() == otherId
            }
    }

    void checkBasics(ReplicationMessage message) {
        assert message.getType() == MessageType.REPLICATION_COMMAND
        assert message.getFromId() == testId
        assert Arrays.equals(message.getData(), fileMessage.getData())
        assert message.getFilename() == fileMessage.getFilename()
        assert message.getOwner() == fileMessage.getOwner()
    }
}
