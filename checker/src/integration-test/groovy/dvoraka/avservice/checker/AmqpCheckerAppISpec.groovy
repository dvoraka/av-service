package dvoraka.avservice.checker

import dvoraka.avservice.checker.configuration.AmqpCheckerConfig
import dvoraka.avservice.checker.runner.AmqpCheckerApp
import dvoraka.avservice.server.AvServer
import dvoraka.avservice.server.configuration.AmqpConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Run AMQP checker App.
 */
@ContextConfiguration(classes = [AmqpCheckerConfig.class, AmqpConfig.class])
@ActiveProfiles(["amqp", "database"])
class AmqpCheckerAppISpec extends Specification {

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
            AmqpCheckerApp.main([] as String[])

        then:
            notThrown(Exception)
    }
}
