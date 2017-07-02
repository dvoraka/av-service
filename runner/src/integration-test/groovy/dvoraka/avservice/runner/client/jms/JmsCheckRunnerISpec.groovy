package dvoraka.avservice.runner.client.jms

import spock.lang.Specification

/**
 * Test for check runner.
 */
class JmsCheckRunnerISpec extends Specification {

    def "Run JMS check runner"() {
        when:
            JmsCheckRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
