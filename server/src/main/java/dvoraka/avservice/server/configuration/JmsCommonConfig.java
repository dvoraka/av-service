package dvoraka.avservice.server.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * JMS common configuration.
 */
@Configuration
@Import({ServerCommonConfig.class})
@Profile("jms")
public class JmsCommonConfig {
}
