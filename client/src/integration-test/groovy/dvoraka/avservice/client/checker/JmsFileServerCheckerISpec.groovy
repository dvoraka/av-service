package dvoraka.avservice.client.checker

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.runner.configuration.RunnerConfig
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * JMS file server checker spec.
 */
@ContextConfiguration(classes = [ClientConfig.class, RunnerConfig])
@ActiveProfiles(['client', 'jms', 'file-client', 'checker', 'no-db'])
@DirtiesContext
class JmsFileServerCheckerISpec extends CheckerISpec {

    def setupSpec() {
        runnerConfiguration = jmsFileServerConfiguration()
    }
}
