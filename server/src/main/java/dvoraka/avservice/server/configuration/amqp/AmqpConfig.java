package dvoraka.avservice.server.configuration.amqp;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Main AMQP configuration for clients.
 */
@Configuration
@Import({
        AmqpCommonConfig.class,
        AmqpServerConfig.class,
        AmqpClientConfig.class,
        AmqpCheckerConfig.class
})
@Profile("amqp")
public class AmqpConfig {
}
