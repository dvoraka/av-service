package dvoraka.avservice.runner.client.kafka

import spock.lang.Specification

class KafkaBufferedLoadTestRunnerISpec extends Specification {

    def setupSpec() {
        System.setProperty('avservice.perf.msgCount', '2')
    }

    def "Run Kafka buffered load test runner"() {
        when:
            KafkaBufferedLoadTestRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
