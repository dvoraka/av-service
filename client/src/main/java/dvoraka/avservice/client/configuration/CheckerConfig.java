package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.AvNetworkComponent;
import dvoraka.avservice.client.checker.CheckApp;
import dvoraka.avservice.client.checker.Checker;
import dvoraka.avservice.client.checker.PerformanceTester;
import dvoraka.avservice.client.checker.SimpleChecker;
import dvoraka.avservice.common.testing.PerformanceTestProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Checker configuration for the import.
 */
@Configuration
@Profile("checker")
public class CheckerConfig {

    @Bean
    public Checker checker(AvNetworkComponent avNetworkComponent) {
        return new SimpleChecker(avNetworkComponent);
    }

    @Bean
    public PerformanceTester defaultLoadTester(
            Checker checker,
            PerformanceTestProperties testProperties
    ) {
        return new PerformanceTester(checker, testProperties);
    }

    @Bean
    public CheckApp checkApp(Checker checker) {
        return new CheckApp(checker);
    }
}
