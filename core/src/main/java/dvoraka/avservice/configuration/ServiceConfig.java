package dvoraka.avservice.configuration;

import dvoraka.avservice.avprogram.configuration.AvProgramConfig;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.db.configuration.NoDatabaseConfig;
import dvoraka.avservice.db.configuration.SolrConfig;
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
        ServiceCoreConfig.class,
        AvProgramConfig.class,
        DatabaseConfig.class,
        NoDatabaseConfig.class,
        SolrConfig.class
})
public class ServiceConfig {

    @Bean
    public MBeanExporter mbeanExporter() {
        MBeanExporter exporter = new AnnotationMBeanExporter();
        exporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);

        return exporter;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
