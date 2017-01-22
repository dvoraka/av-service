package dvoraka.avservice.storage.configuration;

import dvoraka.avservice.storage.service.DummyFileService;
import dvoraka.avservice.storage.service.FileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Storage configuration.
 */
@Configuration
public class StorageConfig {

    @Bean
    FileService fileService() {
        return new DummyFileService();
    }
}
