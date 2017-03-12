package dvoraka.avservice.server.configuration.bridge;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.server.ServerComponentBridge;
import dvoraka.avservice.server.configuration.ServerCommonConfig;
import dvoraka.avservice.server.configuration.amqp.AmqpBridgeInputConfig;
import dvoraka.avservice.server.configuration.jms.JmsBridgeOutputConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * AMQP to JMS bridge configuration.
 */
@Configuration
@Import({
        ServerCommonConfig.class,
        AmqpBridgeInputConfig.class,
        JmsBridgeOutputConfig.class
})
@Profile("amqp2jms")
public class AmqpToJmsConfig {

    @Bean
    public ServerComponentBridge componentBridge(
            ServerComponent inComponent,
            ServerComponent outComponent
    ) {
        return new ServerComponentBridge(inComponent, outComponent);
    }
}
