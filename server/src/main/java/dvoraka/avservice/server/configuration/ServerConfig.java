package dvoraka.avservice.server.configuration;

import dvoraka.avservice.server.configuration.amqp.AmqpConfig;
import dvoraka.avservice.server.configuration.jms.JmsConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Server main configuration.
 */
@Configuration
@Profile("server")
@Import({
        AmqpConfig.class,
        JmsConfig.class
})
public class ServerConfig {
}
