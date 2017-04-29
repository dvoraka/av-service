package dvoraka.avservice.server.runner.amqp

import spock.lang.Specification

/**
 * Test for load test running.
 */
class AmqpLoadTestRunnerISpec extends Specification {

    def setupSpec() {
        System.setProperty('avservice.perf.msgCount', '2')
    }

    def "Run AMQP load test runner"() {
        when:
            AmqpLoadTestRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
