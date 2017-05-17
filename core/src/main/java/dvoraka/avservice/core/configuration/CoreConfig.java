package dvoraka.avservice.core.configuration;

import dvoraka.avservice.avprogram.configuration.AvProgramConfig;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.storage.configuration.StorageConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;

import java.util.function.Predicate;

/**
 * Core module main configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@EnableMBeanExport
@Profile("core")
@Import({
        CoreCheckConfig.class,
        CoreStorageConfig.class,
        CoreStorageReplicationConfig.class,

        AvProgramConfig.class,
        DatabaseConfig.class,
        StorageConfig.class
})
public class CoreConfig {

    /**
     * Input filter configuration for an AV check message processor.
     *
     * @return the input filter
     */
    @Bean
    public Predicate<AvMessage> avCheckInputFilter() {
        return (message) -> message.getType() == MessageType.FILE_CHECK
                || message.getType() == MessageType.FILE_SAVE
                || message.getType() == MessageType.FILE_UPDATE;
    }

    /**
     * Special MBeanExporter bean for integration tests.
     *
     * @return MBeanExporter with a different registration strategy
     */
    @Bean
    @Profile("itest")
    public MBeanExporter mbeanExporter() {
        MBeanExporter exporter = new MBeanExporter();
        exporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);

        return exporter;
    }
}
