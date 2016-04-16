package dvoraka.avservice.checker;

import dvoraka.avservice.checker.receiver.AVReceiver;
import dvoraka.avservice.checker.receiver.Receiver;
import dvoraka.avservice.checker.sender.AVSender;
import dvoraka.avservice.checker.sender.Sender;
import dvoraka.avservice.checker.utils.AVUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Spring configuration for the main application.
 */
@Configuration
public class AppConfig {

    @Value("${host:localhost}")
    private String host;

    @Value("${infected:true}")
    private boolean infected;

    @Value("${appid:antivirus}")
    private String appId;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Sender sender() {
        return new AVSender(host);
    }

    @Bean
    public Receiver receiver() {
        return new AVReceiver(host);
    }

    @Bean
    public AVUtils utils() {
        return new AVUtils(sender(), receiver());
    }

    @Bean
    public AVChecker checker() {
        return new AVChecker(sender(), receiver(), infected, appId);
    }
}
