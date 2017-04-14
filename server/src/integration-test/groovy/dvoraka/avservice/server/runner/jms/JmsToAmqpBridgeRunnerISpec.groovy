package dvoraka.avservice.server.runner.jms

import spock.lang.Ignore
import spock.lang.Specification

/**
 * Test for bridge running.
 */
@Ignore
class JmsToAmqpBridgeRunnerISpec extends Specification {

    def "Run JMS to AMQP bridge"() {
        when:
            JmsToAmqpBridgeRunner.setTestRun(true)
            JmsToAmqpBridgeRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
