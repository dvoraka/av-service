package dvoraka.avservice.configuration;

import dvoraka.avservice.DefaultMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.avprogram.AvProgram;
import dvoraka.avservice.avprogram.configuration.AvProgramConfig;
import dvoraka.avservice.common.ReceivingType;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.db.configuration.NoDatabaseConfig;
import dvoraka.avservice.db.configuration.SolrConfig;
import dvoraka.avservice.service.AvService;
import dvoraka.avservice.service.DefaultAvService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * AV service Spring configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@EnableMBeanExport
@Import({
        AvProgramConfig.class,
        DatabaseConfig.class,
        NoDatabaseConfig.class,
        SolrConfig.class
})
public class ServiceConfig {

    @Value("${avservice.cpuCores:2}")
    private Integer cpuCores;

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

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

    @Bean
    public MBeanExporter mbeanExporter() {
        MBeanExporter exporter = new AnnotationMBeanExporter();
        exporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);

        return exporter;
    }
}
