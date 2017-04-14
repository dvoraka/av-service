package dvoraka.avservice.server.runner.jms

import spock.lang.Ignore
import spock.lang.Specification

/**
 * Test for server running.
 */
@Ignore
class JmsServerRunnerISpec extends Specification {

    def "Run JMS server"() {
        when:
            JmsServerRunner.setTestRun(true)
            JmsServerRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
