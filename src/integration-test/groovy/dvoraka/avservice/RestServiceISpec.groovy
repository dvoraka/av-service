package dvoraka.avservice

import dvoraka.avservice.common.Utils
import dvoraka.avservice.configuration.AppConfig
import dvoraka.avservice.data.AVMessage
import dvoraka.avservice.rest.RestClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * REST testing.
 */
@ContextConfiguration(classes = [AppConfig])
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

    def "check normal message"() {
        setup:
        client.postMessage(Utils.genNormalMessage(), "/msg-check")

        expect:
        true
    }

    def "check infected message"() {
        setup:
        client.postMessage(Utils.genInfectedMessage(), "/msg-check")

        expect:
        true
    }
}
