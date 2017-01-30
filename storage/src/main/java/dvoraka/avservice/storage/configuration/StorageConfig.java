package dvoraka.avservice.storage.configuration;

import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.db.repository.DbFileRepository;
import dvoraka.avservice.storage.service.DbFileService;
import dvoraka.avservice.storage.service.FileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Storage configuration.
 */
@Configuration
@Import({
        DatabaseConfig.class
})
@Profile("storage")
public class StorageConfig {

    @Bean
    @Profile("db")
    FileService dbFileService(DbFileRepository dbFileRepository) {
        return new DbFileService(dbFileRepository);
    }

//    @Bean
//    @Profile("no-db")
//    public FileService fileService() {
//        return new DummyFileService();
//    }
}
