package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.CompositeMessageProcessor;
import dvoraka.avservice.DefaultMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.ProcessorConfiguration;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.rest.service.AvRestService;
import dvoraka.avservice.rest.service.LocalRestService;
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
    public AvRestService avRestService(
            MessageProcessor restMessageProcessor,
            MessageProcessor checkAndFileProcessor
    ) {
        return new LocalRestService(restMessageProcessor, checkAndFileProcessor);
    }

    @Bean
    public MessageProcessor restMessageProcessor(
            AvService avService,
            MessageInfoService messageInfoService
    ) {
        final int threads = 20;
        return new DefaultMessageProcessor(
                threads,
                "service1",
                avService,
                messageInfoService);
    }

    @Bean
    public MessageProcessor checkAndFileProcessor(
            MessageProcessor restMessageProcessor,
            MessageProcessor fileMessageProcessor
    ) {
        ProcessorConfiguration checkConfig = new ProcessorConfiguration(restMessageProcessor);
        ProcessorConfiguration fileConfig = new ProcessorConfiguration(fileMessageProcessor);

        CompositeMessageProcessor processor = new CompositeMessageProcessor();
        processor.addProcessor(checkConfig);
        processor.addProcessor(fileConfig);

        processor.start();

        return processor;
    }
}
