package dvoraka.avservice.server.runner.jms

import spock.lang.Specification

/**
 * Test for server running.
 */
class JmsFileServerRunnerISpec extends Specification {

    def "Run JMS file server"() {
        when:
            JmsFileServerRunner.setTestRun(true)
            JmsFileServerRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
