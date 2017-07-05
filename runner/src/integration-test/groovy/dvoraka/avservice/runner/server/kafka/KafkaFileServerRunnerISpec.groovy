package dvoraka.avservice.runner.server.kafka

import spock.lang.Specification

/**
 * Test for server runner.
 */
class KafkaFileServerRunnerISpec extends Specification {

    def "Run Kafka file server"() {
        when:
            KafkaFileServerRunner.setTestRun(true)
            KafkaFileServerRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
