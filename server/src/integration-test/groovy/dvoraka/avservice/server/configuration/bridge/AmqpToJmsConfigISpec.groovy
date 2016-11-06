package dvoraka.avservice.server.configuration.bridge

import dvoraka.avservice.server.ServerComponentBridge
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [AmqpToJmsConfig.class])
@ActiveProfiles(["amqp2jms", "no-db"])
class AmqpToJmsConfigISpec extends Specification {

    @Autowired
    ServerComponentBridge bridge


    def "test"() {
        expect:
            true
    }
}
