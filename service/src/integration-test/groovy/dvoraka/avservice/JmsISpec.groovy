package dvoraka.avservice

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
class JmsISpec extends Specification {

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
}
