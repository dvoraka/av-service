package dvoraka.avservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Specification class for AVPServer.
 */
@ContextConfiguration(classes = [AppConfig])
class AVServerSpec extends Specification {

    @Autowired
    AVServer avServer

    def "default test"() {
        expect:
        true
    }

    def "AV server loading"() {
        expect:
        avServer != null
    }
}
