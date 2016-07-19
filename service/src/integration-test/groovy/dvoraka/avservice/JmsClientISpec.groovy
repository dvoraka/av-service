package dvoraka.avservice

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.configuration.JmsConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

import javax.jms.TextMessage

/**
 * JMS testing.
 */
@ContextConfiguration(classes = [JmsConfig.class])
@ActiveProfiles("jms")
class JmsClientISpec extends Specification {

    @Autowired
    JmsClient client;


    @Ignore
    def "send test message"() {
        expect:
            client.sendTestMessage()
    }

    @Ignore
    def "send test messages"() {
        expect:
            100.times {
                client.sendTestMessage()
            }
    }

    @Ignore
    def "receive test message"() {
        expect:
            client.receiveTestMessage()
    }

    def "send and receive test message"() {
        given:
            String expectedCorrId = JmsClient.TEST_CORRELATION_ID
            String expectedText = JmsClient.TEST_TEXT

        when:
            client.sendTestMessage()
            Object msg = client.receiveTestMessageAsObject()

            TextMessage message = null
            if (msg instanceof TextMessage) {
                message = (TextMessage) msg
            }
            String corrId = message?.getJMSCorrelationID()
            String msgText = message?.getText()

        then:
            corrId == expectedCorrId
            msgText == expectedText
    }

    @Ignore
    def "send AvMessage"() {
        setup:
            AvMessage message = Utils.genNormalMessage()

        expect:
            client.sendMessage(message, JmsClient.TEST_DESTINATION)
    }

    @Ignore
    def "send 10 AvMessages"() {
        setup:
            AvMessage message = Utils.genNormalMessage()

        expect:
            10.times {
                client.sendMessage(message, JmsClient.TEST_DESTINATION)
            }
    }

    def "send and receive AvMessage"() {
        given:
            AvMessage message = Utils.genNormalMessage()
            String destination = JmsClient.TEST_DESTINATION

        when:
            client.sendMessage(message, destination)
            AvMessage receivedMessage = client.receiveMessage(destination)

        then:
            receivedMessage.getId() == message.getId()
    }
}
