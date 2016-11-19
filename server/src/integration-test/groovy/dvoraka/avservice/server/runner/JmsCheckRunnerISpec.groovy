package dvoraka.avservice.server.runner

import spock.lang.Specification

/**
 * Test for check running.
 */
class JmsCheckRunnerISpec extends Specification {

    def "Run JMS check runner"() {
        when:
            JmsCheckRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
