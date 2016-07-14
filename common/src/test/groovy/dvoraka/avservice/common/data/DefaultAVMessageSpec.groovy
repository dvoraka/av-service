package dvoraka.avservice.common.data

import spock.lang.Specification

import java.nio.charset.StandardCharsets

/**
 * DefaultAvMessage test.
 */
class DefaultAVMessageSpec extends Specification {


    def "message creating test"() {
        setup:
        String testString = 'TEST-STRING'
        DefaultAvMessage message = new DefaultAvMessage.Builder(testString)
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

    def "create normal response test"() {
        setup:
        DefaultAvMessage message = new DefaultAvMessage.Builder('TEST-ID').build()
        String expCorrId = message.getId()

        AvMessage response = message.createResponse(false)

        expect:
        response.getCorrelationId().equals(expCorrId)
        response.getType().equals(AVMessageType.RESPONSE)
    }

    def "create infected response test"() {
        setup:
        DefaultAvMessage message = new DefaultAvMessage.Builder('TEST-ID').build()
        String expCorrId = message.getId()

        AvMessage response = message.createResponse(true)

        expect:
        response.getCorrelationId().equals(expCorrId)
        response.getType().equals(AVMessageType.RESPONSE)
    }

    def "null data test"() {
        setup:
        DefaultAvMessage message = new DefaultAvMessage.Builder('TEST-ID').build()

        expect:
        message.getData() == null
    }

    def "simple toString test"() {
        setup:
        DefaultAvMessage message = new DefaultAvMessage.Builder('TEST-ID').build()

        expect:
        message.toString().startsWith("DefaultAvMessage {")
        message.toString().endsWith("}")
    }
}
