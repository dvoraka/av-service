package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.ServerAdapter;
import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.client.service.DefaultAvServiceClient;
import dvoraka.avservice.client.service.DefaultFileServiceClient;
import dvoraka.avservice.client.service.FileServiceClient;
import dvoraka.avservice.client.service.response.DefaultResponseClient;
import dvoraka.avservice.client.service.response.ResponseClient;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * File client configuration for the import.
 */
@Configuration
@Profile("file-client")
@Import({
        // AMQP
        AmqpFileClientConfig.class,
        AmqpFileCommonConfig.class,
        // JMS
        JmsFileClientConfig.class,
        JmsFileCommonConfig.class,
        // Kafka
        KafkaFileClientConfig.class
})
public class FileClientConfig {

    @Bean
    public AvServiceClient avServiceClient(ServerAdapter serverAdapter) {
        return new DefaultAvServiceClient(serverAdapter);
    }

    @Bean
    public FileServiceClient fileServiceClient(ServerAdapter serverAdapter) {
        return new DefaultFileServiceClient(serverAdapter);
    }

    @Bean
    public ResponseClient responseClient(
            ServerAdapter serverAdapter,
            MessageInfoService messageInfoService
    ) {
        return new DefaultResponseClient(serverAdapter, messageInfoService);
    }
}
