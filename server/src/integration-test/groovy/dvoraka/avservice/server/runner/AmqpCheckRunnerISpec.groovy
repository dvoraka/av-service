package dvoraka.avservice.server.runner

import spock.lang.Specification

/**
 * Test for check running.
 */
class AmqpCheckRunnerISpec extends Specification {

    def "Run AMQP check runner"() {
        when:
            AmqpCheckRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
