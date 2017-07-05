package dvoraka.avservice.runner.client.kafka

import spock.lang.Specification

/**
 * Test for check runner.
 */
class KafkaCheckRunnerISpec extends Specification {

    def "Run Kafka check runner"() {
        when:
            KafkaCheckRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
