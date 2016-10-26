package dvoraka.avservice.server.configuration;

import dvoraka.avservice.server.jms.JmsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * JMS client configuration for import.
 */
@Configuration
@Profile("jms-client")
public class JmsClientConfig {

    @Bean
    public JmsClient jmsClient() {
        return new JmsClient();
    }
}
