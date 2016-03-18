package dvoraka.avservice

import dvoraka.avservice.configuration.AppConfig
import dvoraka.avservice.data.AVMessage
import dvoraka.avservice.rest.RestClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

/**
 * REST testing.
 */
@WebAppConfiguration
@ContextConfiguration(classes = [AppConfig])
@ActiveProfiles("rest")
class RestServiceISpec extends Specification {

    @Autowired
    RestClient client;


    def "get testing message"() {
        setup:
        AVMessage message = client.getTestMessage("/gen-msg")

        expect:
        message != null
        message.getServiceId().equals("testing-service")
    }
}
