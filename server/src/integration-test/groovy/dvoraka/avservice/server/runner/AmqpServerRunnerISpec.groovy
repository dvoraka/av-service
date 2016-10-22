package dvoraka.avservice.server.runner

import spock.lang.Specification

/**
 * Test for server running.
 */
class AmqpServerRunnerISpec extends Specification {

    def "Run AMQP server runner"() {
        when:
            AmqpServerRunner.setTestRun(true)
            AmqpServerRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
