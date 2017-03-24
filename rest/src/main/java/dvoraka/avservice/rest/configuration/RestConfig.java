package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.client.configuration.ClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * REST superset configuration for import.
 */
@Configuration
@Import({
        RestSecurityConfig.class,
        RestLocalConfig.class,
        // AMQP
        RestAmqpConfig.class,
//        AmqpConfig.class,
        // JMS
        RestJmsConfig.class,
//        JmsConfig.class,
        ClientConfig.class
})
public class RestConfig {
}
