package dvoraka.avservice.client.configuration.file;

import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.client.service.DefaultAvServiceClient;
import dvoraka.avservice.client.service.DefaultFileServiceClient;
import dvoraka.avservice.client.service.FileServiceClient;
import dvoraka.avservice.client.service.response.DefaultResponseClient;
import dvoraka.avservice.client.service.response.ResponseClient;
import dvoraka.avservice.client.transport.AvNetworkComponent;
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
    public AvServiceClient avServiceClient(
            AvNetworkComponent avNetworkComponent,
            ResponseClient responseClient
    ) {
        return new DefaultAvServiceClient(avNetworkComponent, responseClient);
    }

    @Bean
    public FileServiceClient fileServiceClient(AvNetworkComponent avNetworkComponent) {
        return new DefaultFileServiceClient(avNetworkComponent);
    }

    @Bean
    public ResponseClient responseClient(
            AvNetworkComponent avNetworkComponent,
            MessageInfoService messageInfoService
    ) {
        return new DefaultResponseClient(avNetworkComponent, messageInfoService);
    }
}
