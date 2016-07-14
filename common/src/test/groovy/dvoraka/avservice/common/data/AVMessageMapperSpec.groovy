package dvoraka.avservice.common.data

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.exception.MapperException
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import spock.lang.Specification

import java.nio.charset.StandardCharsets

/**
 * AvMessage mapper test.
 */
class AVMessageMapperSpec extends Specification {

    String testId = 'TEST-ID'
    String testCorrId = 'TEST-CORR-ID'
    String testAppId = 'TEST-APP-ID'
    String testVirusInfo = 'TEST-INFO'
    String testServiceId = 'TEST-SERVICE-1'
    int dataSize = 10


    def "AMQP Message -> AVMessage, v1"() {
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
        avMessage.getId().equals(testId)
        avMessage.getType().equals(AvMessageType.REQUEST)

        avMessage.getVirusInfo().equals(testVirusInfo)
        avMessage.getServiceId().equals(testServiceId)

        avMessage.getData().length == dataSize
    }

    def "AMQP Message -> AVMessage, empty message"() {
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

    def "AMQP Message -> AVMessage, ID only"() {
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

    def "AMQP Message -> AVMessage, message type only"() {
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

    def "AMQP Message -> AVMessage, message ID and type"() {
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

    def "AMQP Message -> AVMessage, with bad message type"() {
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

    def "AVMessage -> AMQP Message, v1"() {
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
        props.getMessageId().equals(testId)
        // appId
//        props.getAppId().equals(testAppId)
        // correlation ID
        Arrays.equals(props.getCorrelationId(), testCorrId.getBytes(StandardCharsets.UTF_8))
        // type
        props.getType().equals(AvMessageType.REQUEST.toString())

        // HEADERS
        // serviceId
        headers.get(AvMessageMapper.SERVICE_ID_KEY).equals(testServiceId)
        // virusInfo
        headers.get(AvMessageMapper.VIRUS_INFO_KEY).equals(testVirusInfo)

        message.getBody().length == dataSize
    }

    def "AVMessage -> AMQP Message, with normal message for old clients"() {
        setup:
        AvMessage avMessage = Utils.genNormalMessage()

        Message message = AvMessageMapper.transform(avMessage)
        Map<String, Object> headers = message.getMessageProperties().getHeaders()

        expect:
        headers.get('isClean').equals(1)
    }

    def "AVMessage -> AMQP Message, with infected message for old clients"() {
        setup:
        AvMessage avMessage = Utils.genInfectedMessage()

        Message message = AvMessageMapper.transform(avMessage)
        Map<String, Object> headers = message.getMessageProperties().getHeaders()

        expect:
        headers.get('isClean').equals(0)
    }
}
