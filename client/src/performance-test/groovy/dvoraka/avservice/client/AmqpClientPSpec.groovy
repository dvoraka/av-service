package dvoraka.avservice.client

import dvoraka.avservice.client.configuration.ClientConfig
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * AMQP client performance testing.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'file-client', 'amqp', 'no-db'])
class AmqpClientPSpec extends ClientPSpec {
}
