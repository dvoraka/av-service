package dvoraka.avservice.server.runner.jms

import spock.lang.Specification

/**
 * Test for server running.
 */
class JmsCheckServerRunnerISpec extends Specification {

    def "Run JMS server"() {
        when:
            JmsCheckServerRunner.setTestRun(true)
            JmsCheckServerRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
