package dvoraka.avservice.storage.configuration;

import dvoraka.avservice.storage.service.DummyFileService;
import dvoraka.avservice.storage.service.FileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Storage dummy configuration for import.
 */
@Configuration
@Profile("no-db")
public class DummyStorageConfig {

    @Bean
    public FileService fileService() {
        return new DummyFileService();
    }
}
