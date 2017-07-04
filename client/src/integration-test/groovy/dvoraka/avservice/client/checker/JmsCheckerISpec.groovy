package dvoraka.avservice.client.checker

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.runner.server.jms.JmsCheckServerRunner
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * JMS AV checker spec.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'jms', 'file-client', 'checker', 'no-db'])
@DirtiesContext
class JmsCheckerISpec extends CheckerISpec {

    def setupSpec() {
        JmsCheckServerRunner.setTestRun(false)
        runner = new JmsCheckServerRunner()
        runner.runAsync()
        sleep(3_000)
    }
}
