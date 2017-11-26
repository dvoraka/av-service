package dvoraka.avservice.client.checker

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.runner.configuration.RunnerConfig
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * JMS AV checker spec.
 */
@ContextConfiguration(classes = [ClientConfig.class, RunnerConfig.class])
@ActiveProfiles(['client', 'jms', 'file-client', 'checker', 'no-db'])
@DirtiesContext
class JmsCheckerISpec extends CheckerISpec {

    def setupSpec() {
        runnerConfiguration = jmsCheckServerConfiguration()
    }
}
