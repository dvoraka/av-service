package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.server.configuration.ServerCommonConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Main JMS configuration for the import.
 */
@Configuration
@Import({
        ServerCommonConfig.class,
        JmsServerConfig.class
})
@Profile("jms")
public class JmsConfig {
}
