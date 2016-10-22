package dvoraka.avservice.common.amqp

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.AvMessageType
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.exception.MapperException
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.charset.StandardCharsets

/**
 * AvMessage mapper test.
 */
class AvMessageMapperSpec extends Specification {

    String testId = 'TEST-ID'
    String testCorrId = 'TEST-CORR-ID'
    String testAppId = 'TEST-APP-ID'
    String testVirusInfo = 'TEST-INFO'
    String testServiceId = 'TEST-SERVICE-1'
    int dataSize = 10


    def "transform Message to AvMessage with null argument"() {
        when:
            AvMessageMapper.transform((Message) null)

        then:
            thrown(IllegalArgumentException)
    }

    def "transform AvMessage to Message with null argument"() {
        when:
            AvMessageMapper.transform((AvMessage) null)

        then:
            thrown(IllegalArgumentException)
    }

    def "AMQP Message -> AvMessage, v1"() {
        setup:
            MessageProperties props = new MessageProperties()

            // PROPERTIES
            // id
            props.setMessageId(testId)
            // appId
            props.setAppId(testAppId)
            // type
            props.setType(AvMessageType.REQUEST.toString())

            // HEADERS
            // virusInfo
            props.setHeader(AvMessageMapper.VIRUS_INFO_KEY, testVirusInfo)
            // serviceId
            props.setHeader(AvMessageMapper.SERVICE_ID_KEY, testServiceId)

            // BODY
            // data
            byte[] body = new byte[dataSize]

            // create AMQP message
            Message message = new Message(body, props)
            // transform to AvMessage
            AvMessage avMessage = AvMessageMapper.transform(message)

        expect:
            avMessage.getId() == testId
            avMessage.getType() == AvMessageType.REQUEST

            avMessage.getVirusInfo() == testVirusInfo
            avMessage.getServiceId() == testServiceId

            avMessage.getData().length == dataSize
    }

    def "AMQP Message -> AvMessage, empty message"() {
        given:
            MessageProperties props = new MessageProperties()

            // PROPERTIES
            // HEADERS
            // BODY

            Message message = new Message(null, props)

        when:
            AvMessageMapper.transform(message)

        then:
            thrown(MapperException)
    }

    def "AMQP Message -> AvMessage, ID only"() {
        given:
            MessageProperties props = new MessageProperties()

            // PROPERTIES
            props.setMessageId(testId)
            // HEADERS
            // BODY

            Message message = new Message(null, props)

        when:
            AvMessageMapper.transform(message)

        then:
            thrown(MapperException)
    }

    def "AMQP Message -> AvMessage, message type only"() {
        given:
            MessageProperties props = new MessageProperties()

            // PROPERTIES
            props.setType(AvMessageType.REQUEST.toString())
            // HEADERS
            // BODY

            Message message = new Message(null, props)

        when:
            AvMessageMapper.transform(message)

        then:
            thrown(MapperException)
    }

    def "AMQP Message -> AvMessage, message ID and type"() {
        given:
            MessageProperties props = new MessageProperties()

            // PROPERTIES
            props.setMessageId(testId)
            props.setType(AvMessageType.REQUEST.toString())
            // HEADERS
            // BODY

            Message message = new Message(null, props)

        when:
            AvMessageMapper.transform(message)

        then:
            notThrown(MapperException)
    }

    def "AMQP Message -> AvMessage, with bad message type"() {
        given:
            MessageProperties props = new MessageProperties()

            // PROPERTIES
            props.setMessageId(testId)
            props.setType("X-BAD-TYPE-X")
            // HEADERS
            // BODY

            Message message = new Message(null, props)

        when:
            AvMessageMapper.transform(message)

        then:
            thrown(MapperException)
    }

    def "AvMessage -> AMQP Message, v1"() {
        setup:
            AvMessage avMessage = new DefaultAvMessage.Builder(testId)
                    .correlationId(testCorrId)
                    .data(new byte[dataSize])
                    .type(AvMessageType.REQUEST)
                    .serviceId(testServiceId)
                    .virusInfo(testVirusInfo)
                    .build()

            // transform to Message
            Message message = AvMessageMapper.transform(avMessage)
            MessageProperties props = message.getMessageProperties()
            Map<String, Object> headers = props.getHeaders()

        expect:
            // PROPERTIES
            // id
            props.getMessageId() == testId
            // correlation ID
            props.getCorrelationIdString() == testCorrId
            // type
            props.getType() == AvMessageType.REQUEST.toString()

            // HEADERS
            // serviceId
            headers.get(AvMessageMapper.SERVICE_ID_KEY) == testServiceId
            // virusInfo
            headers.get(AvMessageMapper.VIRUS_INFO_KEY) == testVirusInfo

            message.getBody().length == dataSize
    }

    @Ignore("deprecated")
    def "AVMessage -> AMQP Message, with normal message for old clients"() {
        setup:
            AvMessage avMessage = Utils.genNormalMessage()

            Message message = AvMessageMapper.transform(avMessage)
            Map<String, Object> headers = message.getMessageProperties().getHeaders()

        expect:
            headers.get('isClean') == 1
    }

    @Ignore("deprecated")
    def "AvMessage -> AMQP Message, with infected message for old clients"() {
        setup:
            AvMessage avMessage = Utils.genInfectedMessage()

            Message message = AvMessageMapper.transform(avMessage)
            Map<String, Object> headers = message.getMessageProperties().getHeaders()

        expect:
            headers.get('isClean') == 0
    }
}
