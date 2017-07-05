package dvoraka.avservice.runner.server.kafka

import spock.lang.Specification

/**
 * Test for server runner.
 */
class KafkaCheckServerRunnerISpec extends Specification {

    def "Run Kafka server"() {
        when:
            KafkaCheckServerRunner.setTestRun(true)
            KafkaCheckServerRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
