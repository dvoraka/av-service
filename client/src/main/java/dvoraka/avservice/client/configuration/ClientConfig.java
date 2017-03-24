package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.ReplicationComponent;
import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.client.service.DefaultAvServiceClient;
import dvoraka.avservice.client.service.DefaultFileServiceClient;
import dvoraka.avservice.client.service.DefaultReplicationServiceClient;
import dvoraka.avservice.client.service.FileServiceClient;
import dvoraka.avservice.client.service.ReplicationServiceClient;
import dvoraka.avservice.client.service.response.DefaultResponseClient;
import dvoraka.avservice.client.service.response.ResponseClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Client main configuration.
 */
@Profile("client")
@Import({
        AmqpClient.class,
        JmsClient.class
})
public class ClientConfig {

    @Bean
    public AvServiceClient avServiceClient(ServerComponent serverComponent) {
        return new DefaultAvServiceClient(serverComponent);
    }

    @Bean
    public ReplicationServiceClient replicationServiceClient(
            ReplicationComponent replicationComponent) {
        return new DefaultReplicationServiceClient(replicationComponent);
    }

    @Bean
    public FileServiceClient fileServiceClient(ServerComponent serverComponent) {
        return new DefaultFileServiceClient(serverComponent);
    }

    @Bean
    public ResponseClient responseClient(ServerComponent serverComponent) {
        return new DefaultResponseClient(serverComponent);
    }
}
