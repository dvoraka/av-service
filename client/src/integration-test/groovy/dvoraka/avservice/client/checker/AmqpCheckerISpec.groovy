package dvoraka.avservice.client.checker

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.runner.server.amqp.AmqpCheckServerRunner
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore

/**
 * AMQP AV checker spec.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'amqp', 'file-client', 'checker', 'no-db'])
@DirtiesContext
@Ignore
class AmqpCheckerISpec extends CheckerISpec {

    def setupSpec() {
        AmqpCheckServerRunner.setTestRun(false)
        runner = new AmqpCheckServerRunner()
        runner.runAsync()
        sleep(2_000)
    }
}
