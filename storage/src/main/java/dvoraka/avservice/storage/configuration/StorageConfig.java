package dvoraka.avservice.storage.configuration;

import dvoraka.avservice.db.configuration.DatabaseConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Storage configuration.
 */
@Configuration
@Import({
        DatabaseConfig.class,
        StorageDummyConfig.class,
        StorageDbConfig.class
})
@Profile("storage")
public class StorageConfig {
}
