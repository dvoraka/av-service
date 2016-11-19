package dvoraka.avservice.checker.configuration;

import dvoraka.avservice.checker.LoadTester;
import dvoraka.avservice.checker.Tester;
import dvoraka.avservice.checker.receiver.AvReceiver;
import dvoraka.avservice.checker.receiver.amqp.AmqpReceiver;
import dvoraka.avservice.checker.sender.AvSender;
import dvoraka.avservice.checker.sender.amqp.AmqpSender;
import dvoraka.avservice.common.testing.DefaultLoadTestProperties;
import dvoraka.avservice.common.testing.LoadTestProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for the load test.
 */
@Configuration
public class LoadTestConfig {

    @Bean
    public AvSender sender() {
        return new AmqpSender(loadTestProperties().getHost(), false, "1");
    }

    @Bean
    public AvReceiver receiver() {
        return new AmqpReceiver(loadTestProperties().getHost(), false);
    }

    @Bean
    public Tester tester() {
        return new LoadTester(loadTestProperties());
    }

    @Bean
    public LoadTestProperties loadTestProperties() {
        return new DefaultLoadTestProperties();
    }
}
