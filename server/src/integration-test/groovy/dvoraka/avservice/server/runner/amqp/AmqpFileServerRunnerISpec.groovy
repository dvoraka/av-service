package dvoraka.avservice.server.runner.amqp

import spock.lang.Ignore
import spock.lang.Specification

/**
 * Test for server running.
 */
@Ignore
class AmqpFileServerRunnerISpec extends Specification {

    def "Run AMQP file server runner"() {
        when:
            AmqpFileServerRunner.setTestRun(true)
            AmqpFileServerRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
