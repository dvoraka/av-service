package dvoraka.avservice.configuration;

import dvoraka.avservice.avprogram.configuration.AvProgramConfig;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.storage.configuration.StorageConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * AV service Spring configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@Import({
        ServiceCoreConfig.class,
        // AV program
        AvProgramConfig.class,
        // DB
        DatabaseConfig.class,
        // Storage
        StorageConfig.class
})
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class ServiceConfig { //NOSONAR

//    @Bean
//    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//        return new PropertySourcesPlaceholderConfigurer();
//    }
}
