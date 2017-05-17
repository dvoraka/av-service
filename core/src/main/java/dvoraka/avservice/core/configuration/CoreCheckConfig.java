package dvoraka.avservice.core.configuration;

import dvoraka.avservice.avprogram.service.AvService;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.core.AvCheckMessageProcessor;
import dvoraka.avservice.core.MessageProcessor;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.function.Predicate;

/**
 * Core AV check configuration for import.
 */
@Configuration
@Profile("check")
public class CoreCheckConfig {

    @Value("${avservice.cpuCores:2}")
    private Integer cpuCores;
    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public MessageProcessor messageProcessor(
            AvService avService,
            MessageInfoService messageInfoService,
            Predicate<AvMessage> checkInputFilter
    ) {
        MessageProcessor checkProcessor = new AvCheckMessageProcessor(
                cpuCores,
                serviceId,
                avService,
                messageInfoService
        );
        checkProcessor.setInputFilter(checkInputFilter);

        return checkProcessor;
    }
}
