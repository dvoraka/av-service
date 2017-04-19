package dvoraka.avservice.server.runner.amqp

import spock.lang.Ignore
import spock.lang.Specification

/**
 * Test for service runner.
 */
@Ignore
class AmqpReplicationServiceRunnerISpec extends Specification {

    def "Run AMQP replication service runner"() {
        when:
            AmqpReplicationServiceRunner.setTestRun(true)
            AmqpReplicationServiceRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}