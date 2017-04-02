package dvoraka.avservice.server.configuration.bridge;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.server.ServerComponentBridge;
import dvoraka.avservice.server.configuration.amqp.AmqpBridgeInputConfig;
import dvoraka.avservice.server.configuration.jms.JmsBridgeOutputConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * AMQP to JMS bridge configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@Profile("amqp2jms")
@Import({
        DatabaseConfig.class,
        AmqpBridgeInputConfig.class,
        JmsBridgeOutputConfig.class
})
public class AmqpToJmsConfig {

    @Bean
    public ServerComponentBridge componentBridge(
            ServerComponent inComponent,
            ServerComponent outComponent
    ) {
        return new ServerComponentBridge(inComponent, outComponent);
    }
}
