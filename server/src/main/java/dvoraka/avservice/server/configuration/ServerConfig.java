package dvoraka.avservice.server.configuration;

import dvoraka.avservice.configuration.CoreConfig;
import dvoraka.avservice.server.configuration.amqp.AmqpCommonServerConfig;
import dvoraka.avservice.server.configuration.amqp.AmqpServerConfig;
import dvoraka.avservice.server.configuration.jms.JmsServerConfig;
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
        // AMQP
        AmqpServerConfig.class,
        AmqpCommonServerConfig.class,
        // JMS
        JmsServerConfig.class,

        CoreConfig.class,
})
public class ServerConfig {
}
