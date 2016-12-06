package dvoraka.avservice.configuration;

import dvoraka.avservice.avprogram.configuration.AvProgramConfig;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.db.configuration.NoDatabaseConfig;
import dvoraka.avservice.db.configuration.SolrConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * AV service Spring configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@Import({
        ServiceCoreConfig.class,
        AvProgramConfig.class,
        DatabaseConfig.class,
        NoDatabaseConfig.class,
        SolrConfig.class
})
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class ServiceConfig { //NOSONAR

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
