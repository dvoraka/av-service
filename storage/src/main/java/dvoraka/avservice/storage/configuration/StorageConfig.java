package dvoraka.avservice.storage.configuration;

import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.storage.replication.DefaultReplicationService;
import dvoraka.avservice.storage.replication.ReplicationService;
import dvoraka.avservice.storage.replication.ReplicationServiceApp;
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
        DatabaseConfig.class,
        ClientConfig.class,
        StorageDummyConfig.class,
        StorageDbConfig.class
})
@Profile("storage")
public class StorageConfig {

    @Bean
    @Profile("replication")
    public ReplicationService replicationService(
            FileService fileService,
            ReplicationServiceClient serviceClient,
            ReplicationResponseClient responseClient
    ) {
        return new DefaultReplicationService(fileService, serviceClient, responseClient);
    }

    @Bean
    @Profile("replication")
    public ReplicationServiceApp replicationServiceApp(ReplicationService replicationService) {
        return new ReplicationServiceApp(replicationService);
    }
}
