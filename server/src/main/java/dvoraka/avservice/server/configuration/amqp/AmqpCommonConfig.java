package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.server.configuration.ServerCommonConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * AMQP common configuration.
 */
@Configuration
@Import({ServerCommonConfig.class})
@Profile("amqp")
public class AmqpCommonConfig {
}
