package dvoraka.avservice.server.runner.amqp

import spock.lang.Specification

/**
 * Test for load test running.
 */
class AmqpBufferedLoadTestRunnerISpec extends Specification {

    def setupSpec() {
        System.setProperty('avservice.perf.msgCount', '2')
    }

    def "Run AMQP buffered load test runner"() {
        when:
            AmqpBufferedLoadTestRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
