package dvoraka.avservice.server.configuration.bridge;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.server.ServerComponentBridge;
import dvoraka.avservice.server.configuration.amqp.AmqpBridgeOutputConfig;
import dvoraka.avservice.server.configuration.jms.JmsBridgeInputConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * JMS to AMQP bridge configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@Profile("jms2amqp")
@Import({
//        ServerCommonConfig.class,
        DatabaseConfig.class,
        JmsBridgeInputConfig.class,
        AmqpBridgeOutputConfig.class
})
public class JmsToAmqpConfig {

    @Bean
    public ServerComponentBridge componentBridge(
            ServerComponent inComponent,
            ServerComponent outComponent
    ) {
        return new ServerComponentBridge(inComponent, outComponent);
    }
}
