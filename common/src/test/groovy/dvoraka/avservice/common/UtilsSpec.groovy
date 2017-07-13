package dvoraka.avservice.common

import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.AvMessageInfo
import dvoraka.avservice.common.data.InfoSource
import dvoraka.avservice.common.data.MessageType
import spock.lang.Specification

import java.nio.charset.StandardCharsets

/**
 * Utils test.
 */
class UtilsSpec extends Specification {

    String testOwner = 'testOwner'


    def "generate normal message"() {
        setup:
            AvMessage message = Utils.genMessage()

        expect:
            checkMessageFields(message)
    }

    def "generate infected message"() {
        setup:
            AvMessage message = Utils.genInfectedMessage()
            byte[] expectedData = message.getData()

        expect:
            checkMessageFields(message)
            Arrays.equals(expectedData, Utils.EICAR.getBytes(StandardCharsets.UTF_8))
    }

    def "generate file message"() {
        setup:
            AvMessage fileMessage = Utils.genFileMessage()

        expect:
            checkMessageFields(fileMessage)
            checkFileMessageFields(fileMessage)
    }

    def "generate save message"() {
        setup:
            AvMessage fileMessage = Utils.genSaveMessage()

        expect:
            checkMessageFields(fileMessage)
            checkFileMessageFields(fileMessage)
            fileMessage.getType() == MessageType.FILE_SAVE
    }

    def "generate load message"() {
        setup:
            AvMessage fileMessage = Utils.genLoadMessage()

        expect:
            checkMessageFields(fileMessage)
            checkFileMessageFields(fileMessage)
            fileMessage.getType() == MessageType.FILE_LOAD
    }

    def "generate update message"() {
        setup:
            AvMessage fileMessage = Utils.genUpdateMessage()

        expect:
            checkMessageFields(fileMessage)
            checkFileMessageFields(fileMessage)
            fileMessage.getType() == MessageType.FILE_UPDATE
    }

    def "generate delete message"() {
        setup:
            AvMessage fileMessage = Utils.genDeleteMessage()

        expect:
            checkMessageFields(fileMessage)
            checkFileMessageFields(fileMessage)
            fileMessage.getType() == MessageType.FILE_DELETE
    }

    def "generate file message with username"() {
        setup:
            AvMessage fileMessage = Utils.genFileMessage(testOwner)

        expect:
            checkMessageFields(fileMessage)
            checkFileMessageFields(fileMessage)
            fileMessage.getOwner() == testOwner
    }

    def "generate infected file message"() {
        setup:
            AvMessage fileMessage = Utils.genInfectedFileMessage()
            byte[] expectedData = fileMessage.getData()

        expect:
            checkMessageFields(fileMessage)
            checkFileMessageFields(fileMessage)
            Arrays.equals(expectedData, Utils.EICAR.getBytes(StandardCharsets.UTF_8))
    }

    def "generate infected file message with username"() {
        setup:
            AvMessage fileMessage = Utils.genInfectedFileMessage(testOwner)
            byte[] expectedData = fileMessage.getData()

        expect:
            checkMessageFields(fileMessage)
            checkFileMessageFields(fileMessage)
            Arrays.equals(expectedData, Utils.EICAR.getBytes(StandardCharsets.UTF_8))
            fileMessage.getOwner() == testOwner
    }

    def "generate message info"() {
        given:
            AvMessageInfo messageInfo = Utils.genAvMessageInfo(InfoSource.TEST)

        expect:
            with(messageInfo) {
                getId()
                getSource()
                getServiceId()
                getCreated()
            }

            messageInfo.getSource() == InfoSource.TEST
    }

    void checkMessageFields(AvMessage message) {
        assert message.getId()
        assert message.getCorrelationId()
        assert message.getType()

        assert message.getData()
    }

    void checkFileMessageFields(AvMessage message) {
        assert message.getFilename()
        assert message.getOwner()
        assert message.getData()
    }
}
