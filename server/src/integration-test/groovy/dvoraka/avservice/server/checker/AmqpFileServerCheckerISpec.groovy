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
//TODO: sometimes failed and it needs investigation, maybe bad timing
class AmqpFileServerCheckerISpec extends SimpleCheckerISpec {

    def setupSpec() {
        AmqpFileServerRunner.setTestRun(false)
        runner = new AmqpFileServerRunner()
        runner.runAsync()
        sleep(1000) // wait a bit for the server
    }
}
