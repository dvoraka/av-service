package dvoraka.avservice.server.runner

import spock.lang.Specification

/**
 * Test for server running.
 */
class JmsServerRunnerISpec extends Specification {

    def "Run JMS server"() {
        when:
            JmsServerRunner.setTestRun(true)
            JmsServerRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
