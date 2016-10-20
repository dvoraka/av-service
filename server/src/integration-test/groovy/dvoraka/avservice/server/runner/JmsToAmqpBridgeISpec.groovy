package dvoraka.avservice.server.runner

import spock.lang.Specification

/**
 * Test for bridge running.
 */
class JmsToAmqpBridgeISpec extends Specification {

    def "Run bridge"() {
        when:
            JmsToAmqpBridge.setTestRun(true)
            JmsToAmqpBridge.main([] as String[])

        then:
            notThrown(Exception)
    }
}
