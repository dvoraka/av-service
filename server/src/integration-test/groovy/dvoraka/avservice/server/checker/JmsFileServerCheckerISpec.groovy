package dvoraka.avservice.server.checker

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.server.runner.jms.JmsFileServerRunner
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * JMS file server checker spec.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['itest', 'client', 'jms', 'file-client', 'checker', 'no-db'])
@DirtiesContext
class JmsFileServerCheckerISpec extends SimpleCheckerISpec {

    def setupSpec() {
        JmsFileServerRunner.setTestRun(false)
        runner = new JmsFileServerRunner()
        runner.runAsync()
        sleep(2000) // wait for server
    }
}
