package dvoraka.avservice.common.data

import dvoraka.avservice.common.exception.MapperException
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import spock.lang.Specification

import java.nio.charset.StandardCharsets

/**
 * AVMessage mapper test.
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
        props.setType(AVMessageType.REQUEST.toString())

        // HEADERS
        // virusInfo
        props.setHeader(AVMessageMapper.VIRUS_INFO_KEY, testVirusInfo)
        // serviceId
        props.setHeader(AVMessageMapper.SERVICE_ID_KEY, testServiceId)

        // BODY
        // data
        byte[] body = new byte[dataSize]

        // create AMQP message
        Message message = new Message(body, props)
        // transform to AVMessage
        AVMessage avMessage = AVMessageMapper.transform(message)

        expect:
        avMessage.getId().equals(testId)
        avMessage.getType().equals(AVMessageType.REQUEST)

        avMessage.getVirusInfo().equals(testVirusInfo)
        avMessage.getServiceId().equals(testServiceId)

        avMessage.getData().length == dataSize
    }

    def "AMQP Message -> AVMessage, empty message"() {
        setup:
        MessageProperties props = new MessageProperties()

        // PROPERTIES
        // HEADERS
        // BODY

        Message message = new Message(null, props)

        when:
        AVMessage avMessage = AVMessageMapper.transform(message)

        then:
        thrown(MapperException)
    }

    def "AMQP Message -> AVMessage, ID only"() {
        setup:
        MessageProperties props = new MessageProperties()

        // PROPERTIES
        props.setMessageId(testId)
        // HEADERS
        // BODY

        Message message = new Message(null, props)

        when:
        AVMessage avMessage = AVMessageMapper.transform(message)

        then:
        thrown(MapperException)
    }

    def "AMQP Message -> AVMessage, message type only"() {
        setup:
        MessageProperties props = new MessageProperties()

        // PROPERTIES
        props.setType(AVMessageType.REQUEST.toString())
        // HEADERS
        // BODY

        Message message = new Message(null, props)

        when:
        AVMessage avMessage = AVMessageMapper.transform(message)

        then:
        thrown(MapperException)
    }

    def "AMQP Message -> AVMessage, message ID and type"() {
        setup:
        MessageProperties props = new MessageProperties()

        // PROPERTIES
        props.setMessageId(testId)
        props.setType(AVMessageType.REQUEST.toString())
        // HEADERS
        // BODY

        Message message = new Message(null, props)

        when:
        AVMessage avMessage = AVMessageMapper.transform(message)

        then:
        notThrown(MapperException)
    }

    def "AVMessage -> AMQP Message, v1"() {
        setup:
        AVMessage avMessage = new DefaultAVMessage.Builder(testId)
                .correlationId(testCorrId)
                .data(new byte[dataSize])
                .type(AVMessageType.REQUEST)
                .serviceId(testServiceId)
                .virusInfo(testVirusInfo)
                .build()

        // transform to Message
        Message message = AVMessageMapper.transform(avMessage)
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
        props.getType().equals(AVMessageType.REQUEST.toString())

        // HEADERS
        // serviceId
        headers.get(AVMessageMapper.SERVICE_ID_KEY).equals(testServiceId)
        // virusInfo
        headers.get(AVMessageMapper.VIRUS_INFO_KEY).equals(testVirusInfo)

        message.getBody().length == dataSize
    }
}
