package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.server.configuration.ServerCommonConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Main AMQP configuration for API clients.
 */
@Configuration
@Import({
        ClientConfig.class,
        ServerCommonConfig.class,
        AmqpServerConfig.class,
        AmqpFileServerConfig.class
})
@Profile("amqp")
public class AmqpConfig {
}
