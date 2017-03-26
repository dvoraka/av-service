package dvoraka.avservice.server.configuration

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [ServerConfig.class])
@DirtiesContext
@ActiveProfiles(['server', 'client', 'amqp', 'amqp-client', 'no-db'])
class ServerConfigAmqpISpec extends Specification {

    @Autowired
    RabbitTemplate rabbitTemplate


    def "test"() {
        expect:
            true
    }
}
