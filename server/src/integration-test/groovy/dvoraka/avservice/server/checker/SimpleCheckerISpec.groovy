package dvoraka.avservice.server.checker

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.server.configuration.amqp.AmqpConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Simple checker spec.
 */
@ContextConfiguration(classes = [AmqpConfig.class])
@ActiveProfiles(['core', 'amqp', 'amqp-checker', 'no-db'])
class SimpleCheckerISpec extends Specification {

    @Autowired
    Checker checker


    def "test"() {
        expect:
            true
    }

    def "send message"() {
        given:
            AvMessage message = Utils.genInfectedMessage()

        when:
            checker.sendMessage(message)

        then:
            notThrown(Exception)
    }
}
