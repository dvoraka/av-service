package dvoraka.avservice.configuration

import dvoraka.avservice.MessageProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [CoreConfig.class])
@ActiveProfiles(['core', 'check', 'no-db'])
@DirtiesContext
class CoreConfigISpec extends Specification {

    @Autowired
    MessageProcessor messageProcessor


    def "test"() {
        expect:
            true
    }
}
