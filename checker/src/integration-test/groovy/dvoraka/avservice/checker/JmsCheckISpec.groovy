package dvoraka.avservice.checker

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.server.AvServer
import dvoraka.avservice.server.configuration.JmsConfig
import dvoraka.avservice.server.jms.JmsClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Temporary JMS infrastructure test.
 */
// TODO: update
@ContextConfiguration(classes = [JmsConfig.class])
@ActiveProfiles(["jms", "jms-client", "jms-server", "database"])
class JmsCheckISpec extends Specification {

    @Autowired
    AvServer basicAvServer
    @Autowired
    JmsClient jmsClient


    def setup() {
        if (!basicAvServer.isRunning()) {
            basicAvServer.start()
        }
    }

    def cleanup() {
    }

    def "send and receive infected AvMessage"() {
        given:
            AvMessage infectedMsg = Utils.genInfectedMessage()

        when:
            jmsClient.sendMessage(infectedMsg, "check")
            AvMessage receivedMsg = jmsClient.receiveMessage("result")

        then:
            receivedMsg
            receivedMsg.getCorrelationId() == infectedMsg.getId()
    }
}
