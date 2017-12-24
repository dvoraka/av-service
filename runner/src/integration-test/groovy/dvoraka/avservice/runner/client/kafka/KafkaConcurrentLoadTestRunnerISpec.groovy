package dvoraka.avservice.runner.client.kafka

import spock.lang.Specification

class KafkaConcurrentLoadTestRunnerISpec extends Specification {

    def setupSpec() {
        System.setProperty('avservice.perf.msgCount', '2')
    }

    def "Run Kafka concurrent load test runner"() {
        when:
            KafkaConcurrentLoadTestRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
