package dvoraka.avservice.server.checker

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.server.runner.jms.JmsServerRunner
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * JMS checker spec.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'jms', 'file-client', 'checker', 'no-db'])
@DirtiesContext
class JmsSimpleCheckerISpec extends SimpleCheckerISpec {

    def setupSpec() {
        JmsServerRunner.setTestRun(false)
        runner = new JmsServerRunner()
        runner.runAsync()
    }
}
