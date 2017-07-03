package dvoraka.avservice.server.configuration.kafka

import dvoraka.avservice.server.AvServer
import dvoraka.avservice.server.configuration.ServerConfig
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
@ActiveProfiles(['core', 'check', 'server', 'kafka', 'no-db'])
class KafkaAvServerConfigISpec extends Specification {

    @Autowired
    AvServer fileServer


    def "test"() {
        expect:
            true
    }
}
