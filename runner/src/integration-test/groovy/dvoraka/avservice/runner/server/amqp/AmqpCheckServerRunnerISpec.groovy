package dvoraka.avservice.runner.server.amqp

import spock.lang.Specification

/**
 * Test for server runner.
 */
class AmqpCheckServerRunnerISpec extends Specification {

    def "Run AMQP server runner"() {
        when:
            AmqpCheckServerRunner.setTestRun(true)
            AmqpCheckServerRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
