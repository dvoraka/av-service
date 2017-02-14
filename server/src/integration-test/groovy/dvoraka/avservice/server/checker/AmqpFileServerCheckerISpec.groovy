package dvoraka.avservice.server.checker

import dvoraka.avservice.server.configuration.amqp.AmqpConfig
import dvoraka.avservice.server.runner.amqp.AmqpFileServerRunner
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * AMQP file server checker spec.
 */
@ContextConfiguration(classes = [AmqpConfig.class])
@ActiveProfiles(['amqp', 'amqp-checker', 'no-db'])
class AmqpFileServerCheckerISpec extends SimpleCheckerISpec {

    def setupSpec() {
        AmqpFileServerRunner.setTestRun(false)
        runner = new AmqpFileServerRunner()
        runner.runAsync()
    }
}
