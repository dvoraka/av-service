package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.DefaultMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.rest.LocalRestStrategy;
import dvoraka.avservice.rest.RestStrategy;
import dvoraka.avservice.service.AvService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * REST local configuration.
 */
@Configuration
@Profile("rest-local")
public class RestLocalConfig {

    @Bean
    public RestStrategy restStrategy(MessageProcessor restMessageProcessor) {
        return new LocalRestStrategy(restMessageProcessor);
    }

    @Bean
    public MessageProcessor restMessageProcessor(
            AvService avService, MessageInfoService messageInfoService) {
        final int threads = 20;
        return new DefaultMessageProcessor(
                threads,
                "service1",
                avService,
                messageInfoService);
    }
}
