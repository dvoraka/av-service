package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.client.service.DefaultAvServiceClient;
import dvoraka.avservice.client.service.DefaultFileServiceClient;
import dvoraka.avservice.client.service.FileServiceClient;
import dvoraka.avservice.client.service.response.DefaultResponseClient;
import dvoraka.avservice.client.service.response.ResponseClient;
import dvoraka.avservice.common.testing.DefaultPerformanceTestProperties;
import dvoraka.avservice.common.testing.PerformanceTestProperties;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Client module main configuration.
 */
@Configuration
@Profile("client")
@Import({
        // Message info service
        DatabaseConfig.class,
        // AMQP
        AmqpClientConfig.class,
        AmqpReplicationClientConfig.class,
        AmqpCommonConfig.class,
        TestAmqpReplicationClientConfig.class,
        // JMS
        JmsClient.class,
        JmsCommonConfig.class,
        // Checker
        CheckerConfig.class,
        // Replication
        ReplicationClientConfig.class,
        TestReplicationClientConfig.class
})
@PropertySource("classpath:avservice.properties")
public class ClientConfig {

    @Value("${avservice.perf.msgCount}")
    private long msgCount;
    @Value("${avservice.perf.maxRate}")
    private long maxRate;


    @Bean
    public AvServiceClient avServiceClient(ServerComponent serverComponent) {
        return new DefaultAvServiceClient(serverComponent);
    }

    @Bean
    public FileServiceClient fileServiceClient(ServerComponent serverComponent) {
        return new DefaultFileServiceClient(serverComponent);
    }

    @Bean
    public ResponseClient responseClient(ServerComponent serverComponent) {
        return new DefaultResponseClient(serverComponent);
    }

    @Bean
    public PerformanceTestProperties testProperties() {
        return new DefaultPerformanceTestProperties.Builder()
                .msgCount(msgCount)
                .maxRate(maxRate)
                .build();
    }
}
