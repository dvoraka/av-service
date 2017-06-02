package dvoraka.avservice.common.data

import dvoraka.avservice.common.Utils
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

import java.nio.charset.StandardCharsets

/**
 * Default AV message spec.
 */
class DefaultAvMessageSpec extends Specification {

    String testId = 'TEST-ID'
    String testFilename = 'testFilename'
    String testOwner = 'testOwner'


    def "message creating test"() {
        setup:
            String testString = 'TEST-STRING'
            DefaultAvMessage message = new DefaultAvMessage.Builder(testString)
                    .correlationId(testString)
                    .data(testString.getBytes(StandardCharsets.UTF_8))
                    .type(MessageType.FILE_CHECK)
                    .virusInfo(testString)
                    .build()

        expect:
            message.getId() == testString
            message.getCorrelationId() == testString
            Arrays.equals(message.getData(), testString.getBytes(StandardCharsets.UTF_8))
            message.getType() == MessageType.FILE_CHECK
            message.getVirusInfo() == testString
    }

    def "create normal response with string test"() {
        setup:
            DefaultAvMessage message = new DefaultAvMessage.Builder(testId).build()
            String expCorrId = message.getId()
            String virusInfo = 'Bad virus!'

            AvMessage response = message.createCheckResponse(virusInfo)

        expect:
            response.getCorrelationId() == expCorrId
            response.getType() == MessageType.RESPONSE
            response.getVirusInfo() == virusInfo
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
            response.getType() == MessageType.RESPONSE_ERROR
    }

    def "create file message test"() {
        given:
            DefaultAvMessage message = new DefaultAvMessage.Builder(testId)
                    .filename(testFilename)
                    .owner(testOwner)
                    .build()

            String expCorrId = message.getId()
            byte[] data = new byte[5]

        when:
            AvMessage response = message.createFileMessage(data, MessageType.FILE_UPDATE)

        then:
            response.getCorrelationId() == expCorrId
            response.getFilename() == testFilename
            response.getOwner() == testOwner
            response.getType() == MessageType.FILE_UPDATE
            Arrays.equals(response.getData(), data)
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

    def "not equality test"() {
        setup:
            AvMessage msg1 = new DefaultAvMessage.Builder(testId).build()
            AvMessage msg2 = new DefaultAvMessage.Builder(testId)
                    .virusInfo("some cool info")
                    .build()

        expect:
            msg1 != msg2
    }

    def "equals and hashcode verify"() {
        when:
            EqualsVerifier.forClass(DefaultAvMessage.class)
                    .suppress(Warning.NULL_FIELDS, Warning.STRICT_HASHCODE)
                    .verify()

        then:
            notThrown(Exception)
    }

    def "hashcode equality test"() {
        setup:
            AvMessage msg1 = new DefaultAvMessage.Builder(testId).build()
            AvMessage msg2 = new DefaultAvMessage.Builder(testId).build()

        expect:
            msg1.hashCode() == msg2.hashCode()
    }

    def "hashcode not equality test"() {
        setup:
            AvMessage msg1 = new DefaultAvMessage.Builder(Utils.genUuidString()).build()
            AvMessage msg2 = new DefaultAvMessage.Builder(Utils.genUuidString()).build()

        expect:
            msg1.hashCode() != msg2.hashCode()
    }
}
