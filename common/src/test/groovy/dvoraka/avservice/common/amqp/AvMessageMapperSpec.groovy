package dvoraka.avservice.common.amqp

import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.exception.MapperException
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import spock.lang.Subject

/**
 * AvMessage mapper test.
 */
class AvMessageMapperSpec extends Specification {

    @Subject
    AvMessageMapper mapper

    String testId = 'TEST-ID'
    String testCorrId = 'TEST-CORR-ID'
    String testAppId = 'TEST-APP-ID'
    String testVirusInfo = 'TEST-INFO'
    String testServiceId = 'TEST-SERVICE-1'
    int dataSize = 10


    def setup() {
        mapper = new AvMessageMapper()
    }

    def "transform Message to AvMessage with null argument"() {
        when:
            mapper.transform((Message) null)

        then:
            thrown(NullPointerException)
    }

    def "transform AvMessage to Message with null argument"() {
        when:
            mapper.transform((AvMessage) null)

        then:
            thrown(NullPointerException)
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
            props.setType(MessageType.REQUEST.toString())

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
            AvMessage avMessage = mapper.transform(message)

        expect:
            avMessage.getId() == testId
            avMessage.getType() == MessageType.REQUEST

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
            mapper.transform(message)

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
            mapper.transform(message)

        then:
            thrown(MapperException)
    }

    def "AMQP Message -> AvMessage, message type only"() {
        given:
            MessageProperties props = new MessageProperties()

            // PROPERTIES
            props.setType(MessageType.REQUEST.toString())
            // HEADERS
            // BODY

            Message message = new Message(null, props)

        when:
            mapper.transform(message)

        then:
            thrown(MapperException)
    }

    def "AMQP Message -> AvMessage, message ID and type"() {
        given:
            MessageProperties props = new MessageProperties()

            // PROPERTIES
            props.setMessageId(testId)
            props.setType(MessageType.REQUEST.toString())
            // HEADERS
            // BODY

            Message message = new Message(null, props)

        when:
            mapper.transform(message)

        then:
            notThrown(MapperException)
    }

    def "AMQP Message -> AvMessage, message ID, type and correlation ID"() {
        given:
            MessageProperties props = new MessageProperties()

            // PROPERTIES
            props.setMessageId(testId)
            props.setType(MessageType.REQUEST.toString())
            props.setCorrelationId(testCorrId)
            // HEADERS
            // BODY

            Message message = new Message(null, props)

        when:
            mapper.transform(message)

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
            mapper.transform(message)

        then:
            thrown(MapperException)
    }

    def "AvMessage -> AMQP Message, v1"() {
        setup:
            AvMessage avMessage = new DefaultAvMessage.Builder(testId)
                    .correlationId(testCorrId)
                    .data(new byte[dataSize])
                    .type(MessageType.REQUEST)
                    .serviceId(testServiceId)
                    .virusInfo(testVirusInfo)
                    .build()

            // transform to Message
            Message message = mapper.transform(avMessage)
            MessageProperties props = message.getMessageProperties()
            Map<String, Object> headers = props.getHeaders()

        expect:
            // PROPERTIES
            // id
            props.getMessageId() == testId
            // appId
//        props.getAppId().equals(testAppId)
            // correlation ID
            props.getCorrelationId() == testCorrId
            // type
            props.getType() == MessageType.REQUEST.toString()

            // HEADERS
            // serviceId
            headers.get(AvMessageMapper.SERVICE_ID_KEY) == testServiceId
            // virusInfo
            headers.get(AvMessageMapper.VIRUS_INFO_KEY) == testVirusInfo

            message.getBody().length == dataSize
    }

    def "AvMessage -> AMQP Message, v1, without message type"() {
        given:
            AvMessage avMessage = new DefaultAvMessage.Builder(testId)
                    .build()

        when:
            mapper.transform(avMessage)

        then:
            thrown(MapperException)
    }

    def "AvMessage -> AMQP Message, v1, without message ID"() {

        given: "normally it's not possible to build a message without ID"
            AvMessage avMessage = new DefaultAvMessage.Builder(testId)
                    .build()
            ReflectionTestUtils.setField(avMessage, "id", null, null)

        when:
            mapper.transform(avMessage)

        then:
            thrown(MapperException)
    }
}
