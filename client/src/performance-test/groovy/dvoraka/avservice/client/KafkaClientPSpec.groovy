package dvoraka.avservice.client

import dvoraka.avservice.client.configuration.ClientConfig
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * Kafka client performance testing.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'file-client', 'kafka', 'no-db'])
class KafkaClientPSpec extends ClientPSpec {
}
