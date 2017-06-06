package dvoraka.avservice.storage.configuration;

import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Storage module main configuration.
 */
@Configuration
@Import({
        DummyStorageConfig.class,
        DbStorageConfig.class,
        ReplicationConfig.class,
//        AopConfiguration.class,

        DatabaseConfig.class,
        ClientConfig.class
})
@Profile("storage")
public class StorageConfig {
}
