package dvoraka.avservice.core.configuration;

import dvoraka.avservice.core.FileMessageProcessor;
import dvoraka.avservice.core.MessageProcessor;
import dvoraka.avservice.storage.service.FileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Core storage replication configuration for import.
 */
@Configuration
@Profile("replication")
public class CoreStorageReplicationConfig {

    @Bean
    public MessageProcessor fileMessageProcessor(FileService replicationService) {
        return new FileMessageProcessor(replicationService);
    }
}
