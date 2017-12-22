package dvoraka.avservice.client.checker

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.runner.configuration.RunnerConfig
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * Kafka AV checker spec.
 */
@ContextConfiguration(classes = [ClientConfig.class, RunnerConfig.class])
@ActiveProfiles(['client', 'kafka', 'file-client', 'checker', 'no-db'])
@DirtiesContext
class KafkaCheckerISpec extends CheckerISpec {

    def setupSpec() {
        runnerConfiguration = kafkaCheckServerConfiguration()
    }
}
