package dvoraka.avservice.configuration;

import dvoraka.avservice.FileMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.storage.service.FileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Core storage replication Spring configuration.
 */
@Configuration
@Profile("replication")
public class CoreStorageReplicationConfig {

    @Bean
    public MessageProcessor fileMessageProcessor(FileService replicationService) {
        return new FileMessageProcessor(replicationService);
    }
}
