package dvoraka.avservice.client.checker

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.runner.server.jms.JmsFileServerRunner
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * JMS file server checker spec.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'jms', 'file-client', 'checker', 'no-db'])
@DirtiesContext
class JmsFileServerCheckerISpec extends CheckerISpec {

    def setupSpec() {
        JmsFileServerRunner.setTestRun(false)
        runner = new JmsFileServerRunner()
        runner.runAsync()
        sleep(7_000)
    }
}
