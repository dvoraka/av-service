package dvoraka.avservice.common

import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.AvMessageInfo
import dvoraka.avservice.common.data.AvMessageSource
import spock.lang.Specification

import java.nio.charset.StandardCharsets

/**
 * Utils test.
 */
class UtilsSpec extends Specification {


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

    def "generate file message with username"() {
        setup:
            String testOwner = 'testOwner'
            AvMessage fileMessage = Utils.genFileMessage(testOwner)

        expect:
            checkMessageFields(fileMessage)
            checkFileMessageFields(fileMessage)
            fileMessage.getOwner() == testOwner
    }

    def "generate message info"() {
        given:
            AvMessageInfo messageInfo = Utils.genAvMessageInfo(AvMessageSource.TEST)

        expect:
            with(messageInfo) {
                getId()
                getSource()
                getServiceId()
                getCreated()
            }

            messageInfo.getSource() == AvMessageSource.TEST
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
