package dvoraka.avservice.server.configuration;

import dvoraka.avservice.configuration.CoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Server common configuration.
 */
@Configuration
@Import({
        AmqpServerCommonConfig.class,
        JmsServerCommonConfig.class,

        CoreConfig.class
})
public class ServerCommonConfig {
}
