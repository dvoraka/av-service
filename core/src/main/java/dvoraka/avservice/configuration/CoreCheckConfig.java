package dvoraka.avservice.configuration;

import dvoraka.avservice.AvCheckMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.avprogram.service.AvService;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
            MessageInfoService messageInfoService
    ) {
        return new AvCheckMessageProcessor(
                cpuCores,
                serviceId,
                avService,
                messageInfoService);
    }
}
