package dvoraka.avservice.server.configuration

import org.springframework.amqp.core.AmqpAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [EnvironmentConfiguratorConfig.class])
@DirtiesContext
class EnvironmentConfiguratorConfigISpec extends Specification {

    @Autowired
    AmqpAdmin admin


    def "test"() {
        expect:
            true
    }
}
