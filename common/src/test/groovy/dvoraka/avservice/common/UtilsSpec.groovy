package dvoraka.avservice.common

import dvoraka.avservice.common.data.AVMessage
import spock.lang.Specification

import java.nio.charset.StandardCharsets

/**
 * Utils test.
 */
class UtilsSpec extends Specification {


    def "generate normal message"() {
        setup:
        AVMessage message = Utils.genNormalMessage()

        expect:
        checkMessageFields(message)
        message.getVirusInfo().equals('')
    }

    def "generate infected message"() {
        setup:
        AVMessage message = Utils.genInfectedMessage()
        byte[] expectedData = message.getData()

        expect:
        checkMessageFields(message)
        Arrays.equals(expectedData, Utils.EICAR.getBytes(StandardCharsets.UTF_8))
    }

    void checkMessageFields(AVMessage message) {
        assert message.getId()
        assert message.getCorrelationId()
        assert message.getData()
        assert message.getType()
        assert message.getServiceId()
        assert message.getVirusInfo() != null
    }
}
