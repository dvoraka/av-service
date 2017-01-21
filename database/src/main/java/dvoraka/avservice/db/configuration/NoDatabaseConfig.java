package dvoraka.avservice.db.configuration;

import dvoraka.avservice.db.service.DummyFileService;
import dvoraka.avservice.db.service.DummyMessageInfoService;
import dvoraka.avservice.db.service.FileService;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * No database Spring configuration.
 */
@Configuration
@Profile("no-db")
public class NoDatabaseConfig {

    @Bean
    public MessageInfoService messageInfoService() {
        return new DummyMessageInfoService();
    }

    @Bean
    public FileService fileService() {
        return new DummyFileService();
    }
}
