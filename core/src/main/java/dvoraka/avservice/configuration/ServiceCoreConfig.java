package dvoraka.avservice.configuration;

import dvoraka.avservice.DefaultMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.avprogram.AvProgram;
import dvoraka.avservice.common.ReceivingType;
import dvoraka.avservice.service.AvService;
import dvoraka.avservice.service.DefaultAvService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Core configuration
 */
@Configuration
@Profile("core")
public class ServiceCoreConfig {

    @Value("${avservice.cpuCores:2}")
    private Integer cpuCores;

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public AvService avService(AvProgram avProgram) {
        return new DefaultAvService(avProgram);
    }

    @Bean
    public MessageProcessor messageProcessor() {
        return new DefaultMessageProcessor(
                cpuCores,
                ReceivingType.LISTENER,
                null,
                serviceId);
    }
}
