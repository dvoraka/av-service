package dvoraka.avservice.checker

import dvoraka.avservice.checker.configuration.CheckerConfig
import dvoraka.avservice.server.AvServer
import dvoraka.avservice.server.configuration.AmqpConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Run checker App.
 */
@ContextConfiguration(classes = [CheckerConfig.class, AmqpConfig.class])
@ActiveProfiles(["default", "amqp", "amqp-async"])
class CheckerAppISpec extends Specification {

    @Autowired
    AvServer basicAvServer

    @Autowired
    AvChecker checker


    def setup() {
        if (!basicAvServer.isRunning()) {
            basicAvServer.start()
        }
    }

    def cleanup() {
    }

    def "AvChecker loading"() {
        expect:
        checker != null
    }

    def "check method test"() {
        when:
        checker.check()

        then:
        notThrown(Exception)
    }

    def "main method call"() {
        when:
        CheckerApp.main([] as String[])

        then:
        notThrown(Exception)
    }
}
