package dvoraka.avservice.server.configuration.jms;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Main JMS configuration for clients.
 */
@Configuration
@Import({
        JmsCommonConfig.class,
        JmsServerConfig.class,
        JmsClientConfig.class,
        JmsBridgeInputConfig.class
})
@Profile("jms")
public class JmsConfig {
}
