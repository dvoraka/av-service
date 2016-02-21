package dvoraka.avservice

import dvoraka.avservice.server.AVServer
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

    def setup() {
        // stop server
    }

    def "default test"() {
        expect:
        true
    }

    def "AV server loading"() {
        expect:
        avServer != null
    }

    def "status after start"() {
        when:
        avServer.start()

        then:
        avServer.isStarted()
    }

    def "status after stop"() {
        when:
        avServer.stop()

        then:
        avServer.isStopped()
    }
}
