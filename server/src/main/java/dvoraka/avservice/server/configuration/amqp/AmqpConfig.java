package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.server.configuration.ServerCommonConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Main AMQP configuration for the import.
 */
@Configuration
@Import({
        ServerCommonConfig.class,
        AmqpServerConfig.class
})
@Profile("amqp")
public class AmqpConfig {
}
