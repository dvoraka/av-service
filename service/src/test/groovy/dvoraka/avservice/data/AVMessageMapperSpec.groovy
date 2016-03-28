package dvoraka.avservice.data

import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import spock.lang.Specification

/**
 * AVMessage mapper test.
 */
class AVMessageMapperSpec extends Specification {

    String testId = 'TEST-ID'
    String testAppId = 'TEST-APP-ID'
    int dataSize = 10


    def "Message -> AVMessage"() {
        setup:
        MessageProperties props = new MessageProperties()
        props.setMessageId(testId)
        props.setAppId(testAppId)
        props.setType(AVMessageType.REQUEST.toString())

        byte[] body = new byte[dataSize]
        Message message = new Message(body, props)

        AVMessage avMessage = AVMessageMapper.transform(message)

        expect:
        avMessage.getId().equals(testId)
//        avMessage.getType().equals(AVMessageType.REQUEST)

        avMessage.getData().length == dataSize
    }

    def "AVMessage -> Message"() {

    }
}
