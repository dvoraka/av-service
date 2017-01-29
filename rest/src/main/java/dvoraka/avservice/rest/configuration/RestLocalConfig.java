package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.CompositeMessageProcessor;
import dvoraka.avservice.DefaultMessageProcessor;
import dvoraka.avservice.FileMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.ProcessorConfiguration;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.rest.service.AvRestService;
import dvoraka.avservice.rest.service.LocalRestService;
import dvoraka.avservice.service.AvService;
import dvoraka.avservice.storage.service.FileService;
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
        final int threads = 4;
        return new DefaultMessageProcessor(
                threads,
                "service1",
                avService,
                messageInfoService);
    }

    @Bean
    public MessageProcessor restFileMessageProcessor(FileService fileService) {
        return new FileMessageProcessor(fileService);
    }

    @Bean
    public MessageProcessor checkAndFileProcessor(
            MessageProcessor restMessageProcessor,
            MessageProcessor restFileMessageProcessor
    ) {
        ProcessorConfiguration checkConfig = new ProcessorConfiguration(restMessageProcessor);
        ProcessorConfiguration fileConfig = new ProcessorConfiguration(restFileMessageProcessor);

        CompositeMessageProcessor processor = new CompositeMessageProcessor();
        processor.addProcessor(checkConfig);
        processor.addProcessor(fileConfig);

        processor.start();

        return processor;
    }
}
