package dvoraka.avservice.server.configuration;

import dvoraka.avservice.server.AvServer;
import dvoraka.avservice.server.BasicAvServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * JMS server configuration.
 */
@Configuration
@Profile("jms-server")
public class JmsServerConfig {

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public AvServer avServer() {
        return new BasicAvServer(serviceId);
    }
}
