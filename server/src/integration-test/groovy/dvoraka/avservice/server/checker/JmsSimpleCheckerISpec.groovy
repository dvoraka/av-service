package dvoraka.avservice.server.checker

import dvoraka.avservice.server.configuration.jms.JmsConfig
import dvoraka.avservice.server.runner.jms.JmsServerRunner
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * JMS checker spec.
 */
@ContextConfiguration(classes = [JmsConfig.class])
@ActiveProfiles(['client', 'jms', 'jms-client', 'jms-checker', 'no-db'])
class JmsSimpleCheckerISpec extends SimpleCheckerISpec {

    def setupSpec() {
        JmsServerRunner.setTestRun(false)
        runner = new JmsServerRunner()
        runner.runAsync()
    }
}
