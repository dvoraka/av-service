package dvoraka.avservice.runner.server.amqp

import spock.lang.Specification

/**
 * Test for server running.
 */
class AmqpFileServerReplicationRunnerISpec extends Specification {

    def "Run AMQP file server with replication runner"() {
        when:
            AmqpFileServerReplicationRunner.setTestRun(true)
            AmqpFileServerReplicationRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
