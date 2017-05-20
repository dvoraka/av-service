package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.client.configuration.ClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * REST configuration for import.
 */
@Configuration
@Import({
        RestSecurityConfig.class,
        RestLocalConfig.class,
        // AMQP
        RestAmqpConfig.class,
        // JMS
        RestJmsConfig.class,

        ClientConfig.class
})
public class RestConfig {
}
