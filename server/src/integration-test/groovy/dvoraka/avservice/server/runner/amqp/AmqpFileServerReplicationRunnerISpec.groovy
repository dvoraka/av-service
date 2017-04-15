package dvoraka.avservice.server.runner.amqp

import spock.lang.Ignore
import spock.lang.Specification

/**
 * Test for server running.
 */
@Ignore
class AmqpFileServerReplicationRunnerISpec extends Specification {

    def "Run AMQP file server with replication runner"() {
        when:
            AmqpFileServerReplicationRunner.setTestRun(true)
            AmqpFileServerReplicationRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
