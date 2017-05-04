package dvoraka.avservice.client.configuration;

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
        FileClientConfig.class,
        ReplicationClientConfig.class,
        TestReplicationClientConfig.class,
        CheckerConfig.class,

        // Message info service
        DatabaseConfig.class,
})
@PropertySource("classpath:avservice.properties")
public class ClientConfig {

    @Value("${avservice.perf.msgCount}")
    private long msgCount;
    @Value("${avservice.perf.maxRate}")
    private long maxRate;


    @Bean
    public PerformanceTestProperties testProperties() {
        return new DefaultPerformanceTestProperties.Builder()
                .msgCount(msgCount)
                .maxRate(maxRate)
                .build();
    }
}
