package dvoraka.avservice.runner.client.amqp

import spock.lang.Specification

/**
 * Test for check runner.
 */
class AmqpCheckRunnerISpec extends Specification {

    def "Run AMQP check runner"() {
        when:
            AmqpCheckRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
