package dvoraka.avservice.server.configuration;

import dvoraka.avservice.server.AvServer;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.jms.JmsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * JMS configuration.
 */
@Configuration
@Import({JmsCommonConfig.class})
@Profile("jms")
public class JmsConfig {

    @Value("${avservice.serviceId:default1")
    private String serviceId;


    @Bean
    public AvServer avServer() {
        return new BasicAvServer(serviceId);
    }

    @Bean
    public JmsClient jmsClient() {
        return new JmsClient();
    }
}
