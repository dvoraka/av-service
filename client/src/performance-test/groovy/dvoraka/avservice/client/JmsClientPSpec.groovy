package dvoraka.avservice.client

import dvoraka.avservice.client.configuration.ClientConfig
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * JMS client performance testing.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'file-client', 'jms', 'no-db'])
class JmsClientPSpec extends ClientPSpec {
}
