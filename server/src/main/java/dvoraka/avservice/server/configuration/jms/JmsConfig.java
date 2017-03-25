package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.client.configuration.ClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Main JMS configuration for clients.
 */
@Configuration
@Import({
        ClientConfig.class,
        JmsCommonConfig.class,
        JmsServerConfig.class,
        JmsCheckerConfig.class,
        JmsRestConfig.class
})
@Profile("jms")
public class JmsConfig {
}
