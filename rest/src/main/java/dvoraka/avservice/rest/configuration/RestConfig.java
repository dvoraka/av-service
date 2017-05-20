package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.client.configuration.ClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * REST configuration for import.
 */
@Configuration
@Import({
        SecurityRestConfig.class,
        LocalRestConfig.class,
        RemoteRestConfig.class,

        ClientConfig.class
})
public class RestConfig {
}
