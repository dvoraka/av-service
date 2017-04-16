package dvoraka.avservice.configuration;

import dvoraka.avservice.AvCheckMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.avprogram.configuration.AvProgramConfig;
import dvoraka.avservice.avprogram.service.AvService;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.storage.configuration.StorageConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * Core module main configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@EnableMBeanExport
@Profile("core")
@Import({
        CoreStorageConfig.class,
        CoreStorageReplicationConfig.class,

        AvProgramConfig.class,
        DatabaseConfig.class,
        StorageConfig.class
})
public class CoreConfig {

    @Value("${avservice.cpuCores:2}")
    private Integer cpuCores;

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public MessageProcessor checkMessageProcessor(
            AvService avService,
            MessageInfoService messageInfoService
    ) {
        return new AvCheckMessageProcessor(
                cpuCores,
                serviceId,
                avService,
                messageInfoService);
    }

    @Bean
    @Profile("itest")
    public MBeanExporter mBeanExporter() {
        MBeanExporter exporter = new MBeanExporter();
        exporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);

        return exporter;
    }
}
