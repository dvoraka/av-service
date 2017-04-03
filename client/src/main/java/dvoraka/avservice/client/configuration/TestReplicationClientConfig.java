package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.ReplicationComponent;
import dvoraka.avservice.client.service.DefaultReplicationServiceClient;
import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.DefaultReplicationResponseClient;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Replication test client configuration for the import.
 */
@Configuration
@Profile("replication-test")
public class TestReplicationClientConfig {

    @Value("${avservice.storage.replication.testNodeId}")
    private String testNodeId;


    @Bean
    public ReplicationServiceClient replicationServiceClient(
            ReplicationComponent replicationComponent
    ) {
        return new DefaultReplicationServiceClient(replicationComponent, testNodeId);
    }

    @Bean
    public ReplicationResponseClient replicationResponseClient(
            ReplicationComponent replicationComponent
    ) {
        return new DefaultReplicationResponseClient(replicationComponent, testNodeId);
    }
}
