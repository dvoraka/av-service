package dvoraka.avservice.checker.configuration;

import dvoraka.avservice.checker.AvChecker;
import dvoraka.avservice.checker.receiver.amqp.AmqpReceiver;
import dvoraka.avservice.checker.receiver.AvReceiver;
import dvoraka.avservice.checker.sender.amqp.AmqpSender;
import dvoraka.avservice.checker.sender.AvSender;
import dvoraka.avservice.checker.utils.AvUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Spring configuration for the main application.
 */
@Configuration
public class AmqpCheckerConfig {

    @Value("${host:localhost}")
    private String host;

    @Value("${infected:true}")
    private boolean infected;

    @Value("${appid:antivirus}")
    private String appId;

    @Value("${receiveTimeout:200}")
    private long receiveTimeout;


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public AvSender sender() {
        return new AmqpSender(host);
    }

    @Bean
    public AvReceiver receiver() {
        AvReceiver receiver = new AmqpReceiver(host);
        receiver.setReceiveTimeout(receiveTimeout);

        return receiver;
    }

    @Bean
    public AvUtils utils() {
        return new AvUtils();
    }

    @Bean
    public AvChecker checker() {
        return new AvChecker(infected, appId);
    }
}
