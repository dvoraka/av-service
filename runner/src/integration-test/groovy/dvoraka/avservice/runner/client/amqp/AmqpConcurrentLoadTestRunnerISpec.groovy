package dvoraka.avservice.runner.client.amqp

import spock.lang.Specification


class AmqpConcurrentLoadTestRunnerISpec extends Specification {

    def setupSpec() {
        System.setProperty('avservice.perf.msgCount', '2')
    }

    def "Run AMQP concurrent load test runner"() {
        when:
            AmqpConcurrentLoadTestRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
