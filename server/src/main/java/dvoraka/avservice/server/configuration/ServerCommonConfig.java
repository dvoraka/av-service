package dvoraka.avservice.server.configuration;

import dvoraka.avservice.configuration.ServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Server common configuration.
 */
@Configuration
@Import({ServiceConfig.class})
public class ServerCommonConfig {
}
