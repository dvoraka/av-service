package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.server.amqp.AmqpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AMQP client configuration for import.
 */
@Configuration
@Profile("amqp-client")
public class AmqpClientConfig {

    @Value("${avservice.amqp.checkExchange:check}")
    private String checkExchange;


    @Bean
    public AmqpClient amqpClient() {
        return new AmqpClient(checkExchange);
    }
}
