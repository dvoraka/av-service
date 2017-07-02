package dvoraka.avservice.runner.server.amqp

import spock.lang.Specification

/**
 * Test for server running.
 */
class AmqpFileServerRunnerISpec extends Specification {

    def "Run AMQP file server runner"() {
        when:
            AmqpFileServerRunner.setTestRun(true)
            AmqpFileServerRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
