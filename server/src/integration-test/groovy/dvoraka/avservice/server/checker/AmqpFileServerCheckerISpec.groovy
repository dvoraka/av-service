package dvoraka.avservice.server.checker

import dvoraka.avservice.server.configuration.amqp.AmqpConfig
import dvoraka.avservice.server.runner.amqp.AmqpFileServerRunner
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore

/**
 * AMQP file server checker spec.
 */
@ContextConfiguration(classes = [AmqpConfig.class])
@ActiveProfiles(['client', 'amqp', 'amqp-checker', 'no-db'])
@Ignore("broken")
class AmqpFileServerCheckerISpec extends SimpleCheckerISpec {

    def setupSpec() {
        AmqpFileServerRunner.setTestRun(false)
        runner = new AmqpFileServerRunner()
        runner.runAsync()
        sleep(1000) // wait a bit for the server
    }
}
