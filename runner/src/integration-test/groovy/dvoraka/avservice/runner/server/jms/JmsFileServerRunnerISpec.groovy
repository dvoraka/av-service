package dvoraka.avservice.runner.server.jms

import spock.lang.Specification

/**
 * Test for server runner.
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
