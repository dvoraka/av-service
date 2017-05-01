package dvoraka.avservice.server.checker

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.server.runner.amqp.AmqpServerRunner
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * AMQP checker spec.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['itest', 'client', 'amqp', 'file-client', 'checker', 'no-db'])
@DirtiesContext
class AmqpSimpleCheckerISpec extends SimpleCheckerISpec {

    def setupSpec() {
        AmqpServerRunner.setTestRun(false)
        runner = new AmqpServerRunner()
        runner.runAsync()
    }
}
