package dvoraka.avservice.server.configuration;

import dvoraka.avservice.server.jms.JmsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Main JMS configuration for clients.
 */
@Configuration
@Import({
        JmsCommonConfig.class,
        JmsServerConfig.class
})
@Profile("jms")
public class JmsConfig {

    @Bean
    public JmsClient jmsClient() {
        return new JmsClient();
    }
}
