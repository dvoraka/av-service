package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.rest.AmqpRestStrategy;
import dvoraka.avservice.rest.RestStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * REST AMQP configuration.
 */
@Configuration
@Profile("rest-amqp")
public class RestAmqpConfig {

    @Bean
    public RestStrategy restStrategy() {
        return new AmqpRestStrategy(null);
    }
}
