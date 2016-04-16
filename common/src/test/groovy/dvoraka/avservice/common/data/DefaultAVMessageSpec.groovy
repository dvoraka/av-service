package dvoraka.avservice.common.data

import spock.lang.Specification

import java.nio.charset.StandardCharsets

/**
 * DefaultAVMessage test.
 */
class DefaultAVMessageSpec extends Specification {


    def "message creating test"() {
        setup:
        String testString = 'TEST-STRING'
        DefaultAVMessage message = new DefaultAVMessage.Builder(testString)
                .correlationId(testString)
                .data(testString.getBytes(StandardCharsets.UTF_8))
                .type(AVMessageType.REQUEST)
                .serviceId(testString)
                .virusInfo(testString)
                .build()

        expect:
        message.getId().equals(testString)
        message.getCorrelationId().equals(testString)
        Arrays.equals(message.getData(), testString.getBytes(StandardCharsets.UTF_8))
        message.getType().equals(AVMessageType.REQUEST)
        message.getServiceId().equals(testString)
        message.getVirusInfo().equals(testString)
    }

    def "create response test"() {
        setup:
        DefaultAVMessage message = new DefaultAVMessage.Builder('TEST-ID').build()
        String expCorrId = message.getId()

        AVMessage response = message.createResponse(false)

        expect:
        response.getCorrelationId().equals(expCorrId)
        response.getType().equals(AVMessageType.RESPONSE)
    }

    def "null data test"() {
        setup:
        DefaultAVMessage message = new DefaultAVMessage.Builder('TEST-ID').build()

        expect:
        message.getData() == null
    }

    def "simple toString test"() {
        setup:
        DefaultAVMessage message = new DefaultAVMessage.Builder('TEST-ID').build()

        expect:
        message.toString().startsWith("DefaultAVMessage {")
        message.toString().endsWith("}")
    }
}
