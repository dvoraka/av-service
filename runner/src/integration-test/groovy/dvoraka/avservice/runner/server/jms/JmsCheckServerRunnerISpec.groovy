package dvoraka.avservice.runner.server.jms

import spock.lang.Specification

/**
 * Test for server runner.
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
