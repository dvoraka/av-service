package dvoraka.avservice.common.data

import spock.lang.Specification

import java.nio.charset.StandardCharsets

/**
 * DefaultAvMessage test.
 */
class DefaultAvMessageSpec extends Specification {

    String testId = 'TEST-ID'


    def "message creating test"() {
        setup:
            String testString = 'TEST-STRING'
            DefaultAvMessage message = new DefaultAvMessage.Builder(testString)
                    .correlationId(testString)
                    .data(testString.getBytes(StandardCharsets.UTF_8))
                    .type(AvMessageType.REQUEST)
                    .serviceId(testString)
                    .virusInfo(testString)
                    .build()

        expect:
            message.getId() == testString
            message.getCorrelationId() == testString
            Arrays.equals(message.getData(), testString.getBytes(StandardCharsets.UTF_8))
            message.getType() == AvMessageType.REQUEST
            message.getServiceId() == testString
            message.getVirusInfo() == testString
    }

    def "create normal response test"() {
        setup:
            DefaultAvMessage message = new DefaultAvMessage.Builder(testId).build()
            String expCorrId = message.getId()

            AvMessage response = message.createResponse(false)

        expect:
            response.getCorrelationId() == expCorrId
            response.getType() == AvMessageType.RESPONSE
    }

    def "create normal response with string test"() {
        setup:
            DefaultAvMessage message = new DefaultAvMessage.Builder(testId).build()
            String expCorrId = message.getId()
            String virusInfo = 'Bad virus!'

            AvMessage response = message.createResponseWithString(virusInfo)

        expect:
            response.getCorrelationId() == expCorrId
            response.getType() == AvMessageType.RESPONSE
            response.getVirusInfo() == virusInfo
    }

    def "create infected response test"() {
        setup:
            DefaultAvMessage message = new DefaultAvMessage.Builder(testId).build()
            String expCorrId = message.getId()

            AvMessage response = message.createResponse(true)

        expect:
            response.getCorrelationId() == expCorrId
            response.getType() == AvMessageType.RESPONSE
    }

    def "create error response test"() {
        given:
            DefaultAvMessage message = new DefaultAvMessage.Builder(testId).build()
            String expCorrId = message.getId()
            String errorMsg = "TEST-ERROR"

        when:
            AvMessage response = message.createErrorResponse(errorMsg)

        then:
            response.getCorrelationId() == expCorrId
            response.getType() == AvMessageType.RESPONSE_ERROR
    }

    def "null data test"() {
        setup:
            DefaultAvMessage message = new DefaultAvMessage.Builder(testId).build()

        expect:
            message.getData() == new byte[0]
    }

    def "simple toString test"() {
        setup:
            DefaultAvMessage message = new DefaultAvMessage.Builder(testId).build()

        expect:
            message.toString().startsWith("DefaultAvMessage {")
            message.toString().endsWith("}")
    }

    def "equality test"() {
        setup:
            AvMessage msg1 = new DefaultAvMessage.Builder(testId).build()
            AvMessage msg2 = new DefaultAvMessage.Builder(testId).build()

        expect:
            msg1 == msg2
    }
}
