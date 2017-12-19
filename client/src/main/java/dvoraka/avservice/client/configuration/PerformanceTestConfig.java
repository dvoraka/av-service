package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.checker.BufferedPerformanceTester;
import dvoraka.avservice.client.checker.ConcurrentPerformanceTester;
import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.client.service.response.ResponseClient;
import dvoraka.avservice.client.transport.AvNetworkComponent;
import dvoraka.avservice.common.testing.PerformanceTest;
import dvoraka.avservice.common.testing.PerformanceTestProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Performance testing configuration prototype for the import.
 */
@Configuration
@Profile("performance")
public class PerformanceTestConfig {

    @Bean
    @Profile("buffered")
    public BufferedPerformanceTester bufferedPerformanceTester(
            AvServiceClient serviceClient,
            ResponseClient responseClient,
            PerformanceTestProperties testProperties
    ) {
        return new BufferedPerformanceTester(serviceClient, responseClient, testProperties);
    }

    @Bean
    @Profile("concurrent")
    public PerformanceTest concurrentPerformanceTester(
            AvNetworkComponent avNetworkComponent,
            PerformanceTestProperties performanceTestProperties
    ) {
        return new ConcurrentPerformanceTester(avNetworkComponent, performanceTestProperties);
    }
}
