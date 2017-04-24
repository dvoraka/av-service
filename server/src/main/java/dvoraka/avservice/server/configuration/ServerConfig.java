package dvoraka.avservice.server.configuration;

import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.server.configuration.amqp.AmqpConfig;
import dvoraka.avservice.server.configuration.jms.JmsConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Main server configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@Profile("server")
@Import({
        ClientConfig.class,
        AmqpConfig.class,
        JmsConfig.class
})
public class ServerConfig {
}
