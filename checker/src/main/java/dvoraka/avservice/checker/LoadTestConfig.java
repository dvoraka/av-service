package dvoraka.avservice.checker;

import dvoraka.avservice.checker.receiver.amqp.AmqpReceiver;
import dvoraka.avservice.checker.receiver.Receiver;
import dvoraka.avservice.checker.sender.amqp.AmqpSender;
import dvoraka.avservice.checker.sender.Sender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for the load test.
 */
@Configuration
public class LoadTestConfig {

    @Bean
    public Sender sender() {
        return new AmqpSender(loadTestProperties().getHost(), false, "1");
    }

    @Bean
    public Receiver receiver() {
        return new AmqpReceiver(loadTestProperties().getHost(), false);
    }

    @Bean
    public Tester tester() {
        return new LoadTester(loadTestProperties());
    }

    @Bean
    public LoadTestProperties loadTestProperties() {
        return new BasicProperties();
    }
}
