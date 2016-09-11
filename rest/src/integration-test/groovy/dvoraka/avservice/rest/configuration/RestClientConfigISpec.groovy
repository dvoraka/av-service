package dvoraka.avservice.rest.configuration

import dvoraka.avservice.rest.RestClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
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
    RestTemplate restTemplate
    @Autowired
    RestClient restClient


    def "restUrl field loading"() {
        expect:
            env.getProperty("avservice.rest.url") != null
    }

    def "RestTemplate loading"() {
        expect:
            restTemplate != null
    }

    def "RestClient loading"() {
        expect:
            restClient != null
    }
}
