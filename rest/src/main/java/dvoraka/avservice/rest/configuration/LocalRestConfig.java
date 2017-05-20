package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.core.MessageProcessor;
import dvoraka.avservice.rest.service.LocalRestService;
import dvoraka.avservice.rest.service.RestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Local REST configuration for the import.
 */
@Configuration
@Profile("rest-local")
public class LocalRestConfig {

    @Bean
    public RestService avRestService(MessageProcessor messageProcessor) {
        return new LocalRestService(messageProcessor);
    }
}
