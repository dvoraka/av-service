package dvoraka.avservice.rest.configuration

import dvoraka.avservice.rest.service.RestService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

/**
 * Configuration spec.
 */
@ContextConfiguration(classes = SpringWebConfig.class)
@ActiveProfiles(['rest', 'rest-amqp', 'amqp', 'client', 'file-client', 'db'])
@WebAppConfiguration
@DirtiesContext
class RestAmqpConfigISpec extends Specification {

    @Autowired
    RestService avRestService


    def "test"() {
        expect:
            true
    }
}
