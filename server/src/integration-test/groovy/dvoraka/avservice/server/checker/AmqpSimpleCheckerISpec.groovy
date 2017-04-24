package dvoraka.avservice.server.checker

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.server.runner.amqp.AmqpFileServerRunner
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * AMQP checker spec.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'amqp', 'file-client', 'checker', 'no-db'])
@DirtiesContext
class AmqpSimpleCheckerISpec extends SimpleCheckerISpec {

    def setupSpec() {
        AmqpFileServerRunner.setTestRun(false)
        runner = new AmqpFileServerRunner()
        runner.runAsync()
    }
}
