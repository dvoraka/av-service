package dvoraka.avservice.server.runner.jms

import spock.lang.Ignore
import spock.lang.Specification

/**
 * Test for load test running.
 */
@Ignore
class JmsLoadTestRunnerISpec extends Specification {

    def "Run JMS load test runner"() {
        when:
            JmsLoadTestRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
