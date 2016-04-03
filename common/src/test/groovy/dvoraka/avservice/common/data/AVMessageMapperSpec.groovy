package dvoraka.avservice.common.data

import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import spock.lang.Specification

/**
 * AVMessage mapper test.
 */
class AVMessageMapperSpec extends Specification {

    String testId = 'TEST-ID'
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

    def "AVMessage -> AMQP Message"() {

    }
}
