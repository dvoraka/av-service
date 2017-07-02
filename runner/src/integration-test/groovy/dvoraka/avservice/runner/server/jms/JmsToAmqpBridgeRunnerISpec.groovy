package dvoraka.avservice.runner.server.jms

import spock.lang.Specification

/**
 * Test for bridge runner.
 */
class JmsToAmqpBridgeRunnerISpec extends Specification {

    def "Run JMS to AMQP bridge"() {
        when:
            JmsToAmqpBridgeRunner.setTestRun(true)
            JmsToAmqpBridgeRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
