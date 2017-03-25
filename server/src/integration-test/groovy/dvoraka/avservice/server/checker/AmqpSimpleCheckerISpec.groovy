package dvoraka.avservice.server.checker

import dvoraka.avservice.server.configuration.amqp.AmqpConfig
import dvoraka.avservice.server.runner.amqp.AmqpServerRunner
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * AMQP checker spec.
 */
@ContextConfiguration(classes = [AmqpConfig.class])
@ActiveProfiles(['client', 'amqp', 'amqp-client', 'amqp-checker', 'no-db'])
class AmqpSimpleCheckerISpec extends SimpleCheckerISpec {

    def setupSpec() {
        AmqpServerRunner.setTestRun(false)
        runner = new AmqpServerRunner()
        runner.runAsync()
    }
}
