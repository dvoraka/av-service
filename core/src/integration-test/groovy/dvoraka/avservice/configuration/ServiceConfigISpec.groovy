package dvoraka.avservice.configuration

import dvoraka.avservice.MessageProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [ServiceConfig.class])
@ActiveProfiles(['core', 'no-db'])
class ServiceConfigISpec extends Specification {

    @Autowired
    MessageProcessor messageProcessor


    def "test"() {
        expect:
            true
    }
}
