package dvoraka.avservice.rest.configuration

import dvoraka.avservice.rest.RestClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration spec.
 */
@ContextConfiguration(classes = RestClientConfig.class)
@ActiveProfiles("rest-client")
class RestClientConfigISpec extends Specification {

    @Autowired
    Environment env
    @Autowired
    RestClient restClient


    def "test"() {
        expect:
            true
    }

    def "restUrl field loading"() {
        expect:
            env.getProperty("avservice.rest.url")
    }
}
