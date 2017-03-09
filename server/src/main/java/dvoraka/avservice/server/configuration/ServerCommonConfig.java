package dvoraka.avservice.server.configuration;

import dvoraka.avservice.common.testing.DefaultPerformanceTestProperties;
import dvoraka.avservice.common.testing.PerformanceTestProperties;
import dvoraka.avservice.configuration.ServiceConfig;
import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.client.service.AvServiceClient;
import dvoraka.avservice.server.client.service.DefaultAvServiceClient;
import dvoraka.avservice.server.client.service.DefaultFileServiceClient;
import dvoraka.avservice.server.client.service.FileServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Server common configuration.
 */
@Configuration
@Import({ServiceConfig.class})
public class ServerCommonConfig {

    @Value("${avservice.perf.msgCount}")
    private long msgCount;


    @Bean
    public PerformanceTestProperties testProperties() {
        return new DefaultPerformanceTestProperties.Builder()
                .msgCount(msgCount)
                .build();
    }

    @Bean
    @Profile("client")
    public AvServiceClient avServiceClient(ServerComponent serverComponent) {
        return new DefaultAvServiceClient(serverComponent);
    }

    @Bean
    @Profile("client")
    public FileServiceClient fileServiceClient(ServerComponent serverComponent) {
        return new DefaultFileServiceClient(serverComponent);
    }
}
