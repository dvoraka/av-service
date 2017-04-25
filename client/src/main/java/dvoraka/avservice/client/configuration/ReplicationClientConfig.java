package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.ReplicationComponent;
import dvoraka.avservice.client.service.DefaultReplicationServiceClient;
import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.DefaultReplicationResponseClient;
import dvoraka.avservice.client.service.response.ReplicationResponseClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Replication client configuration for the import.
 */
@Configuration
@Profile({"replication", "replication-test"})
@Import({
        AmqpReplicationClientConfig.class
})
public class ReplicationClientConfig {

    @Value("${avservice.storage.replication.nodeId}")
    private String nodeId;


    @Bean
    public ReplicationServiceClient replicationServiceClient(
            ReplicationComponent replicationComponent
    ) {
        return new DefaultReplicationServiceClient(replicationComponent, nodeId);
    }

    @Bean
    public ReplicationResponseClient replicationResponseClient(
            ReplicationComponent replicationComponent
    ) {
        return new DefaultReplicationResponseClient(replicationComponent, nodeId);
    }
}
