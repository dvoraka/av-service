package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.client.service.DefaultAvServiceClient;
import dvoraka.avservice.client.service.DefaultFileServiceClient;
import dvoraka.avservice.client.service.FileServiceClient;
import dvoraka.avservice.client.service.response.DefaultResponseClient;
import dvoraka.avservice.client.service.response.ResponseClient;
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
        AmqpFileCommonConfig.class
})
public class FileClientConfig {

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
}
