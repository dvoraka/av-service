package dvoraka.avservice.storage.configuration;

import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import dvoraka.avservice.common.service.BasicServiceManagement;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.storage.replication.DefaultReplicationService;
import dvoraka.avservice.storage.replication.ReplicationService;
import dvoraka.avservice.storage.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Storage module main configuration.
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

    @Value("${avservice.storage.replication.nodeId}")
    private String nodeId;


    @Bean
    @Profile("replication")
    public ReplicationService replicationService(
            FileService fileService,
            ReplicationServiceClient replicationServiceClient,
            ReplicationResponseClient replicationResponseClient
    ) {
        return new DefaultReplicationService(
                fileService, replicationServiceClient, replicationResponseClient, nodeId);
    }

    @Bean
    @Profile("replication")
    public BasicServiceManagement replicationServiceApp() {
        return new BasicServiceManagement();
    }
}
